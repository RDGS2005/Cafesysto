package ec.edu.cafeteria.usuarios.dto;

public record RegistroPersonalRequest(String nombre, String apellido, String cedula, String celular,
                                      String correo, String clave) {
}
