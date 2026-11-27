package ec.edu.cafeteria.pedidos.dto;

/**
 * Datos enviados por el personal para confirmar la entrega de un pedido (RFP-05-2, RN-NEG-07).
 *
 * @param codigoPresentado código de retiro presentado por el cliente
 */
public record EntregaPedidoRequest(String codigoPresentado) {
}
