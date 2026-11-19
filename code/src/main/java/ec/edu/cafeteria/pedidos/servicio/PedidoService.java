package ec.edu.cafeteria.pedidos.servicio;

import ec.edu.cafeteria.pedidos.entidad.DetallePedido;
import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.entidad.Pedido;
import ec.edu.cafeteria.pedidos.repositorio.PedidoRepository;
import ec.edu.cafeteria.productos.servicio.ProductoService;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private PedidoRepository pedidoRepository;
    private ProductoService productoService;
    private AutenticacionService autenticacionService;

    public DetallePedido crearDetalleSeleccion(String codigoProducto, int cantidad) {
        return null;
    }

    public BigDecimal calcularImporteTotal(List<DetallePedido> detalles) {
        return null;
    }

    public Pedido confirmarPedido(ContextoAutenticado contexto, List<DetallePedido> detalles) {
        return null;
    }

    public EstadoPedido consultarEstado(ContextoAutenticado contexto, String codigoRetiro) {
        return null;
    }

    public List<Pedido> consultarMisPedidos(ContextoAutenticado contexto) {
        return null;
    }

    public List<Pedido> consultarPedidos(ContextoAutenticado contexto) {
        return null;
    }

    public List<Pedido> listarPorEstado(ContextoAutenticado contexto, EstadoPedido estado) {
        return null;
    }

    public Pedido buscarPorCodigoRetiro(ContextoAutenticado contexto, String codigoRetiro) {
        return null;
    }

    public Pedido avanzarEstado(ContextoAutenticado contexto, String codigoRetiro) {
        return null;
    }

    public Pedido entregarPedido(ContextoAutenticado contexto, String codigoRetiro, String codigoPresentado) {
        return null;
    }

    private boolean validarSeleccion(List<DetallePedido> detalles) {
        return false;
    }

    private boolean validarCantidadPositiva(int cantidad) {
        return false;
    }

    private boolean validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        return false;
    }

    private boolean validarCodigoRetiro(Pedido pedido, String codigoPresentado) {
        return false;
    }

    private String generarCodigoRetiro() {
        return null;
    }
}
