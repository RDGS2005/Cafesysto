package ec.edu.cafeteria.usuarios.dto;

public record ClienteResponse(String codigo, String nombre, String apellido, String cedula, String celular,
                              String correo) {
}
