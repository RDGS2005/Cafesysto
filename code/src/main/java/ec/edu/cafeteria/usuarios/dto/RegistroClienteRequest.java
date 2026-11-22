package ec.edu.cafeteria.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario de registro de cliente (RFU-01). El código lo genera el
 * sistema.
 */
public record RegistroClienteRequest(
                @NotBlank String nombre,
                @NotBlank String apellido,
                @NotBlank @Pattern(regexp = "\\d{10}", message = "debe tener exactamente 10 dígitos") String cedula,
                @NotBlank @Pattern(regexp = "\\d{10}", message = "debe tener exactamente 10 dígitos") String celular,
                @NotBlank @Email String correo,
                @NotBlank @Size(min = 8, message = "debe tener al menos 8 caracteres") String clave) {
}
