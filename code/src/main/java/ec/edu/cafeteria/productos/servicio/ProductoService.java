package ec.edu.cafeteria.productos.servicio;

import ec.edu.cafeteria.productos.dto.RegistroProductoRequest;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.productos.repositorio.ProductoRepository;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private ProductoRepository productoRepository;
    private AutenticacionService autenticacionService;

    public List<Producto> obtenerMenu() {
        return null;
    }

    public List<Producto> listarTodos(ContextoAutenticado contexto) {
        return null;
    }

    public Producto registrar(ContextoAutenticado contexto, RegistroProductoRequest datos) {
        return null;
    }

    public void actualizarPrecio(ContextoAutenticado contexto, String codigo, BigDecimal precio) {
    }

    public void actualizarDisponibilidad(ContextoAutenticado contexto, String codigo, EstadoProducto estado) {
    }

    public Producto buscarPorCodigo(String codigo) {
        return null;
    }

    public boolean estaDisponible(String codigo) {
        return false;
    }

    public BigDecimal obtenerPrecioVigente(String codigo) {
        return null;
    }

    private boolean validarPrecio(BigDecimal precio) {
        return false;
    }

    private boolean validarNombreUnico(String nombre) {
        return false;
    }

    private String generarCodigo() {
        return null;
    }
}
