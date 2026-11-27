package ec.edu.cafeteria.productos.servicio;

import ec.edu.cafeteria.comun.excepcion.AccesoDenegadoException;
import ec.edu.cafeteria.comun.excepcion.ConflictoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.comun.excepcion.RecursoNoEncontradoException;
import ec.edu.cafeteria.productos.dto.RegistroProductoRequest;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.productos.repositorio.ProductoRepository;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link ProductoService}. El repositorio y la autenticación se
 * simulan con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    private static final String CODIGO = "PRD-CAFE01";
    private static final ContextoAutenticado PERSONAL =
            new ContextoAutenticado("personal@uni.edu.ec", RolUsuario.PERSONAL);
    private static final ContextoAutenticado CLIENTE =
            new ContextoAutenticado("cliente@uni.edu.ec", RolUsuario.CLIENTE);

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AutenticacionService autenticacionService;

    @InjectMocks
    private ProductoService productoService;

    private Producto crearProducto(String codigo, String nombre, EstadoProducto estado) {
        return new Producto(codigo, nombre, new BigDecimal("1.50"), estado);
    }

    private void rechazarRolPersonal(ContextoAutenticado contexto) {
        when(autenticacionService.exigirRol(contexto, RolUsuario.PERSONAL))
                .thenThrow(new AccesoDenegadoException("Acceso denegado"));
    }

    @Test
    void obtenerMenu_devuelveSoloProductosDisponibles() {
        List<Producto> disponibles =
                List.of(crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE));
        when(productoRepository.buscarDisponibles()).thenReturn(disponibles);

        List<Producto> menu = productoService.obtenerMenu();

        assertEquals(disponibles, menu);
        verify(productoRepository, never()).buscarTodos();
        verifyNoInteractions(autenticacionService);
    }

    @Test
    void listarTodos_devuelveTodosLosProductosParaElPersonal() {
        List<Producto> todos = List.of(
                crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE),
                crearProducto("PRD-TE0001", "Té", EstadoProducto.NO_DISPONIBLE));
        when(productoRepository.buscarTodos()).thenReturn(todos);

        List<Producto> resultado = productoService.listarTodos(PERSONAL);

        assertEquals(todos, resultado);
        verify(autenticacionService).exigirRol(PERSONAL, RolUsuario.PERSONAL);
    }

    @Test
    void listarTodos_rechazaSiNoEsPersonal() {
        rechazarRolPersonal(CLIENTE);

        assertThrows(AccesoDenegadoException.class, () -> productoService.listarTodos(CLIENTE));
        verify(productoRepository, never()).buscarTodos();
    }

    @Test
    void registrar_guardaElProductoConCodigoGeneradoYDisponiblePorDefecto() {
        RegistroProductoRequest datos =
                new RegistroProductoRequest("  Café  ", new BigDecimal("1.50"), null);
        when(productoRepository.existeNombre("Café")).thenReturn(false);
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Producto registrado = productoService.registrar(PERSONAL, datos);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        Producto guardado = captor.getValue();
        assertSame(guardado, registrado);
        assertTrue(guardado.getCodigo().matches("PRD-[A-Z0-9]{6}"));
        assertEquals("Café", guardado.getNombre());
        assertEquals(new BigDecimal("1.50"), guardado.getPrecio());
        assertEquals(EstadoProducto.DISPONIBLE, guardado.getEstado());
    }

    @Test
    void registrar_respetaElEstadoIndicadoPorElPersonal() {
        RegistroProductoRequest datos = new RegistroProductoRequest(
                "Té", new BigDecimal("0.80"), EstadoProducto.NO_DISPONIBLE);
        when(productoRepository.existeNombre("Té")).thenReturn(false);
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Producto registrado = productoService.registrar(PERSONAL, datos);

        assertEquals(EstadoProducto.NO_DISPONIBLE, registrado.getEstado());
    }

    @Test
    void registrar_rechazaPrecioNoPositivo() {
        RegistroProductoRequest datos =
                new RegistroProductoRequest("Café", BigDecimal.ZERO, null);

        assertThrows(NegocioException.class, () -> productoService.registrar(PERSONAL, datos));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void registrar_rechazaNombreDuplicado() {
        RegistroProductoRequest datos =
                new RegistroProductoRequest("Café", new BigDecimal("1.50"), null);
        when(productoRepository.existeNombre("Café")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> productoService.registrar(PERSONAL, datos));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void registrar_rechazaSiNoEsPersonal() {
        RegistroProductoRequest datos =
                new RegistroProductoRequest("Café", new BigDecimal("1.50"), null);
        rechazarRolPersonal(CLIENTE);

        assertThrows(AccesoDenegadoException.class,
                () -> productoService.registrar(CLIENTE, datos));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizarPrecio_cambiaElPrecioDelProducto() {
        Producto producto = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        BigDecimal nuevoPrecio = new BigDecimal("2.00");
        when(productoRepository.buscarPorCodigo(CODIGO)).thenReturn(Optional.of(producto));

        productoService.actualizarPrecio(PERSONAL, CODIGO, nuevoPrecio);

        assertEquals(nuevoPrecio, producto.getPrecio());
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizarPrecio_rechazaPrecioNoPositivo() {
        BigDecimal precioNegativo = new BigDecimal("-1.00");

        assertThrows(NegocioException.class,
                () -> productoService.actualizarPrecio(PERSONAL, CODIGO, precioNegativo));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizarPrecio_rechazaSiNoEsPersonal() {
        BigDecimal nuevoPrecio = new BigDecimal("2.00");
        rechazarRolPersonal(CLIENTE);

        assertThrows(AccesoDenegadoException.class,
                () -> productoService.actualizarPrecio(CLIENTE, CODIGO, nuevoPrecio));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizarDisponibilidad_marcaNoDisponibleSinBorrarElProducto() {
        Producto producto = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        when(productoRepository.buscarPorCodigo(CODIGO)).thenReturn(Optional.of(producto));

        productoService.actualizarDisponibilidad(PERSONAL, CODIGO, EstadoProducto.NO_DISPONIBLE);

        assertFalse(producto.estaDisponible());
        verify(productoRepository).save(producto);
        verify(productoRepository, never()).delete(any(Producto.class));
    }

    @Test
    void actualizarDisponibilidad_permiteVolverAHabilitarElProducto() {
        Producto producto = crearProducto(CODIGO, "Café", EstadoProducto.NO_DISPONIBLE);
        when(productoRepository.buscarPorCodigo(CODIGO)).thenReturn(Optional.of(producto));

        productoService.actualizarDisponibilidad(PERSONAL, CODIGO, EstadoProducto.DISPONIBLE);

        assertTrue(producto.estaDisponible());
        verify(productoRepository).save(producto);
    }

    @Test
    void buscarPorCodigo_devuelveElProducto() {
        Producto producto = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        when(productoRepository.buscarPorCodigo(CODIGO)).thenReturn(Optional.of(producto));

        Producto encontrado = productoService.buscarPorCodigo(CODIGO);

        assertSame(producto, encontrado);
    }

    @Test
    void buscarPorCodigo_rechazaCodigoInexistente() {
        when(productoRepository.buscarPorCodigo("PRD-NOEXIS")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.buscarPorCodigo("PRD-NOEXIS"));
    }

    @Test
    void estaDisponible_devuelveElEstadoDeDisponibilidad() {
        Producto disponible = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        Producto noDisponible = crearProducto("PRD-TE0001", "Té", EstadoProducto.NO_DISPONIBLE);
        when(productoRepository.buscarPorCodigo(CODIGO)).thenReturn(Optional.of(disponible));
        when(productoRepository.buscarPorCodigo("PRD-TE0001"))
                .thenReturn(Optional.of(noDisponible));

        assertTrue(productoService.estaDisponible(CODIGO));
        assertFalse(productoService.estaDisponible("PRD-TE0001"));
    }

    @Test
    void obtenerPrecioVigente_devuelveElPrecioActual() {
        Producto producto = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        producto.cambiarPrecio(new BigDecimal("1.75"));
        when(productoRepository.buscarPorCodigo(CODIGO)).thenReturn(Optional.of(producto));

        BigDecimal precio = productoService.obtenerPrecioVigente(CODIGO);

        assertEquals(new BigDecimal("1.75"), precio);
    }
}
