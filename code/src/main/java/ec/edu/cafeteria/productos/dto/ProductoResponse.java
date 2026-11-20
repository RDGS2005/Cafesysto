package ec.edu.cafeteria.productos.dto;

import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;

import java.math.BigDecimal;

/**
 * Datos de un producto que se envían a la interfaz.
 *
 * @param codigo código único del producto
 * @param nombre nombre del producto
 * @param precio precio vigente del producto
 * @param estado disponibilidad del producto
 */
public record ProductoResponse(String codigo, String nombre, BigDecimal precio,
                               EstadoProducto estado) {

    /**
     * Convierte una entidad {@link Producto} en su respuesta.
     *
     * @param producto producto a convertir
     * @return respuesta con los datos del producto
     */
    public static ProductoResponse desde(Producto producto) {
        return new ProductoResponse(producto.getCodigo(), producto.getNombre(),
                producto.getPrecio(), producto.getEstado());
    }
}
