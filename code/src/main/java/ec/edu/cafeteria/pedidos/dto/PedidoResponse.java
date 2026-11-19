package ec.edu.cafeteria.pedidos.dto;

import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PedidoResponse(String codigoRetiro, OffsetDateTime fechaRegistro, BigDecimal total,
                             EstadoPedido estado, String codigoCliente, List<DetallePedidoResponse> detalles) {
}
