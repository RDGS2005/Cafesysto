package ec.edu.cafeteria.pedidos.dto;

import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;

/**
 * Respuesta con el estado actual de un pedido confirmado (RFP-04).
 *
 * @param codigoRetiro código único de retiro del pedido
 * @param estado estado actual del pedido
 */
public record EstadoPedidoResponse(String codigoRetiro, EstadoPedido estado) {
}
