package ec.edu.cafeteria.usuarios.dto;

import ec.edu.cafeteria.usuarios.entidad.RolUsuario;

/**
 * El token se envía luego en la cabecera "Authorization: Bearer &lt;token&gt;".
 */
public record LoginResponse(String token, RolUsuario rol, boolean requiereCambioClave) {
}
