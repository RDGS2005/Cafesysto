package ec.edu.cafeteria.productos.servicio;

import ec.edu.cafeteria.comun.excepcion.ConflictoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.comun.excepcion.RecursoNoEncontradoException;
import ec.edu.cafeteria.productos.dto.RegistroProductoRequest;
import ec.edu.cafeteria.productos.entidad.EstadoProducto;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.productos.repositorio.ProductoRepository;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

/**
 * Reglas de negocio del módulo de productos.
 *
 * <p>Las operaciones de gestión del menú exigen el rol {@link RolUsuario#PERSONAL}.
 * Los métodos {@link #buscarPorCodigo(String)}, {@link #estaDisponible(String)} y
 * {@link #obtenerPrecioVigente(String)} son el contrato que usa el módulo de pedidos.</p>
 */
@Service
public class ProductoService {

    private static final String PREFIJO_CODIGO = "PRD-";
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_CODIGO = 6;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final ProductoRepository productoRepository;
    private final AutenticacionService autenticacionService;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param productoRepository repositorio de productos
     * @param autenticacionService servicio que verifica el rol del usuario
     */
    public ProductoService(ProductoRepository productoRepository,
                           AutenticacionService autenticacionService) {
        this.productoRepository = productoRepository;
        this.autenticacionService = autenticacionService;
    }

    /**
     * Devuelve el menú para el cliente: solo los productos disponibles (RFM-01).
     *
     * @return productos disponibles ordenados por nombre
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerMenu() {
        return productoRepository.buscarDisponibles();
    }

    /**
     * Devuelve todos los productos, incluidos los no disponibles, para que el personal
     * pueda volver a habilitarlos (RFM-03-2).
     *
     * @param contexto usuario autenticado
     * @return todos los productos ordenados por nombre
     * @throws ec.edu.cafeteria.comun.excepcion.ApiException si el usuario no es personal
     */
    @Transactional(readOnly = true)
    public List<Producto> listarTodos(ContextoAutenticado contexto) {
        exigirPersonal(contexto);
        return productoRepository.buscarTodos();
    }

    /**
     * Registra un producto en el menú con un código generado por el sistema (RFM-02).
     * Si no se indica estado, el producto queda disponible.
     *
     * @param contexto usuario autenticado
     * @param datos nombre, precio y estado opcional del producto
     * @return el producto registrado
     * @throws NegocioException si el nombre está vacío o el precio no es mayor que cero
     * @throws ConflictoException si ya existe un producto con el mismo nombre
     * @throws ec.edu.cafeteria.comun.excepcion.ApiException si el usuario no es personal
     */
    @Transactional
    public Producto registrar(ContextoAutenticado contexto, RegistroProductoRequest datos) {
        exigirPersonal(contexto);
        if (datos.nombre() == null || datos.nombre().isBlank()) {
            throw new NegocioException("El nombre del producto es obligatorio");
        }
        exigirPrecioValido(datos.precio());
        String nombre = datos.nombre().trim();
        if (!validarNombreUnico(nombre)) {
            throw new ConflictoException("Ya existe un producto con ese nombre");
        }
        EstadoProducto estado = datos.estado() != null
                ? datos.estado()
                : EstadoProducto.DISPONIBLE;
        Producto producto = new Producto(generarCodigo(), nombre, datos.precio(), estado);
        return productoRepository.save(producto);
    }

    /**
     * Actualiza el precio de un producto (RFM-03-1). Los pedidos ya confirmados
     * conservan su precio (RN-NEG-04).
     *
     * @param contexto usuario autenticado
     * @param codigo código del producto
     * @param precio nuevo precio, mayor que cero
     * @throws NegocioException si el precio no es mayor que cero
     * @throws RecursoNoEncontradoException si no existe un producto con ese código
     * @throws ec.edu.cafeteria.comun.excepcion.ApiException si el usuario no es personal
     */
    @Transactional
    public void actualizarPrecio(ContextoAutenticado contexto, String codigo, BigDecimal precio) {
        exigirPersonal(contexto);
        exigirPrecioValido(precio);
        Producto producto = buscarPorCodigo(codigo);
        producto.cambiarPrecio(precio);
        productoRepository.save(producto);
    }

    /**
     * Actualiza la disponibilidad de un producto (RFM-03-2). Los productos no se
     * eliminan: se marcan como no disponibles.
     *
     * @param contexto usuario autenticado
     * @param codigo código del producto
     * @param estado nuevo estado de disponibilidad
     * @throws NegocioException si no se indica el estado
     * @throws RecursoNoEncontradoException si no existe un producto con ese código
     * @throws ec.edu.cafeteria.comun.excepcion.ApiException si el usuario no es personal
     */
    @Transactional
    public void actualizarDisponibilidad(ContextoAutenticado contexto, String codigo,
                                         EstadoProducto estado) {
        exigirPersonal(contexto);
        if (estado == null) {
            throw new NegocioException("El estado del producto es obligatorio");
        }
        Producto producto = buscarPorCodigo(codigo);
        producto.cambiarDisponibilidad(estado);
        productoRepository.save(producto);
    }

    /**
     * Busca un producto por su código. Forma parte del contrato con el módulo de pedidos.
     *
     * @param codigo código del producto
     * @return el producto encontrado
     * @throws RecursoNoEncontradoException si no existe un producto con ese código
     */
    @Transactional(readOnly = true)
    public Producto buscarPorCodigo(String codigo) {
        return productoRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el producto " + codigo));
    }

    /**
     * Indica si un producto puede incluirse en nuevos pedidos (RN-NEG-02). Forma parte
     * del contrato con el módulo de pedidos.
     *
     * @param codigo código del producto
     * @return {@code true} si el producto está disponible
     * @throws RecursoNoEncontradoException si no existe un producto con ese código
     */
    @Transactional(readOnly = true)
    public boolean estaDisponible(String codigo) {
        return buscarPorCodigo(codigo).estaDisponible();
    }

    /**
     * Devuelve el precio vigente de un producto para calcular el importe de un pedido
     * (RN-NEG-03). Forma parte del contrato con el módulo de pedidos.
     *
     * @param codigo código del producto
     * @return precio vigente del producto
     * @throws RecursoNoEncontradoException si no existe un producto con ese código
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerPrecioVigente(String codigo) {
        return buscarPorCodigo(codigo).getPrecio();
    }

    private void exigirPersonal(ContextoAutenticado contexto) {
        autenticacionService.exigirRol(contexto, RolUsuario.PERSONAL);
    }

    private void exigirPrecioValido(BigDecimal precio) {
        if (!validarPrecio(precio)) {
            throw new NegocioException("El precio debe ser mayor que cero");
        }
    }

    private boolean validarPrecio(BigDecimal precio) {
        return precio != null && precio.signum() > 0;
    }

    private boolean validarNombreUnico(String nombre) {
        return !productoRepository.existeNombre(nombre);
    }

    private String generarCodigo() {
        String codigo;
        do {
            StringBuilder constructor = new StringBuilder(PREFIJO_CODIGO);
            for (int i = 0; i < LONGITUD_CODIGO; i++) {
                int posicion = ALEATORIO.nextInt(ALFABETO_CODIGO.length());
                constructor.append(ALFABETO_CODIGO.charAt(posicion));
            }
            codigo = constructor.toString();
        } while (productoRepository.buscarPorCodigo(codigo).isPresent());
        return codigo;
    }
}
