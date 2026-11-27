package ec.edu.cafeteria.pedidos.servicio;

import ec.edu.cafeteria.comun.excepcion.AccesoDenegadoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.comun.excepcion.NoAutenticadoException;
import ec.edu.cafeteria.comun.excepcion.RecursoNoEncontradoException;
import ec.edu.cafeteria.pedidos.dto.SeleccionProductoRequest;
import ec.edu.cafeteria.pedidos.entidad.DetallePedido;
import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.entidad.Pedido;
import ec.edu.cafeteria.pedidos.repositorio.PedidoRepository;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.productos.servicio.ProductoService;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link PedidoService}. Las dependencias externas se simulan con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    private static final ContextoAutenticado CONTEXTO_CLIENTE =
            new ContextoAutenticado("cliente@epn.edu.ec", RolUsuario.CLIENTE);
    private static final ContextoAutenticado CONTEXTO_OTRO_CLIENTE =
            new ContextoAutenticado("otro@epn.edu.ec", RolUsuario.CLIENTE);
    private static final ContextoAutenticado CONTEXTO_PERSONAL =
            new ContextoAutenticado("personal@epn.edu.ec", RolUsuario.PERSONAL);
    private static final ContextoAutenticado CONTEXTO_ADMIN =
            new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private AutenticacionService autenticacionService;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente crearCliente(String codigo) {
        Cliente cliente = new Cliente();
        cliente.setCodigo(codigo);
        return cliente;
    }

    private Personal crearPersonal() {
        return new Personal();
    }

    private Producto crearProducto(String codigo, String nombre, String precio, EstadoProducto estado) {
        return new Producto(codigo, nombre, new BigDecimal(precio), estado);
    }

    @Test
    void crearDetalleSeleccion_devuelveElDetalleConElPrecioVigente() {
        Producto cafe = crearProducto("PRD-CAFE01", "Café", "1.50", EstadoProducto.DISPONIBLE);
        when(productoService.buscarPorCodigo("PRD-CAFE01")).thenReturn(cafe);

        DetallePedido detalle = pedidoService.crearDetalleSeleccion("PRD-CAFE01", 2);

        assertEquals(cafe, detalle.getProducto());
        assertEquals(2, detalle.getCantidad());
        assertEquals(0, new BigDecimal("1.50").compareTo(detalle.getPrecioUnitario()));
        assertEquals(0, new BigDecimal("3.00").compareTo(detalle.calcularSubtotal()));
    }

    @Test
    void crearDetalleSeleccion_rechazaCantidadNoPositiva() {
        assertThrows(NegocioException.class, () -> pedidoService.crearDetalleSeleccion("PRD-CAFE01", 0));
        assertThrows(NegocioException.class, () -> pedidoService.crearDetalleSeleccion("PRD-CAFE01", -3));
    }

    @Test
    void crearDetalleSeleccion_rechazaProductoNoDisponible() {
        Producto agotado = crearProducto("PRD-AGOT01", "Croissant", "1.80", EstadoProducto.NO_DISPONIBLE);
        when(productoService.buscarPorCodigo("PRD-AGOT01")).thenReturn(agotado);

        assertThrows(NegocioException.class, () -> pedidoService.crearDetalleSeleccion("PRD-AGOT01", 1));
    }

    @Test
    void calcularImporteTotal_sumaLosSubtotales() {
        Producto cafe = crearProducto("PRD-1", "Café", "1.50", EstadoProducto.DISPONIBLE);
        Producto te = crearProducto("PRD-2", "Té", "1.25", EstadoProducto.DISPONIBLE);
        List<DetallePedido> detalles = List.of(
                new DetallePedido(cafe, 2, new BigDecimal("1.50")),
                new DetallePedido(te, 3, new BigDecimal("1.25"))
        );

        BigDecimal total = pedidoService.calcularImporteTotal(detalles);

        assertEquals(0, new BigDecimal("6.75").compareTo(total));
    }

    @Test
    void calcularImporteTotal_devuelveCeroSinDetalles() {
        assertEquals(0, BigDecimal.ZERO.compareTo(pedidoService.calcularImporteTotal(List.of())));
        assertEquals(0, BigDecimal.ZERO.compareTo(pedidoService.calcularImporteTotal(null)));
    }

    @Test
    void confirmarPedido_guardaElPedidoPendienteConCodigoDeRetiro() {
        Cliente cliente = crearCliente("CLI-0001");
        Producto cafe = crearProducto("PRD-CAFE01", "Café", "1.50", EstadoProducto.DISPONIBLE);
        DetallePedido seleccion = new DetallePedido(cafe, 2, new BigDecimal("1.50"));

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(productoService.buscarPorCodigo("PRD-CAFE01")).thenReturn(cafe);
        when(pedidoRepository.existeCodigoRetiro(anyString())).thenReturn(false);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido confirmado = pedidoService.confirmarPedido(CONTEXTO_CLIENTE, List.of(seleccion));

        assertEquals(EstadoPedido.PENDIENTE, confirmado.getEstado());
        assertNotNull(confirmado.getCodigoRetiro());
        assertTrue(confirmado.getCodigoRetiro().startsWith("PED-"));
        assertEquals(0, new BigDecimal("3.00").compareTo(confirmado.getTotal()));
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void confirmarPedido_asociaElPedidoAlClienteAutenticado() {
        Cliente cliente = crearCliente("CLI-0099");
        Producto cafe = crearProducto("PRD-CAFE01", "Café", "1.50", EstadoProducto.DISPONIBLE);
        DetallePedido seleccion = new DetallePedido(cafe, 1, new BigDecimal("1.50"));

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(productoService.buscarPorCodigo("PRD-CAFE01")).thenReturn(cafe);
        when(pedidoRepository.existeCodigoRetiro(anyString())).thenReturn(false);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido confirmado = pedidoService.confirmarPedido(CONTEXTO_CLIENTE, List.of(seleccion));

        assertEquals(cliente, confirmado.getCliente());
        assertEquals("CLI-0099", confirmado.getCliente().obtenerCodigo());
    }

    @Test
    void confirmarPedido_rechazaSeleccionVacia() {
        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(crearCliente("CLI-0001"));

        assertThrows(NegocioException.class, () -> pedidoService.confirmarPedido(CONTEXTO_CLIENTE, List.of()));
        assertThrows(NegocioException.class, () -> pedidoService.confirmarPedido(CONTEXTO_CLIENTE, null));
    }

    @Test
    void confirmarPedido_rechazaSiNoEsCliente() {
        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_PERSONAL))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class, () -> pedidoService.confirmarPedido(CONTEXTO_PERSONAL, List.of()));
    }

    @Test
    void confirmarPedido_guardaElPrecioUnitarioDelMomentoDeLaConfirmacion() {
        Cliente cliente = crearCliente("CLI-0001");
        Producto cafe = crearProducto("PRD-CAFE01", "Café", "2.00", EstadoProducto.DISPONIBLE);
        DetallePedido seleccion = new DetallePedido(cafe, 2, new BigDecimal("1.50"));

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(productoService.buscarPorCodigo("PRD-CAFE01")).thenReturn(cafe);
        when(pedidoRepository.existeCodigoRetiro(anyString())).thenReturn(false);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido confirmado = pedidoService.confirmarPedido(CONTEXTO_CLIENTE, List.of(seleccion));
        cafe.cambiarPrecio(new BigDecimal("3.50"));

        assertEquals(0, new BigDecimal("2.00").compareTo(confirmado.getDetalles().get(0).getPrecioUnitario()));
        assertEquals(0, new BigDecimal("4.00").compareTo(confirmado.getTotal()));
    }

    @Test
    void confirmarPedido_generaUnCodigoDeRetiroUnico() {
        Cliente cliente = crearCliente("CLI-0001");
        Producto cafe = crearProducto("PRD-CAFE01", "Café", "1.50", EstadoProducto.DISPONIBLE);
        DetallePedido seleccion = new DetallePedido(cafe, 1, new BigDecimal("1.50"));

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(productoService.buscarPorCodigo("PRD-CAFE01")).thenReturn(cafe);
        when(pedidoRepository.existeCodigoRetiro(anyString())).thenReturn(true, false);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido confirmado = pedidoService.confirmarPedido(CONTEXTO_CLIENTE, List.of(seleccion));

        assertNotNull(confirmado.getCodigoRetiro());
        verify(pedidoRepository, times(2)).existeCodigoRetiro(anyString());
    }

    @Test
    void confirmarPedido_consolidaProductosDuplicadosEnUnSoloDetalle() {
        Cliente cliente = crearCliente("CLI-0001");
        Producto cafe = crearProducto("PRD-CAFE01", "Café", "1.50", EstadoProducto.DISPONIBLE);
        DetallePedido d1 = new DetallePedido(cafe, 2, new BigDecimal("1.50"));
        DetallePedido d2 = new DetallePedido(cafe, 3, new BigDecimal("1.50"));

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(productoService.buscarPorCodigo("PRD-CAFE01")).thenReturn(cafe);
        when(pedidoRepository.existeCodigoRetiro(anyString())).thenReturn(false);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido confirmado = pedidoService.confirmarPedido(CONTEXTO_CLIENTE, List.of(d1, d2));

        assertEquals(1, confirmado.getDetalles().size());
        assertEquals(5, confirmado.getDetalles().get(0).getCantidad());
        assertEquals(0, new BigDecimal("7.50").compareTo(confirmado.getTotal()));
    }

    @Test
    void confirmarPedido_rechazaSiAgotaIntentosDeCodigoUnico() {
        Cliente cliente = crearCliente("CLI-0001");
        Producto cafe = crearProducto("PRD-CAFE01", "Café", "1.50", EstadoProducto.DISPONIBLE);
        DetallePedido seleccion = new DetallePedido(cafe, 1, new BigDecimal("1.50"));

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(pedidoRepository.existeCodigoRetiro(anyString())).thenReturn(true);

        assertThrows(NegocioException.class, () -> pedidoService.confirmarPedido(CONTEXTO_CLIENTE, List.of(seleccion)));
    }

    @Test
    void consultarEstado_devuelveElEstadoDelPedidoPropio() {
        Cliente cliente = crearCliente("CLI-0001");
        Pedido pedido = new Pedido("PED-111111", cliente);

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertEquals(EstadoPedido.PENDIENTE, pedidoService.consultarEstado(CONTEXTO_CLIENTE, "PED-111111"));
    }

    @Test
    void consultarEstado_ocultaElPedidoDeOtroCliente() {
        Cliente dueno = crearCliente("CLI-0001");
        Cliente otro = crearCliente("CLI-0002");
        Pedido pedido = new Pedido("PED-111111", dueno);

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_OTRO_CLIENTE)).thenReturn(otro);
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.consultarEstado(CONTEXTO_OTRO_CLIENTE, "PED-111111"));
    }

    @Test
    void consultarEstado_permiteAlPersonalConsultarCualquierPedido() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertEquals(EstadoPedido.EN_PREPARACION, pedidoService.consultarEstado(CONTEXTO_PERSONAL, "PED-111111"));
    }

    @Test
    void consultarMisPedidos_devuelveSoloLosPedidosDelClienteAutenticado() {
        Cliente cliente = crearCliente("CLI-0001");
        Pedido pedido = new Pedido("PED-111111", cliente);

        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_CLIENTE)).thenReturn(cliente);
        when(pedidoRepository.buscarPorCliente("CLI-0001")).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.consultarMisPedidos(CONTEXTO_CLIENTE);

        assertEquals(List.of(pedido), resultado);
        verify(pedidoRepository).buscarPorCliente("CLI-0001");
    }

    @Test
    void consultarPedidos_devuelveTodosLosPedidosAlPersonal() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarTodos()).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.consultarPedidos(CONTEXTO_PERSONAL);

        assertEquals(List.of(pedido), resultado);
    }

    @Test
    void consultarPedidos_rechazaSiNoEsPersonal() {
        when(autenticacionService.exigirRol(CONTEXTO_CLIENTE, RolUsuario.PERSONAL))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class, () -> pedidoService.consultarPedidos(CONTEXTO_CLIENTE));
    }

    @Test
    void listarPorEstado_filtraPorEstadoParaElPersonal() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorEstado(EstadoPedido.PENDIENTE)).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.listarPorEstado(CONTEXTO_PERSONAL, EstadoPedido.PENDIENTE);

        assertEquals(List.of(pedido), resultado);
        verify(pedidoRepository).buscarPorEstado(EstadoPedido.PENDIENTE);
    }

    @Test
    void listarPorEstado_rechazaSiNoEsPersonal() {
        when(autenticacionService.exigirRol(CONTEXTO_CLIENTE, RolUsuario.PERSONAL))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class,
                () -> pedidoService.listarPorEstado(CONTEXTO_CLIENTE, EstadoPedido.PENDIENTE));
    }

    @Test
    void buscarPorCodigoRetiro_devuelveElPedido() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertEquals(pedido, pedidoService.buscarPorCodigoRetiro(CONTEXTO_PERSONAL, " ped-111111 "));
    }

    @Test
    void buscarPorCodigoRetiro_rechazaCodigoInexistente() {
        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.buscarPorCodigoRetiro(CONTEXTO_PERSONAL, "PED-NOEXISTE"));
    }

    @Test
    void buscarPorCodigoRetiro_rechazaSinSesionYRolAdministrador() {
        assertThrows(NoAutenticadoException.class,
                () -> pedidoService.buscarPorCodigoRetiro(null, "PED-111111"));
        assertThrows(AccesoDenegadoException.class,
                () -> pedidoService.buscarPorCodigoRetiro(CONTEXTO_ADMIN, "PED-111111"));
    }

    @Test
    void avanzarEstado_pasaDePendienteAEnPreparacion() {
        Personal personal = crearPersonal();
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(personal);
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido actualizado = pedidoService.avanzarEstado(CONTEXTO_PERSONAL, "PED-111111");

        assertEquals(EstadoPedido.EN_PREPARACION, actualizado.getEstado());
        assertEquals(personal, actualizado.getPersonal());
    }

    @Test
    void avanzarEstado_pasaDeEnPreparacionAListoParaRetirar() {
        Personal personal = crearPersonal();
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(personal);
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido actualizado = pedidoService.avanzarEstado(CONTEXTO_PERSONAL, "PED-111111");

        assertEquals(EstadoPedido.LISTO_PARA_RETIRAR, actualizado.getEstado());
    }

    @Test
    void avanzarEstado_rechazaElPasoAEntregado() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertThrows(NegocioException.class, () -> pedidoService.avanzarEstado(CONTEXTO_PERSONAL, "PED-111111"));
    }

    @Test
    void avanzarEstado_rechazaSaltosYRetrocesos() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);
        pedido.cambiarEstado(EstadoPedido.ENTREGADO);

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertThrows(NegocioException.class, () -> pedidoService.avanzarEstado(CONTEXTO_PERSONAL, "PED-111111"));
        assertThrows(NegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.PENDIENTE));
    }

    @Test
    void avanzarEstado_rechazaSiNoEsPersonal() {
        when(autenticacionService.exigirRol(CONTEXTO_CLIENTE, RolUsuario.PERSONAL))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class, () -> pedidoService.avanzarEstado(CONTEXTO_CLIENTE, "PED-111111"));
    }

    @Test
    void entregarPedido_marcaEntregadoConElCodigoCorrecto() {
        Personal personal = crearPersonal();
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(personal);
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido entregado = pedidoService.entregarPedido(CONTEXTO_PERSONAL, "PED-111111", "PED-111111");

        assertEquals(EstadoPedido.ENTREGADO, entregado.getEstado());
        assertEquals(personal, entregado.getPersonal());
    }

    @Test
    void entregarPedido_rechazaCodigoDeRetiroIncorrecto() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertThrows(NegocioException.class,
                () -> pedidoService.entregarPedido(CONTEXTO_PERSONAL, "PED-111111", "PED-999999"));
    }

    @Test
    void entregarPedido_rechazaSiNoEstaListoParaRetirar() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertThrows(NegocioException.class,
                () -> pedidoService.entregarPedido(CONTEXTO_PERSONAL, "PED-111111", "PED-111111"));
    }

    @Test
    void entregarPedido_rechazaSiYaFueEntregado() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);
        pedido.cambiarEstado(EstadoPedido.ENTREGADO);

        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-111111")).thenReturn(Optional.of(pedido));

        assertThrows(NegocioException.class,
                () -> pedidoService.entregarPedido(CONTEXTO_PERSONAL, "PED-111111", "PED-111111"));
    }

    @Test
    void entregarPedido_rechazaSiNoEsPersonal() {
        when(autenticacionService.exigirRol(CONTEXTO_CLIENTE, RolUsuario.PERSONAL))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class,
                () -> pedidoService.entregarPedido(CONTEXTO_CLIENTE, "PED-111111", "PED-111111"));
    }

    @Test
    void confirmarSeleccion_validaSesionAntesDeConsultarProductos() {
        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_PERSONAL))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class, () -> pedidoService.confirmarSeleccion(
                CONTEXTO_PERSONAL, List.of(new SeleccionProductoRequest("PRD-CAFE01", 1))));
    }

    @Test
    void consultarMisPedidos_rechazaSiNoEsCliente() {
        when(autenticacionService.obtenerClienteAutenticado(CONTEXTO_PERSONAL))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class, () -> pedidoService.consultarMisPedidos(CONTEXTO_PERSONAL));
    }

    @Test
    void listarPorEstado_conEstadoNuloDevuelveTodos() {
        Pedido pedido = new Pedido("PED-111111", crearCliente("CLI-0001"));
        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarTodos()).thenReturn(List.of(pedido));

        assertEquals(List.of(pedido), pedidoService.listarPorEstado(CONTEXTO_PERSONAL, null));
    }

    @Test
    void buscarPorCodigoRetiro_rechazaCodigoNuloOVacio() {
        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.buscarPorCodigoRetiro(CONTEXTO_PERSONAL, null));
        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.buscarPorCodigoRetiro(CONTEXTO_PERSONAL, "   "));
    }

    @Test
    void avanzarEstadoYEntregar_rechazanPedidoInexistente() {
        when(autenticacionService.exigirRol(CONTEXTO_PERSONAL, RolUsuario.PERSONAL)).thenReturn(crearPersonal());
        when(pedidoRepository.buscarPorCodigoRetiro("PED-NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.avanzarEstado(CONTEXTO_PERSONAL, "PED-NOEXISTE"));
        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.entregarPedido(CONTEXTO_PERSONAL, "PED-NOEXISTE", "PED-NOEXISTE"));
    }
}