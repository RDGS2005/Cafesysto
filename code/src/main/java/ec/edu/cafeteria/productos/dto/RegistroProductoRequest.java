package ec.edu.cafeteria.productos.dto;

import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Datos para registrar un producto en el menú (RFM-02). El código lo genera el sistema.
 *
 * @param nombre nombre del producto, obligatorio y único
 * @param precio precio del producto, mayor que cero
 * @param estado disponibilidad inicial; si es {@code null}, el producto queda
 *               {@link EstadoProducto#DISPONIBLE}
 */
public record RegistroProductoRequest(
        @NotBlank String nombre,
        @NotNull
        @DecimalMin(value = "0.00", inclusive = false, message = "debe ser mayor que cero")
        @Digits(integer = 8, fraction = 2) BigDecimal precio,
        EstadoProducto estado) {
}
