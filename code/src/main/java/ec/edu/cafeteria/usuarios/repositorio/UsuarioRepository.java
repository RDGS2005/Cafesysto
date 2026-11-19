package ec.edu.cafeteria.usuarios.repositorio;

import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Persona;
import ec.edu.cafeteria.usuarios.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select p from Persona p where p.cedula = :cedula")
    Optional<Persona> buscarPorCedula(@Param("cedula") String cedula);

    @Query("select p from Persona p where lower(p.correo) = lower(:correo)")
    Optional<Persona> buscarPorCorreo(@Param("correo") String correo);

    @Query("select c from Cliente c where c.codigo = :codigoCliente")
    Optional<Cliente> buscarPorCodigoCliente(@Param("codigoCliente") String codigoCliente);

    @Query("select count(c) > 0 from Cliente c where c.codigo = :codigoCliente")
    boolean existeCodigoCliente(@Param("codigoCliente") String codigoCliente);
}
