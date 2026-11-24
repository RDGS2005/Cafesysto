package ec.edu.cafeteria.pedidos.dto;

/**
 * Solicitud con un producto del menú y la cantidad deseada por el cliente (RFP-01).
 *
 * @param codigoProducto código único del producto
 * @param cantidad cantidad entera positiva solicitada
 */
public record SeleccionProductoRequest(String codigoProducto, int cantidad) {
}
