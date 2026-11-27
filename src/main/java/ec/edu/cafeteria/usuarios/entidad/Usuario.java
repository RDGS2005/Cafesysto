package ec.edu.cafeteria.usuarios.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Raíz de la jerarquía de usuarios. Se guarda en una sola tabla ("usuario");
 * la columna "rol" actúa como discriminador del subtipo.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "rol", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave_hash", nullable = false)
    private String claveHash;

    public String obtenerClaveHash() {
        return claveHash;
    }

    /**
     * Identificador de acceso: correo para clientes y personal, nombre de usuario
     * para el administrador.
     */
    public abstract String obtenerTokenAcceso();

    public abstract RolUsuario obtenerRol();
}
