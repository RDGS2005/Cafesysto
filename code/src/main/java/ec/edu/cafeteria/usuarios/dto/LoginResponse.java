package ec.edu.cafeteria.usuarios.dto;

import ec.edu.cafeteria.usuarios.entidad.RolUsuario;

public record LoginResponse(String token, RolUsuario rol, boolean requiereCambioClave) {
}
