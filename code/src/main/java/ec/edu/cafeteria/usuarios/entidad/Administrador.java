package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "requiere_cambio_clave")
    private Boolean requiereCambioClave;

    public boolean debeCambiarClave() {
        return false;
    }

    public void actualizarClave(String nuevoHash) {
    }

    @Override
    public String obtenerTokenAcceso() {
        return null;
    }

    @Override
    public RolUsuario obtenerRol() {
        return null;
    }
}
