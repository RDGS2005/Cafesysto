package ec.edu.cafeteria.productos.entidad;

/**
 * Disponibilidad de un producto en el menú (RFM-02).
 */
public enum EstadoProducto {

    /** El producto aparece en el menú y puede incluirse en nuevos pedidos. */
    DISPONIBLE,

    /** El producto no aparece en el menú ni puede incluirse en nuevos pedidos. */
    NO_DISPONIBLE
}
