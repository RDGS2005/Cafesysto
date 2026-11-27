package ec.edu.cafeteria.pedidos.dto;

import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.entidad.Pedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Representación de salida de un pedido confirmado con sus detalles.
 *
 * @param codigoRetiro código único de retiro del pedido
 * @param fechaRegistro fecha y hora de confirmación
 * @param total importe total del pedido
 * @param estado estado actual del pedido
 * @param codigoCliente código del cliente propietario
 * @param detalles lista de productos y cantidades del pedido
 */
public record PedidoResponse(String codigoRetiro, OffsetDateTime fechaRegistro, BigDecimal total,
                             EstadoPedido estado, String codigoCliente, List<DetallePedidoResponse> detalles) {

    /**
     * Construye la respuesta a partir de una entidad {@link Pedido}.
     *
     * @param pedido entidad de pedido confirmado
     * @return representación de salida del pedido
     */
    public static PedidoResponse desde(Pedido pedido) {
        List<DetallePedidoResponse> detallesRespuesta = pedido.getDetalles().stream()
                .map(DetallePedidoResponse::desde)
                .toList();
        String codigoCliente = pedido.getCliente() != null ? pedido.getCliente().obtenerCodigo() : null;
        return new PedidoResponse(
                pedido.getCodigoRetiro(),
                pedido.getFechaRegistro(),
                pedido.getTotal(),
                pedido.getEstado(),
                codigoCliente,
                detallesRespuesta
        );
    }
}
