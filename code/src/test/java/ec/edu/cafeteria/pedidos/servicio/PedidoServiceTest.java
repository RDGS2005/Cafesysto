package ec.edu.cafeteria.pedidos.servicio;

import ec.edu.cafeteria.pedidos.repositorio.PedidoRepository;
import ec.edu.cafeteria.productos.servicio.ProductoService;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private AutenticacionService autenticacionService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void crearDetalleSeleccion_devuelveElDetalleConElPrecioVigente() {
    }

    @Test
    void crearDetalleSeleccion_rechazaCantidadNoPositiva() {
    }

    @Test
    void crearDetalleSeleccion_rechazaProductoNoDisponible() {
    }

    @Test
    void calcularImporteTotal_sumaLosSubtotales() {
    }

    @Test
    void calcularImporteTotal_devuelveCeroSinDetalles() {
    }

    @Test
    void confirmarPedido_guardaElPedidoPendienteConCodigoDeRetiro() {
    }

    @Test
    void confirmarPedido_asociaElPedidoAlClienteAutenticado() {
    }

    @Test
    void confirmarPedido_rechazaSeleccionVacia() {
    }

    @Test
    void confirmarPedido_rechazaSiNoEsCliente() {
    }

    @Test
    void confirmarPedido_guardaElPrecioUnitarioDelMomentoDeLaConfirmacion() {
    }

    @Test
    void confirmarPedido_generaUnCodigoDeRetiroUnico() {
    }

    @Test
    void consultarEstado_devuelveElEstadoDelPedidoPropio() {
    }

    @Test
    void consultarEstado_ocultaElPedidoDeOtroCliente() {
    }

    @Test
    void consultarEstado_permiteAlPersonalConsultarCualquierPedido() {
    }

    @Test
    void consultarMisPedidos_devuelveSoloLosPedidosDelClienteAutenticado() {
    }

    @Test
    void consultarPedidos_devuelveTodosLosPedidosAlPersonal() {
    }

    @Test
    void consultarPedidos_rechazaSiNoEsPersonal() {
    }

    @Test
    void listarPorEstado_filtraPorEstadoParaElPersonal() {
    }

    @Test
    void buscarPorCodigoRetiro_devuelveElPedido() {
    }

    @Test
    void buscarPorCodigoRetiro_rechazaCodigoInexistente() {
    }

    @Test
    void avanzarEstado_pasaDePendienteAEnPreparacion() {
    }

    @Test
    void avanzarEstado_pasaDeEnPreparacionAListoParaRetirar() {
    }

    @Test
    void avanzarEstado_rechazaElPasoAEntregado() {
    }

    @Test
    void avanzarEstado_rechazaSaltosYRetrocesos() {
    }

    @Test
    void avanzarEstado_rechazaSiNoEsPersonal() {
    }

    @Test
    void entregarPedido_marcaEntregadoConElCodigoCorrecto() {
    }

    @Test
    void entregarPedido_rechazaCodigoDeRetiroIncorrecto() {
    }

    @Test
    void entregarPedido_rechazaSiNoEstaListoParaRetirar() {
    }

    @Test
    void entregarPedido_rechazaSiNoEsPersonal() {
    }
}
