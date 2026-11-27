package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos comunes de clientes y personal. No aplica al administrador. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public abstract class Persona extends Usuario {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "cedula", length = 10)
    private String cedula;

    @Column(name = "celular", length = 10)
    private String celular;

    @Column(name = "correo")
    private String correo;

    public String obtenerCedula() {
        return cedula;
    }

    public String obtenerCorreo() {
        return correo;
    }

    @Override
    public String obtenerTokenAcceso() {
        return correo;
    }
}
