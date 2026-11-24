package ec.edu.cafeteria.pedidos.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Respuesta con los detalles valorizados al precio vigente y el total previo del pedido (RFP-02).
 *
 * @param detalles líneas valorizadas con precio unitario vigente y subtotal
 * @param total importe total calculado
 */
public record TotalPedidoResponse(List<DetallePedidoResponse> detalles, BigDecimal total) {
}
