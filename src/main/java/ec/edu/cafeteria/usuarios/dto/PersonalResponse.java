package ec.edu.cafeteria.usuarios.dto;

import ec.edu.cafeteria.usuarios.entidad.Personal;

public record PersonalResponse(String nombre, String apellido, String cedula, String celular, String correo) {

    public static PersonalResponse desde(Personal p) {
        return new PersonalResponse(p.getNombre(), p.getApellido(), p.getCedula(), p.getCelular(), p.getCorreo());
    }
}
