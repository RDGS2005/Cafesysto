package ec.edu.cafeteria.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record CambioClaveRequest(@NotBlank String actual, @NotBlank String nueva, @NotBlank String confirmacion) {
}
