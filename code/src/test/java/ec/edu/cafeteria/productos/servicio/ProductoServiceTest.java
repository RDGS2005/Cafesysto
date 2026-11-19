package ec.edu.cafeteria.productos.servicio;

import ec.edu.cafeteria.productos.repositorio.ProductoRepository;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AutenticacionService autenticacionService;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void obtenerMenu_devuelveSoloProductosDisponibles() {
    }

    @Test
    void listarTodos_devuelveTodosLosProductosParaElPersonal() {
    }

    @Test
    void listarTodos_rechazaSiNoEsPersonal() {
    }

    @Test
    void registrar_guardaElProductoConCodigoGeneradoYDisponiblePorDefecto() {
    }

    @Test
    void registrar_respetaElEstadoIndicadoPorElPersonal() {
    }

    @Test
    void registrar_rechazaPrecioNoPositivo() {
    }

    @Test
    void registrar_rechazaNombreDuplicado() {
    }

    @Test
    void registrar_rechazaSiNoEsPersonal() {
    }

    @Test
    void actualizarPrecio_cambiaElPrecioDelProducto() {
    }

    @Test
    void actualizarPrecio_rechazaPrecioNoPositivo() {
    }

    @Test
    void actualizarPrecio_rechazaSiNoEsPersonal() {
    }

    @Test
    void actualizarDisponibilidad_marcaNoDisponibleSinBorrarElProducto() {
    }

    @Test
    void actualizarDisponibilidad_permiteVolverAHabilitarElProducto() {
    }

    @Test
    void buscarPorCodigo_devuelveElProducto() {
    }

    @Test
    void buscarPorCodigo_rechazaCodigoInexistente() {
    }

    @Test
    void estaDisponible_devuelveElEstadoDeDisponibilidad() {
    }

    @Test
    void obtenerPrecioVigente_devuelveElPrecioActual() {
    }
}
