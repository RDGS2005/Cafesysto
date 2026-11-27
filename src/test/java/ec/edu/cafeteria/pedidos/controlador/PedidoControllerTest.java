package ec.edu.cafeteria.pedidos.controlador;

import ec.edu.cafeteria.comun.excepcion.AccesoDenegadoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.pedidos.dto.DetallePedidoResponse;
import ec.edu.cafeteria.pedidos.dto.EntregaPedidoRequest;
import ec.edu.cafeteria.pedidos.dto.EstadoPedidoResponse;
import ec.edu.cafeteria.pedidos.dto.PedidoResponse;
import ec.edu.cafeteria.pedidos.dto.SeleccionPedidoRequest;
import ec.edu.cafeteria.pedidos.dto.SeleccionProductoRequest;
import ec.edu.cafeteria.pedidos.dto.TotalPedidoResponse;
import ec.edu.cafeteria.pedidos.entidad.DetallePedido;
import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.entidad.Pedido;
import ec.edu.cafeteria.pedidos.servicio.PedidoService;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link PedidoController}. El servicio se simula con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    private static final String CODIGO_RETIRO = "PED-ABC123";
    private static final ContextoAutenticado CLIENTE =
            new ContextoAutenticado("cliente@epn.edu.ec", RolUsuario.CLIENTE);
    private static final ContextoAutenticado PERSONAL =
            new ContextoAutenticado("personal@epn.edu.ec", RolUsuario.PERSONAL);

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private DetallePedido crearDetalle() {
        Producto cafe = new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), EstadoProducto.DISPONIBLE);
        return new DetallePedido(cafe, 2, new BigDecimal("1.50"));
    }

    private Pedido crearPedido(EstadoPedido estadoFinal) {
        Cliente cliente = new Cliente();
        cliente.setCodigo("CLI-0001");
        Pedido pedido = new Pedido(CODIGO_RETIRO, cliente);
        pedido.agregarDetalle(crearDetalle());
        if (estadoFinal == EstadoPedido.EN_PREPARACION) {
            pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        } else if (estadoFinal == EstadoPedido.LISTO_PARA_RETIRAR) {
            pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
            pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);
        } else if (estadoFinal == EstadoPedido.ENTREGADO) {
            pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
            pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);
            pedido.cambiarEstado(EstadoPedido.ENTREGADO);
        }
        return pedido;
    }

    @Test
    void seleccionarProducto_devuelveLaLineaSeleccionada() {
        DetallePedido detalle = crearDetalle();
        when(pedidoService.crearDetalleSeleccion("PRD-CAFE01", 2)).thenReturn(detalle);

        DetallePedidoResponse respuesta = pedidoController.seleccionarProducto(
                new SeleccionProductoRequest("PRD-CAFE01", 2));

        assertEquals(DetallePedidoResponse.desde(detalle), respuesta);
    }

    @Test
    void seleccionarProducto_rechazaSolicitudNula() {
        assertThrows(NegocioException.class, () -> pedidoController.seleccionarProducto(null));
    }

    @Test
    void calcularTotalPrevio_devuelveLosDetallesYElTotal() {
        DetallePedido detalle = crearDetalle();
        SeleccionPedidoRequest solicitud = new SeleccionPedidoRequest(
                List.of(new SeleccionProductoRequest("PRD-CAFE01", 2)));
        when(pedidoService.crearDetalleSeleccion("PRD-CAFE01", 2)).thenReturn(detalle);
        when(pedidoService.calcularImporteTotal(List.of(detalle))).thenReturn(new BigDecimal("3.00"));

        TotalPedidoResponse respuesta = pedidoController.calcularTotalPrevio(solicitud);

        assertEquals(List.of(DetallePedidoResponse.desde(detalle)), respuesta.detalles());
        assertEquals(0, new BigDecimal("3.00").compareTo(respuesta.total()));
    }

    @Test
    void calcularTotalPrevio_consolidaProductosRepetidos() {
        Producto cafe = new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), EstadoProducto.DISPONIBLE);
        DetallePedido detalleConsolidado = new DetallePedido(cafe, 3, new BigDecimal("1.50"));
        SeleccionPedidoRequest solicitud = new SeleccionPedidoRequest(List.of(
                new SeleccionProductoRequest("PRD-CAFE01", 1),
                new SeleccionProductoRequest("PRD-CAFE01", 2)
        ));
        when(pedidoService.crearDetalleSeleccion("PRD-CAFE01", 3)).thenReturn(detalleConsolidado);
        when(pedidoService.calcularImporteTotal(List.of(detalleConsolidado))).thenReturn(new BigDecimal("4.50"));

        TotalPedidoResponse respuesta = pedidoController.calcularTotalPrevio(solicitud);

        assertEquals(1, respuesta.detalles().size());
        assertEquals(3, respuesta.detalles().get(0).cantidad());
        assertEquals(0, new BigDecimal("4.50").compareTo(respuesta.total()));
    }

    @Test
    void calcularTotalPrevio_rechazaItemNuloEnLista() {
        SeleccionPedidoRequest solicitud = new SeleccionPedidoRequest(Collections.singletonList(null));

        assertThrows(NegocioException.class, () -> pedidoController.calcularTotalPrevio(solicitud));
    }

    @Test
    void confirmarPedido_devuelveElPedidoConSuCodigoDeRetiro() {
        Pedido pedido = crearPedido(EstadoPedido.PENDIENTE);
        List<SeleccionProductoRequest> items = List.of(new SeleccionProductoRequest("PRD-CAFE01", 2));
        SeleccionPedidoRequest solicitud = new SeleccionPedidoRequest(items);
        when(pedidoService.confirmarSeleccion(CLIENTE, items)).thenReturn(pedido);

        PedidoResponse respuesta = pedidoController.confirmarPedido(CLIENTE, solicitud);

        assertEquals(CODIGO_RETIRO, respuesta.codigoRetiro());
        assertEquals(EstadoPedido.PENDIENTE, respuesta.estado());
        assertEquals("CLI-0001", respuesta.codigoCliente());
    }

    @Test
    void confirmarPedido_validaRolAntesDeConsultarProductos() {
        List<SeleccionProductoRequest> items = List.of(new SeleccionProductoRequest("PRD-CAFE01", 2));
        SeleccionPedidoRequest solicitud = new SeleccionPedidoRequest(items);
        when(pedidoService.confirmarSeleccion(PERSONAL, items))
                .thenThrow(new AccesoDenegadoException("No tiene permiso para esta operación"));

        assertThrows(AccesoDenegadoException.class, () -> pedidoController.confirmarPedido(PERSONAL, solicitud));
        verify(pedidoService, never()).crearDetalleSeleccion(anyString(), anyInt());
    }

    @Test
    void consultarEstadoPedido_devuelveElEstadoDelPedido() {
        when(pedidoService.consultarEstado(CLIENTE, " ped-abc123 ")).thenReturn(EstadoPedido.EN_PREPARACION);

        EstadoPedidoResponse respuesta = pedidoController.consultarEstadoPedido(CLIENTE, " ped-abc123 ");

        assertEquals(new EstadoPedidoResponse(CODIGO_RETIRO, EstadoPedido.EN_PREPARACION), respuesta);
    }

    @Test
    void consultarMisPedidos_devuelveLosPedidosDelCliente() {
        Pedido pedido = crearPedido(EstadoPedido.PENDIENTE);
        when(pedidoService.consultarMisPedidos(CLIENTE)).thenReturn(List.of(pedido));

        List<PedidoResponse> respuesta = pedidoController.consultarMisPedidos(CLIENTE);

        assertEquals(List.of(PedidoResponse.desde(pedido)), respuesta);
    }

    @Test
    void consultarPedidos_sinFiltroDevuelveTodosLosPedidos() {
        Pedido pedido = crearPedido(EstadoPedido.PENDIENTE);
        when(pedidoService.consultarPedidos(PERSONAL)).thenReturn(List.of(pedido));

        List<PedidoResponse> respuesta = pedidoController.consultarPedidos(PERSONAL, null);

        assertEquals(List.of(PedidoResponse.desde(pedido)), respuesta);
        verify(pedidoService).consultarPedidos(PERSONAL);
    }

    @Test
    void consultarPedidos_conEstadoFiltraPorEstado() {
        Pedido pedido = crearPedido(EstadoPedido.EN_PREPARACION);
        when(pedidoService.listarPorEstado(PERSONAL, EstadoPedido.EN_PREPARACION)).thenReturn(List.of(pedido));

        List<PedidoResponse> respuesta = pedidoController.consultarPedidos(PERSONAL, EstadoPedido.EN_PREPARACION);

        assertEquals(List.of(PedidoResponse.desde(pedido)), respuesta);
        verify(pedidoService).listarPorEstado(PERSONAL, EstadoPedido.EN_PREPARACION);
    }

    @Test
    void consultarDetallePedido_devuelveElDetalleDelPedido() {
        Pedido pedido = crearPedido(EstadoPedido.PENDIENTE);
        when(pedidoService.buscarPorCodigoRetiro(PERSONAL, CODIGO_RETIRO)).thenReturn(pedido);

        PedidoResponse respuesta = pedidoController.consultarDetallePedido(PERSONAL, CODIGO_RETIRO);

        assertEquals(PedidoResponse.desde(pedido), respuesta);
    }

    @Test
    void avanzarEstadoPedido_devuelveElPedidoConElNuevoEstado() {
        Pedido pedido = crearPedido(EstadoPedido.EN_PREPARACION);
        when(pedidoService.avanzarEstado(PERSONAL, CODIGO_RETIRO)).thenReturn(pedido);

        PedidoResponse respuesta = pedidoController.avanzarEstadoPedido(PERSONAL, CODIGO_RETIRO);

        assertEquals(EstadoPedido.EN_PREPARACION, respuesta.estado());
    }

    @Test
    void registrarEntregaPedido_devuelveElPedidoEntregado() {
        Pedido pedido = crearPedido(EstadoPedido.ENTREGADO);
        when(pedidoService.entregarPedido(PERSONAL, CODIGO_RETIRO, CODIGO_RETIRO)).thenReturn(pedido);

        PedidoResponse respuesta = pedidoController.registrarEntregaPedido(
                PERSONAL, CODIGO_RETIRO, new EntregaPedidoRequest(CODIGO_RETIRO));

        assertEquals(EstadoPedido.ENTREGADO, respuesta.estado());
    }
}
