package ec.edu.cafeteria.pedidos.repositorio;

import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.entidad.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la persistencia y consulta de {@link Pedido}.
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca un pedido por su código único de retiro.
     *
     * @param codigoRetiro código de retiro del pedido
     * @return pedido encontrado, si existe
     */
    @Query("select p from Pedido p where p.codigoRetiro = :codigoRetiro")
    Optional<Pedido> buscarPorCodigoRetiro(@Param("codigoRetiro") String codigoRetiro);

    /**
     * Verifica si ya existe un pedido registrado con el código de retiro indicado (RN-NEG-05).
     *
     * @param codigoRetiro código de retiro a verificar
     * @return {@code true} si el código ya está en uso
     */
    @Query("select count(p) > 0 from Pedido p where p.codigoRetiro = :codigoRetiro")
    boolean existeCodigoRetiro(@Param("codigoRetiro") String codigoRetiro);

    /**
     * Devuelve todos los pedidos confirmados ordenados del más reciente al más antiguo.
     *
     * @return lista de pedidos
     */
    @Query("select p from Pedido p order by p.fechaRegistro desc")
    List<Pedido> buscarTodos();

    /**
     * Devuelve los pedidos que se encuentran en un estado determinado,
     * ordenados del más reciente al más antiguo.
     *
     * @param estado estado por el cual filtrar
     * @return lista de pedidos en el estado indicado
     */
    @Query("select p from Pedido p where p.estado = :estado order by p.fechaRegistro desc")
    List<Pedido> buscarPorEstado(@Param("estado") EstadoPedido estado);

    /**
     * Devuelve los pedidos confirmados de un cliente específico,
     * ordenados del más reciente al más antiguo.
     *
     * @param codigoCliente código único del cliente
     * @return lista de pedidos del cliente
     */
    @Query("select p from Pedido p where p.cliente.codigo = :codigoCliente order by p.fechaRegistro desc")
    List<Pedido> buscarPorCliente(@Param("codigoCliente") String codigoCliente);
}
