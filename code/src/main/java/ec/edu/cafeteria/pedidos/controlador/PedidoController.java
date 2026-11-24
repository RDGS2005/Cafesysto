package ec.edu.cafeteria.pedidos.controlador;

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
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Punto de entrada HTTP del módulo de pedidos. Delega la lógica de negocio en
 * {@link PedidoService} y mantiene los contratos esperados por el frontend.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Crea el controlador con su servicio de pedidos.
     *
     * @param pedidoService servicio del módulo de pedidos
     */
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Valida y construye la línea de selección de un producto con su precio vigente (RFP-01).
     *
     * @param datos código del producto y cantidad solicitada
     * @return detalle calculado con precio unitario vigente y subtotal
     */
    @PostMapping("/seleccion")
    public DetallePedidoResponse seleccionarProducto(@RequestBody SeleccionProductoRequest datos) {
        if (datos == null) {
            throw new NegocioException("Los datos del producto son obligatorios");
        }
        DetallePedido detalle = pedidoService.crearDetalleSeleccion(datos.codigoProducto(), datos.cantidad());
        return DetallePedidoResponse.desde(detalle);
    }

    /**
     * Calcula el importe total previo de una selección de productos antes de confirmar (RFP-02).
     *
     * <p>Si el mismo producto aparece repetido en la solicitud, consolida sus cantidades
     * para mantener consistencia exacta con la confirmación del pedido.</p>
     *
     * @param datos selección de productos y cantidades del pedido en curso
     * @return lista de detalles con precios vigentes y el importe total calculado
     */
    @PostMapping("/total")
    public TotalPedidoResponse calcularTotalPrevio(@RequestBody SeleccionPedidoRequest datos) {
        List<DetallePedido> detalles = construirDetallesConsolidados(datos);
        BigDecimal total = pedidoService.calcularImporteTotal(detalles);
        List<DetallePedidoResponse> respuestas = detalles.stream()
                .map(DetallePedidoResponse::desde)
                .toList();
        return new TotalPedidoResponse(respuestas, total);
    }

    /**
     * Confirma el pedido del cliente autenticado y genera su código de retiro único (RFP-03).
     *
     * <p>Delega directamente al servicio para validar primero la sesión y el rol de cliente
     * antes de consultar el catálogo de productos.</p>
     *
     * @param contexto identidad autenticada del cliente
     * @param datos selección de productos a confirmar
     * @return pedido confirmado con código de retiro y estado {@link EstadoPedido#PENDIENTE}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse confirmarPedido(ContextoAutenticado contexto, @RequestBody SeleccionPedidoRequest datos) {
        List<SeleccionProductoRequest> items = (datos != null && datos.items() != null)
                ? datos.items()
                : List.of();
        Pedido pedido = pedidoService.confirmarSeleccion(contexto, items);
        return PedidoResponse.desde(pedido);
    }

    /**
     * Consulta el estado actual de un pedido mediante su código de retiro (RFP-04).
     *
     * @param contexto identidad autenticada del solicitante
     * @param codigoRetiro código de retiro del pedido
     * @return código de retiro normalizado y estado actual
     */
    @GetMapping("/{codigoRetiro}/estado")
    public EstadoPedidoResponse consultarEstadoPedido(ContextoAutenticado contexto,
                                                      @PathVariable String codigoRetiro) {
        EstadoPedido estado = pedidoService.consultarEstado(contexto, codigoRetiro);
        String codigoNormalizado = (codigoRetiro != null) ? codigoRetiro.trim().toUpperCase(Locale.ROOT) : null;
        return new EstadoPedidoResponse(codigoNormalizado, estado);
    }

    /**
     * Lista todos los pedidos confirmados del cliente autenticado (RFP-04).
     *
     * @param contexto identidad autenticada del cliente
     * @return pedidos del cliente ordenados del más reciente al más antiguo
     */
    @GetMapping("/mis-pedidos")
    public List<PedidoResponse> consultarMisPedidos(ContextoAutenticado contexto) {
        return pedidoService.consultarMisPedidos(contexto).stream()
                .map(PedidoResponse::desde)
                .toList();
    }

    /**
     * Lista los pedidos recibidos para el personal, opcionalmente filtrados por estado (RFP-05-1).
     *
     * @param contexto identidad autenticada del personal
     * @param estado filtro de estado opcional
     * @return lista de pedidos encontrados
     */
    @GetMapping
    public List<PedidoResponse> consultarPedidos(ContextoAutenticado contexto,
                                                 @RequestParam(required = false) EstadoPedido estado) {
        List<Pedido> pedidos = (estado == null)
                ? pedidoService.consultarPedidos(contexto)
                : pedidoService.listarPorEstado(contexto, estado);
        return pedidos.stream()
                .map(PedidoResponse::desde)
                .toList();
    }

    /**
     * Consulta el detalle completo de un pedido por su código de retiro (RFP-04, RFP-05-1).
     *
     * @param contexto identidad autenticada del solicitante
     * @param codigoRetiro código de retiro del pedido
     * @return detalle completo del pedido
     */
    @GetMapping("/{codigoRetiro}")
    public PedidoResponse consultarDetallePedido(ContextoAutenticado contexto, @PathVariable String codigoRetiro) {
        return PedidoResponse.desde(pedidoService.buscarPorCodigoRetiro(contexto, codigoRetiro));
    }

    /**
     * Avanza un pedido al siguiente estado de preparación (RFP-05-2).
     *
     * @param contexto identidad autenticada del personal
     * @param codigoRetiro código de retiro del pedido
     * @return pedido con el estado actualizado
     */
    @PostMapping("/{codigoRetiro}/avanzar")
    public PedidoResponse avanzarEstadoPedido(ContextoAutenticado contexto, @PathVariable String codigoRetiro) {
        return PedidoResponse.desde(pedidoService.avanzarEstado(contexto, codigoRetiro));
    }

    /**
     * Registra la entrega de un pedido validando el código presentado por el cliente (RFP-05-2).
     *
     * @param contexto identidad autenticada del personal
     * @param codigoRetiro código de retiro del pedido
     * @param datos código presentado por el cliente
     * @return pedido actualizado en estado {@link EstadoPedido#ENTREGADO}
     */
    @PostMapping("/{codigoRetiro}/entrega")
    public PedidoResponse registrarEntregaPedido(ContextoAutenticado contexto, @PathVariable String codigoRetiro,
                                                 @RequestBody EntregaPedidoRequest datos) {
        String codigoPresentado = (datos != null) ? datos.codigoPresentado() : null;
        return PedidoResponse.desde(pedidoService.entregarPedido(contexto, codigoRetiro, codigoPresentado));
    }

    private List<DetallePedido> construirDetallesConsolidados(SeleccionPedidoRequest datos) {
        if (datos == null || datos.items() == null || datos.items().isEmpty()) {
            return List.of();
        }
        Map<String, Integer> cantidadesPorCodigo = new LinkedHashMap<>();
        for (SeleccionProductoRequest item : datos.items()) {
            if (item == null) {
                throw new NegocioException("Cada ítem del pedido es obligatorio");
            }
            if (item.codigoProducto() == null || item.codigoProducto().isBlank()) {
                throw new NegocioException("El código del producto es obligatorio");
            }
            if (item.cantidad() <= 0) {
                throw new NegocioException("La cantidad debe ser un número entero positivo");
            }
            String codigo = item.codigoProducto().trim();
            try {
                cantidadesPorCodigo.merge(codigo, item.cantidad(), Math::addExact);
            } catch (ArithmeticException ex) {
                throw new NegocioException("La cantidad total del producto excede el límite permitido");
            }
        }
        List<DetallePedido> detalles = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : cantidadesPorCodigo.entrySet()) {
            detalles.add(pedidoService.crearDetalleSeleccion(entrada.getKey(), entrada.getValue()));
        }
        return detalles;
    }
}
