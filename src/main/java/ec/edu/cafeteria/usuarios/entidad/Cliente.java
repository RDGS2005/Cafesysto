package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Persona {

    /**
     * Código generado por el sistema al registrar al cliente; no cambia después.
     */
    @Column(name = "codigo_cliente", updatable = false)
    private String codigo;

    public String obtenerCodigo() {
        return codigo;
    }

    @Override
    public RolUsuario obtenerRol() {
        return RolUsuario.CLIENTE;
    }
}
