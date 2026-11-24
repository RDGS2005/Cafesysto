package ec.edu.cafeteria.pedidos.dto;

import java.util.List;

/**
 * Solicitud con los productos y cantidades seleccionados para calcular total o confirmar (RFP-02, RFP-03).
 *
 * @param items lista de productos seleccionados y sus cantidades
 */
public record SeleccionPedidoRequest(List<SeleccionProductoRequest> items) {
}
