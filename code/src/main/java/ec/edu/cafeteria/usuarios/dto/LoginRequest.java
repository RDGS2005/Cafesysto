package ec.edu.cafeteria.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Identificador: correo para cliente y personal, nombre de usuario para el
 * administrador.
 */
public record LoginRequest(@NotBlank String identificador, @NotBlank String clave) {
}
