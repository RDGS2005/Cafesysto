package ec.edu.cafeteria.productos.dto;

import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import jakarta.validation.constraints.NotNull;

/**
 * Datos para actualizar la disponibilidad de un producto (RFM-03-2).
 *
 * @param estado nuevo estado de disponibilidad del producto
 */
public record ActualizarDisponibilidadRequest(@NotNull EstadoProducto estado) {
}
