package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Persona {

    @Column(name = "codigo_cliente")
    private String codigo;

    public String obtenerCodigo() {
        return null;
    }

    @Override
    public RolUsuario obtenerRol() {
        return null;
    }
}
