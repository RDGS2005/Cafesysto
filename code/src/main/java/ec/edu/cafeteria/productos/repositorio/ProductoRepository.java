package ec.edu.cafeteria.productos.repositorio;

import ec.edu.cafeteria.productos.entidad.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("select p from Producto p where p.codigo = :codigo")
    Optional<Producto> buscarPorCodigo(@Param("codigo") String codigo);

    @Query("select count(p) > 0 from Producto p where lower(p.nombre) = lower(:nombre)")
    boolean existeNombre(@Param("nombre") String nombre);

    @Query("select p from Producto p order by p.nombre")
    List<Producto> buscarTodos();

    @Query("select p from Producto p where p.estado = ec.edu.cafeteria.productos.entidad.EstadoProducto.DISPONIBLE order by p.nombre")
    List<Producto> buscarDisponibles();
}
