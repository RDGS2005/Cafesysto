package ec.edu.cafeteria.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para registrar personal (RFU-05).
 * PENDIENTE: RFU-05 no define cómo se asigna la contraseña inicial; por ahora
 * la envía el administrador.
 */
public record RegistroPersonalRequest(
                @NotBlank String nombre,
                @NotBlank String apellido,
                @NotBlank @Pattern(regexp = "\\d{10}", message = "debe tener exactamente 10 dígitos") String cedula,
                @NotBlank @Pattern(regexp = "\\d{10}", message = "debe tener exactamente 10 dígitos") String celular,
                @NotBlank @Email String correo,
                @NotBlank @Size(min = 8, message = "debe tener al menos 8 caracteres") String clave) {
}
