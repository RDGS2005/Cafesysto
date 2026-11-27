package ec.edu.cafeteria.productos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Datos para actualizar el precio de un producto (RFM-03-1).
 *
 * @param precio nuevo precio del producto, mayor que cero
 */
public record ActualizarPrecioRequest(
        @NotNull
        @DecimalMin(value = "0.00", inclusive = false, message = "debe ser mayor que cero")
        @Digits(integer = 8, fraction = 2) BigDecimal precio) {
}
