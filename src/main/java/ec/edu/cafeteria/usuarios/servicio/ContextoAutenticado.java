package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.usuarios.entidad.RolUsuario;

/**
 * Identidad validada por el servidor (se recupera de la sesión en cada
 * solicitud).
 * Nunca se arma con un código de usuario recibido del formulario.
 */
public record ContextoAutenticado(String identificador, RolUsuario rol) {
}
