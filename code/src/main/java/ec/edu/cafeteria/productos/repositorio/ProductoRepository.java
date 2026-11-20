package ec.edu.cafeteria.productos.repositorio;

import ec.edu.cafeteria.productos.entidad.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a los productos persistidos. Hereda de {@link JpaRepository} las operaciones
 * para guardar y actualizar ({@code save}).
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca un producto por su código.
     *
     * @param codigo código del producto
     * @return el producto, o vacío si no existe
     */
    @Query("select p from Producto p where p.codigo = :codigo")
    Optional<Producto> buscarPorCodigo(@Param("codigo") String codigo);

    /**
     * Indica si ya existe un producto con el nombre dado, sin distinguir mayúsculas.
     *
     * @param nombre nombre a comprobar
     * @return {@code true} si el nombre ya está registrado
     */
    @Query("select count(p) > 0 from Producto p where lower(p.nombre) = lower(:nombre)")
    boolean existeNombre(@Param("nombre") String nombre);

    /**
     * Devuelve todos los productos ordenados por nombre.
     *
     * @return lista de productos
     */
    @Query("select p from Producto p order by p.nombre")
    List<Producto> buscarTodos();

    /**
     * Devuelve los productos disponibles ordenados por nombre.
     *
     * @return lista de productos disponibles
     */
    @Query("select p from Producto p"
            + " where p.estado = ec.edu.cafeteria.productos.entidad.EstadoProducto.DISPONIBLE"
            + " order by p.nombre")
    List<Producto> buscarDisponibles();
}
