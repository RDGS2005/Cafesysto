package ec.edu.cafeteria.usuarios.repositorio;

import ec.edu.cafeteria.usuarios.entidad.Administrador;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Persona;
import ec.edu.cafeteria.usuarios.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Contrato de persistencia de usuarios. guardar y actualizarCredenciales del
 * diagrama
 * corresponden a {@code save}, que ya hereda de JpaRepository.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select p from Persona p where p.cedula = :cedula")
    Optional<Persona> buscarPorCedula(@Param("cedula") String cedula);

    @Query("select p from Persona p where lower(p.correo) = lower(:correo)")
    Optional<Persona> buscarPorCorreo(@Param("correo") String correo);

    @Query("select c from Cliente c where c.codigo = :codigo")
    Optional<Cliente> buscarPorCodigoCliente(@Param("codigo") String codigo);

    @Query("select count(c) > 0 from Cliente c where c.codigo = :codigo")
    boolean existeCodigoCliente(@Param("codigo") String codigo);

    @Query("select a from Administrador a where lower(a.nombreUsuario) = lower(:nombreUsuario)")
    Optional<Administrador> buscarAdministrador(@Param("nombreUsuario") String nombreUsuario);

    @Query("select count(a) > 0 from Administrador a")
    boolean existeAdministrador();
}
