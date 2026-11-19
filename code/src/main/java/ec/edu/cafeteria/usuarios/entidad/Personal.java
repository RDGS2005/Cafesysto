package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PERSONAL")
public class Personal extends Persona {

    @Override
    public RolUsuario obtenerRol() {
        return null;
    }
}
