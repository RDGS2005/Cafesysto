package ec.edu.cafeteria.productos.controlador;

import ec.edu.cafeteria.productos.dto.ActualizarDisponibilidadRequest;
import ec.edu.cafeteria.productos.dto.ActualizarPrecioRequest;
import ec.edu.cafeteria.productos.dto.ProductoResponse;
import ec.edu.cafeteria.productos.dto.RegistroProductoRequest;
import ec.edu.cafeteria.productos.servicio.ProductoService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private ProductoService productoService;

    @GetMapping("/menu")
    public List<ProductoResponse> consultarMenu() {
        return null;
    }

    @GetMapping
    public List<ProductoResponse> listarProductos(ContextoAutenticado contexto) {
        return null;
    }

    @PostMapping
    public ProductoResponse registrarProducto(ContextoAutenticado contexto,
                                              @RequestBody RegistroProductoRequest datos) {
        return null;
    }

    @PatchMapping("/{codigo}/precio")
    public void actualizarPrecio(ContextoAutenticado contexto, @PathVariable String codigo,
                                 @RequestBody ActualizarPrecioRequest datos) {
    }

    @PatchMapping("/{codigo}/disponibilidad")
    public void actualizarDisponibilidad(ContextoAutenticado contexto, @PathVariable String codigo,
                                         @RequestBody ActualizarDisponibilidadRequest datos) {
    }
}
