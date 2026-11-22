package ec.edu.cafeteria.usuarios.dto;

import ec.edu.cafeteria.usuarios.entidad.Cliente;

public record ClienteResponse(String codigo, String nombre, String apellido, String cedula, String celular,
        String correo) {

    public static ClienteResponse desde(Cliente c) {
        return new ClienteResponse(c.obtenerCodigo(), c.getNombre(), c.getApellido(), c.getCedula(),
                c.getCelular(), c.getCorreo());
    }
}
