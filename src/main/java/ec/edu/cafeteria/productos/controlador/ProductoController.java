package ec.edu.cafeteria.productos.controlador;

import ec.edu.cafeteria.productos.dto.ActualizarDisponibilidadRequest;
import ec.edu.cafeteria.productos.dto.ActualizarPrecioRequest;
import ec.edu.cafeteria.productos.dto.ProductoResponse;
import ec.edu.cafeteria.productos.dto.RegistroProductoRequest;
import ec.edu.cafeteria.productos.servicio.ProductoService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Punto de entrada HTTP del módulo de productos. Delega toda la lógica en
 * {@link ProductoService}.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Crea el controlador con su servicio.
     *
     * @param productoService servicio del módulo de productos
     */
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Consulta el menú: solo los productos disponibles (RFM-01).
     *
     * @return productos disponibles
     */
    @GetMapping("/menu")
    public List<ProductoResponse> consultarMenu() {
        return productoService.obtenerMenu().stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    /**
     * Lista todos los productos, incluidos los no disponibles, para el personal.
     *
     * @param contexto usuario autenticado
     * @return todos los productos
     */
    @GetMapping
    public List<ProductoResponse> listarProductos(ContextoAutenticado contexto) {
        return productoService.listarTodos(contexto).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    /**
     * Registra un producto en el menú (RFM-02).
     *
     * @param contexto usuario autenticado
     * @param datos datos del producto
     * @return el producto registrado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse registrarProducto(ContextoAutenticado contexto,
                                              @Valid @RequestBody RegistroProductoRequest datos) {
        return ProductoResponse.desde(productoService.registrar(contexto, datos));
    }

    /**
     * Actualiza el precio de un producto (RFM-03-1).
     *
     * @param contexto usuario autenticado
     * @param codigo código del producto
     * @param datos nuevo precio
     */
    @PatchMapping("/{codigo}/precio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizarPrecio(ContextoAutenticado contexto, @PathVariable String codigo,
                                 @Valid @RequestBody ActualizarPrecioRequest datos) {
        productoService.actualizarPrecio(contexto, codigo, datos.precio());
    }

    /**
     * Actualiza la disponibilidad de un producto (RFM-03-2).
     *
     * @param contexto usuario autenticado
     * @param codigo código del producto
     * @param datos nuevo estado de disponibilidad
     */
    @PatchMapping("/{codigo}/disponibilidad")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizarDisponibilidad(
            ContextoAutenticado contexto,
            @PathVariable String codigo,
            @Valid @RequestBody ActualizarDisponibilidadRequest datos) {
        productoService.actualizarDisponibilidad(contexto, codigo, datos.estado());
    }
}
