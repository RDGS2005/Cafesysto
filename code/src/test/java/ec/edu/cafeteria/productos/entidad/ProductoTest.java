package ec.edu.cafeteria.productos.entidad;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la entidad {@link Producto}.
 */
class ProductoTest {

    private Producto crearProducto(EstadoProducto estado) {
        return new Producto("PRD-CAFE01", "Café", new BigDecimal("1.50"), estado);
    }

    @Test
    void cambiarPrecio_actualizaElPrecio() {
        Producto producto = crearProducto(EstadoProducto.DISPONIBLE);
        BigDecimal nuevoPrecio = new BigDecimal("2.00");

        producto.cambiarPrecio(nuevoPrecio);

        assertEquals(nuevoPrecio, producto.getPrecio());
    }

    @Test
    void cambiarDisponibilidad_actualizaElEstado() {
        Producto producto = crearProducto(EstadoProducto.DISPONIBLE);

        producto.cambiarDisponibilidad(EstadoProducto.NO_DISPONIBLE);

        assertEquals(EstadoProducto.NO_DISPONIBLE, producto.getEstado());
    }

    @Test
    void estaDisponible_devuelveTrueSiElEstadoEsDisponible() {
        Producto producto = crearProducto(EstadoProducto.DISPONIBLE);

        assertTrue(producto.estaDisponible());
    }

    @Test
    void estaDisponible_devuelveFalseSiElEstadoEsNoDisponible() {
        Producto producto = crearProducto(EstadoProducto.NO_DISPONIBLE);

        assertFalse(producto.estaDisponible());
    }
}
