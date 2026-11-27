package ec.edu.cafeteria.pedidos.dto;

import ec.edu.cafeteria.pedidos.entidad.DetallePedido;

import java.math.BigDecimal;

/**
 * Representación de salida de una línea de detalle de pedido.
 *
 * @param codigoProducto código único del producto
 * @param nombreProducto nombre del producto
 * @param cantidad cantidad solicitada
 * @param precioUnitario precio unitario registrado en el detalle
 * @param subtotal importe parcial de la línea ({@code precioUnitario * cantidad})
 */
public record DetallePedidoResponse(String codigoProducto, String nombreProducto, int cantidad,
                                    BigDecimal precioUnitario, BigDecimal subtotal) {

    /**
     * Construye la respuesta a partir de una entidad {@link DetallePedido}.
     *
     * @param detalle entidad de detalle del pedido
     * @return representación de salida del detalle
     */
    public static DetallePedidoResponse desde(DetallePedido detalle) {
        return new DetallePedidoResponse(
                detalle.getProducto().getCodigo(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.calcularSubtotal()
        );
    }
}
