package ec.edu.cafeteria.pedidos.dto;

import java.math.BigDecimal;

public record DetallePedidoResponse(String codigoProducto, String nombreProducto, int cantidad,
                                    BigDecimal precioUnitario, BigDecimal subtotal) {
}
