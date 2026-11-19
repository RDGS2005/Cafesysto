package ec.edu.cafeteria.pedidos.controlador;

import ec.edu.cafeteria.pedidos.dto.DetallePedidoResponse;
import ec.edu.cafeteria.pedidos.dto.EntregaPedidoRequest;
import ec.edu.cafeteria.pedidos.dto.EstadoPedidoResponse;
import ec.edu.cafeteria.pedidos.dto.PedidoResponse;
import ec.edu.cafeteria.pedidos.dto.SeleccionPedidoRequest;
import ec.edu.cafeteria.pedidos.dto.SeleccionProductoRequest;
import ec.edu.cafeteria.pedidos.dto.TotalPedidoResponse;
import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.servicio.PedidoService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private PedidoService pedidoService;

    @PostMapping("/seleccion")
    public DetallePedidoResponse seleccionarProducto(@RequestBody SeleccionProductoRequest datos) {
        return null;
    }

    @PostMapping("/total")
    public TotalPedidoResponse calcularTotalPrevio(@RequestBody SeleccionPedidoRequest datos) {
        return null;
    }

    @PostMapping
    public PedidoResponse confirmarPedido(ContextoAutenticado contexto, @RequestBody SeleccionPedidoRequest datos) {
        return null;
    }

    @GetMapping("/{codigoRetiro}/estado")
    public EstadoPedidoResponse consultarEstadoPedido(ContextoAutenticado contexto,
                                                      @PathVariable String codigoRetiro) {
        return null;
    }

    @GetMapping("/mis-pedidos")
    public List<PedidoResponse> consultarMisPedidos(ContextoAutenticado contexto) {
        return null;
    }

    @GetMapping
    public List<PedidoResponse> consultarPedidos(ContextoAutenticado contexto,
                                                 @RequestParam(required = false) EstadoPedido estado) {
        return null;
    }

    @GetMapping("/{codigoRetiro}")
    public PedidoResponse consultarDetallePedido(ContextoAutenticado contexto, @PathVariable String codigoRetiro) {
        return null;
    }

    @PostMapping("/{codigoRetiro}/avanzar")
    public PedidoResponse avanzarEstadoPedido(ContextoAutenticado contexto, @PathVariable String codigoRetiro) {
        return null;
    }

    @PostMapping("/{codigoRetiro}/entrega")
    public PedidoResponse registrarEntregaPedido(ContextoAutenticado contexto, @PathVariable String codigoRetiro,
                                                 @RequestBody EntregaPedidoRequest datos) {
        return null;
    }
}
