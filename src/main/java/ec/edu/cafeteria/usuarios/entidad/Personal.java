package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PERSONAL")
@NoArgsConstructor
public class Personal extends Persona {

    @Override
    public RolUsuario obtenerRol() {
        return RolUsuario.PERSONAL;
    }
}
