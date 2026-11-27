package ec.edu.cafeteria.pedidos.entidad;

import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias de la entidad {@link DetallePedido}.
 */
class DetallePedidoTest {

    @Test
    void calcularSubtotal_multiplicaPrecioUnitarioPorCantidad() {
        Producto cafe = new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), EstadoProducto.DISPONIBLE);
        DetallePedido detalle = new DetallePedido(cafe, 3, new BigDecimal("1.50"));

        assertEquals(0, new BigDecimal("4.50").compareTo(detalle.calcularSubtotal()));
    }

    @Test
    void calcularSubtotal_usaElPrecioGuardadoEnElDetalle() {
        Producto cafe = new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), EstadoProducto.DISPONIBLE);
        DetallePedido detalle = new DetallePedido(cafe, 2, new BigDecimal("1.50"));

        cafe.cambiarPrecio(new BigDecimal("2.75"));

        assertEquals(0, new BigDecimal("3.00").compareTo(detalle.calcularSubtotal()));
        assertEquals(0, new BigDecimal("1.50").compareTo(detalle.getPrecioUnitario()));
    }

    @Test
    void actualizarCantidad_cambiaLaCantidad() {
        Producto cafe = new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), EstadoProducto.DISPONIBLE);
        DetallePedido detalle = new DetallePedido(cafe, 2, new BigDecimal("1.50"));

        detalle.actualizarCantidad(5);

        assertEquals(5, detalle.getCantidad());
        assertEquals(0, new BigDecimal("7.50").compareTo(detalle.calcularSubtotal()));
    }

    @Test
    void actualizarCantidad_rechazaCantidadNoPositivaONula() {
        Producto cafe = new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), EstadoProducto.DISPONIBLE);
        DetallePedido detalle = new DetallePedido(cafe, 2, new BigDecimal("1.50"));

        assertThrows(NegocioException.class, () -> detalle.actualizarCantidad(0));
        assertThrows(NegocioException.class, () -> detalle.actualizarCantidad(-2));
        assertThrows(NegocioException.class, () -> detalle.actualizarCantidad(null));
    }

    @Test
    void constructor_rechazaParametrosInvalidos() {
        Producto cafe = new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), EstadoProducto.DISPONIBLE);

        assertThrows(NegocioException.class, () -> new DetallePedido(null, 1, new BigDecimal("1.50")));
        assertThrows(NegocioException.class, () -> new DetallePedido(cafe, 0, new BigDecimal("1.50")));
        assertThrows(NegocioException.class, () -> new DetallePedido(cafe, 1, BigDecimal.ZERO));
        assertThrows(NegocioException.class, () -> new DetallePedido(cafe, 1, new BigDecimal("-1.00")));
    }
}
