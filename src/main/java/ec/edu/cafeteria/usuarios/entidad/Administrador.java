package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "requiere_cambio_clave")
    private Boolean requiereCambioClave;

    public boolean debeCambiarClave() {
        return Boolean.TRUE.equals(requiereCambioClave);
    }

    /** Guarda el nuevo hash y retira la marca de cambio pendiente. */
    public void actualizarClave(String nuevoHash) {
        setClaveHash(nuevoHash);
        this.requiereCambioClave = false;
    }

    @Override
    public String obtenerTokenAcceso() {
        return nombreUsuario;
    }

    @Override
    public RolUsuario obtenerRol() {
        return RolUsuario.ADMINISTRADOR;
    }
}
