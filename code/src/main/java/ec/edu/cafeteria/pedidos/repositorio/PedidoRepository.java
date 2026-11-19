package ec.edu.cafeteria.pedidos.repositorio;

import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.entidad.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("select p from Pedido p where p.codigoRetiro = :codigoRetiro")
    Optional<Pedido> buscarPorCodigoRetiro(@Param("codigoRetiro") String codigoRetiro);

    @Query("select count(p) > 0 from Pedido p where p.codigoRetiro = :codigoRetiro")
    boolean existeCodigoRetiro(@Param("codigoRetiro") String codigoRetiro);

    @Query("select p from Pedido p order by p.fechaRegistro desc")
    List<Pedido> buscarTodos();

    @Query("select p from Pedido p where p.estado = :estado order by p.fechaRegistro desc")
    List<Pedido> buscarPorEstado(@Param("estado") EstadoPedido estado);

    @Query("select p from Pedido p where p.cliente.codigo = :codigoCliente order by p.fechaRegistro desc")
    List<Pedido> buscarPorCliente(@Param("codigoCliente") String codigoCliente);
}
