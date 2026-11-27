package ec.edu.cafeteria.productos.controlador;

import ec.edu.cafeteria.productos.dto.ActualizarDisponibilidadRequest;
import ec.edu.cafeteria.productos.dto.ActualizarPrecioRequest;
import ec.edu.cafeteria.productos.dto.ProductoResponse;
import ec.edu.cafeteria.productos.dto.RegistroProductoRequest;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.productos.servicio.ProductoService;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link ProductoController}. El servicio se simula con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private static final String CODIGO = "PRD-CAFE01";
    private static final ContextoAutenticado PERSONAL =
            new ContextoAutenticado("personal@uni.edu.ec", RolUsuario.PERSONAL);

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private Producto crearProducto(String codigo, String nombre, EstadoProducto estado) {
        return new Producto(codigo, nombre, new BigDecimal("1.50"), estado);
    }

    @Test
    void consultarMenu_devuelveLosProductosDisponibles() {
        Producto cafe = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        when(productoService.obtenerMenu()).thenReturn(List.of(cafe));

        List<ProductoResponse> menu = productoController.consultarMenu();

        assertEquals(List.of(ProductoResponse.desde(cafe)), menu);
    }

    @Test
    void listarProductos_devuelveTodosLosProductos() {
        Producto cafe = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        Producto te = crearProducto("PRD-TE0001", "Té", EstadoProducto.NO_DISPONIBLE);
        when(productoService.listarTodos(PERSONAL)).thenReturn(List.of(cafe, te));

        List<ProductoResponse> productos = productoController.listarProductos(PERSONAL);

        assertEquals(List.of(ProductoResponse.desde(cafe), ProductoResponse.desde(te)),
                productos);
    }

    @Test
    void registrarProducto_devuelveElProductoRegistrado() {
        RegistroProductoRequest datos =
                new RegistroProductoRequest("Café", new BigDecimal("1.50"), null);
        Producto cafe = crearProducto(CODIGO, "Café", EstadoProducto.DISPONIBLE);
        when(productoService.registrar(PERSONAL, datos)).thenReturn(cafe);

        ProductoResponse respuesta = productoController.registrarProducto(PERSONAL, datos);

        assertEquals(new ProductoResponse(CODIGO, "Café", new BigDecimal("1.50"),
                EstadoProducto.DISPONIBLE), respuesta);
    }

    @Test
    void actualizarPrecio_delegaEnElServicio() {
        BigDecimal nuevoPrecio = new BigDecimal("2.00");

        productoController.actualizarPrecio(PERSONAL, CODIGO,
                new ActualizarPrecioRequest(nuevoPrecio));

        verify(productoService).actualizarPrecio(PERSONAL, CODIGO, nuevoPrecio);
    }

    @Test
    void actualizarDisponibilidad_delegaEnElServicio() {
        productoController.actualizarDisponibilidad(PERSONAL, CODIGO,
                new ActualizarDisponibilidadRequest(EstadoProducto.NO_DISPONIBLE));

        verify(productoService)
                .actualizarDisponibilidad(PERSONAL, CODIGO, EstadoProducto.NO_DISPONIBLE);
    }
}
