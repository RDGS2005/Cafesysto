package ec.edu.cafeteria.productos.controlador;

import ec.edu.cafeteria.productos.servicio.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @Test
    void consultarMenu_devuelveLosProductosDisponibles() {
    }

    @Test
    void listarProductos_devuelveTodosLosProductos() {
    }

    @Test
    void registrarProducto_devuelveElProductoRegistrado() {
    }

    @Test
    void actualizarPrecio_delegaEnElServicio() {
    }

    @Test
    void actualizarDisponibilidad_delegaEnElServicio() {
    }
}
