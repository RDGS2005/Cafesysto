package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public abstract class Persona extends Usuario {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "cedula")
    private String cedula;

    @Column(name = "celular")
    private String celular;

    @Column(name = "correo")
    private String correo;

    public String obtenerCedula() {
        return null;
    }

    public String obtenerCorreo() {
        return null;
    }

    @Override
    public String obtenerTokenAcceso() {
        return null;
    }
}
