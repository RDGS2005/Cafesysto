package ec.edu.cafeteria.pedidos.controlador;

import ec.edu.cafeteria.pedidos.servicio.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @Test
    void seleccionarProducto_devuelveLaLineaSeleccionada() {
    }

    @Test
    void calcularTotalPrevio_devuelveLosDetallesYElTotal() {
    }

    @Test
    void confirmarPedido_devuelveElPedidoConSuCodigoDeRetiro() {
    }

    @Test
    void consultarEstadoPedido_devuelveElEstadoDelPedido() {
    }

    @Test
    void consultarMisPedidos_devuelveLosPedidosDelCliente() {
    }

    @Test
    void consultarPedidos_sinFiltroDevuelveTodosLosPedidos() {
    }

    @Test
    void consultarPedidos_conEstadoFiltraPorEstado() {
    }

    @Test
    void consultarDetallePedido_devuelveElDetalleDelPedido() {
    }

    @Test
    void avanzarEstadoPedido_devuelveElPedidoConElNuevoEstado() {
    }

    @Test
    void registrarEntregaPedido_devuelveElPedidoEntregado() {
    }
}
