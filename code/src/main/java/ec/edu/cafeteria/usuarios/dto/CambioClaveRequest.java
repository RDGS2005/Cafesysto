package ec.edu.cafeteria.usuarios.dto;

public record CambioClaveRequest(String actual, String nueva, String confirmacion) {
}
