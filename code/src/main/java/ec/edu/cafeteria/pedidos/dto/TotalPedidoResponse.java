package ec.edu.cafeteria.pedidos.dto;

import java.math.BigDecimal;
import java.util.List;

public record TotalPedidoResponse(List<DetallePedidoResponse> detalles, BigDecimal total) {
}
