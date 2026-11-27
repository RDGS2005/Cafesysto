package ec.edu.cafeteria.pedidos.entidad;

import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la entidad {@link Pedido}.
 */
class PedidoTest {

    private Cliente crearCliente() {
        Cliente cliente = new Cliente();
        cliente.setCodigo("CLI-0001");
        return cliente;
    }

    private Producto crearProducto(String codigo, String nombre, String precio) {
        return new Producto(codigo, nombre, new BigDecimal(precio), EstadoProducto.DISPONIBLE);
    }

    @Test
    void calcularTotal_sumaLosSubtotalesDeLosDetalles() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());
        pedido.agregarDetalle(new DetallePedido(crearProducto("PRD-1", "Café", "1.50"), 2, new BigDecimal("1.50")));
        pedido.agregarDetalle(new DetallePedido(crearProducto("PRD-2", "Empanada", "1.25"), 3, new BigDecimal("1.25")));

        BigDecimal total = pedido.calcularTotal();

        assertEquals(0, new BigDecimal("6.75").compareTo(total));
        assertEquals(0, new BigDecimal("6.75").compareTo(pedido.getTotal()));
    }

    @Test
    void calcularTotal_devuelveCeroSiNoHayDetalles() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());

        BigDecimal total = pedido.calcularTotal();

        assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    @Test
    void agregarDetalle_incorporaElDetalleAlPedido() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());
        DetallePedido detalle = new DetallePedido(crearProducto("PRD-1", "Café", "1.50"), 2, new BigDecimal("1.50"));

        pedido.agregarDetalle(detalle);

        assertEquals(1, pedido.getDetalles().size());
        assertEquals(pedido, detalle.getPedido());
        assertEquals(0, new BigDecimal("3.00").compareTo(pedido.getTotal()));
    }

    @Test
    void agregarDetalle_rechazaDetalleNulo() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());

        assertThrows(NegocioException.class, () -> pedido.agregarDetalle(null));
    }

    @Test
    void eliminarDetalle_quitaElDetalleDelPedido() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());
        DetallePedido detalle = new DetallePedido(crearProducto("PRD-1", "Café", "1.50"), 2, new BigDecimal("1.50"));
        pedido.agregarDetalle(detalle);

        pedido.eliminarDetalle(detalle);

        assertTrue(pedido.getDetalles().isEmpty());
        assertNull(detalle.getPedido());
        assertEquals(0, BigDecimal.ZERO.compareTo(pedido.getTotal()));
    }

    @Test
    void cambiarEstado_actualizaElEstado() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());

        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);

        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());
    }

    @Test
    void estaListoParaRetirar_devuelveTrueSoloEnListoParaRetirar() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());
        assertFalse(pedido.estaListoParaRetirar());

        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        assertFalse(pedido.estaListoParaRetirar());

        pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);
        assertTrue(pedido.estaListoParaRetirar());

        pedido.cambiarEstado(EstadoPedido.ENTREGADO);
        assertFalse(pedido.estaListoParaRetirar());
    }

    @Test
    void verificarCodigoRetiro_devuelveTrueSiElCodigoCoincide() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());

        assertTrue(pedido.verificarCodigoRetiro("PED-ABC123"));
        assertTrue(pedido.verificarCodigoRetiro(" ped-abc123 "));
    }

    @Test
    void verificarCodigoRetiro_devuelveFalseSiElCodigoNoCoincide() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());

        assertFalse(pedido.verificarCodigoRetiro("PED-ZZZ999"));
        assertFalse(pedido.verificarCodigoRetiro(""));
        assertFalse(pedido.verificarCodigoRetiro("   "));
        assertFalse(pedido.verificarCodigoRetiro(null));
    }

    @Test
    void cambiarEstado_rechazaSaltosRetrocesosYNulo() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());

        assertThrows(NegocioException.class, () -> pedido.cambiarEstado(null));
        assertThrows(NegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.PENDIENTE));
        assertThrows(NegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR));
        assertThrows(NegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.ENTREGADO));

        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        assertThrows(NegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.PENDIENTE));
        pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR);
        pedido.cambiarEstado(EstadoPedido.ENTREGADO);
        assertThrows(NegocioException.class, () -> pedido.cambiarEstado(EstadoPedido.LISTO_PARA_RETIRAR));
    }

    @Test
    void getDetalles_devuelveVistaInmutable() {
        Pedido pedido = new Pedido("PED-ABC123", crearCliente());
        DetallePedido detalle = new DetallePedido(crearProducto("PRD-1", "Café", "1.50"), 1, new BigDecimal("1.50"));

        assertThrows(UnsupportedOperationException.class, () -> pedido.getDetalles().add(detalle));
    }
}
