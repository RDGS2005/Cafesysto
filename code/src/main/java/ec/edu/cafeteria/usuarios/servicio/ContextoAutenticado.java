package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.usuarios.entidad.RolUsuario;

public record ContextoAutenticado(String identificador, RolUsuario rol) {
}
