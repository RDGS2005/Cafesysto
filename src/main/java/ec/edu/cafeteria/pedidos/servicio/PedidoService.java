package ec.edu.cafeteria.pedidos.servicio;

import ec.edu.cafeteria.comun.excepcion.AccesoDenegadoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.comun.excepcion.NoAutenticadoException;
import ec.edu.cafeteria.comun.excepcion.RecursoNoEncontradoException;
import ec.edu.cafeteria.pedidos.dto.SeleccionProductoRequest;
import ec.edu.cafeteria.pedidos.entidad.DetallePedido;
import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;
import ec.edu.cafeteria.pedidos.entidad.Pedido;
import ec.edu.cafeteria.pedidos.repositorio.PedidoRepository;
import ec.edu.cafeteria.productos.entidad.Producto;
import ec.edu.cafeteria.productos.servicio.ProductoService;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Servicio de lógica de negocio del módulo de pedidos.
 *
 * <p>Implementa las historias de usuario RFP-01, RFP-02, RFP-03, RFP-04, RFP-05-1 y RFP-05-2,
 * garantizando el cumplimiento de las reglas de negocio RN-NEG-01 a RN-NEG-07 y el control
 * de acceso por rol autenticado.</p>
 */
@Service
public class PedidoService {

    private static final String PREFIJO_CODIGO_RETIRO = "PED-";
    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_SUFIJO_CODIGO = 6;
    private static final int MAX_INTENTOS_CODIGO = 20;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PedidoRepository pedidoRepository;
    private final ProductoService productoService;
    private final AutenticacionService autenticacionService;

    /**
     * Crea el servicio de pedidos con sus dependencias.
     *
     * @param pedidoRepository repositorio de persistencia de pedidos
     * @param productoService servicio del módulo de productos
     * @param autenticacionService servicio de autenticación y validación de roles
     */
    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoService productoService,
                         AutenticacionService autenticacionService) {
        this.pedidoRepository = pedidoRepository;
        this.productoService = productoService;
        this.autenticacionService = autenticacionService;
    }

    /**
     * Crea una línea de detalle para la selección previa del cliente verificando que la
     * cantidad sea entera positiva y que el producto exista y esté disponible (RFP-01, RN-NEG-01, RN-NEG-02).
     *
     * @param codigoProducto código único del producto en el menú
     * @param cantidad cantidad solicitada del producto
     * @return detalle creado con el precio vigente del producto
     */
    @Transactional(readOnly = true)
    public DetallePedido crearDetalleSeleccion(String codigoProducto, int cantidad) {
        if (!validarCantidadPositiva(cantidad)) {
            throw new NegocioException("La cantidad debe ser un número entero positivo");
        }
        if (codigoProducto == null || codigoProducto.isBlank()) {
            throw new NegocioException("El código del producto es obligatorio");
        }
        Producto producto = productoService.buscarPorCodigo(codigoProducto.trim());
        if (!producto.estaDisponible()) {
            throw new NegocioException("El producto no está disponible: " + producto.getNombre());
        }
        return new DetallePedido(producto, cantidad, producto.getPrecio());
    }

    /**
     * Calcula el importe total de una lista de detalles sumando sus subtotales (RFP-02, RN-NEG-03).
     *
     * @param detalles líneas de detalle seleccionadas
     * @return suma de subtotales, o {@link BigDecimal#ZERO} si no hay detalles
     */
    public BigDecimal calcularImporteTotal(List<DetallePedido> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido detalle : detalles) {
            if (detalle != null) {
                total = total.add(detalle.calcularSubtotal());
            }
        }
        return total;
    }

    /**
     * Confirma un pedido a partir de los ítems enviados por el cliente, validando primero
     * la sesión autenticada antes de consultar el catálogo de productos (RFP-03, RN-NEG-01 a RN-NEG-06).
     *
     * @param contexto identidad autenticada del cliente
     * @param items lista de productos y cantidades seleccionadas
     * @return pedido confirmado y persistido en estado {@link EstadoPedido#PENDIENTE}
     */
    @Transactional
    public Pedido confirmarSeleccion(ContextoAutenticado contexto, List<SeleccionProductoRequest> items) {
        Cliente cliente = autenticacionService.obtenerClienteAutenticado(contexto);
        if (items == null || items.isEmpty()) {
            throw new NegocioException("El pedido debe contener al menos un producto");
        }
        Map<String, Integer> cantidadesPorCodigo = new LinkedHashMap<>();
        for (SeleccionProductoRequest item : items) {
            if (item == null || item.codigoProducto() == null || item.codigoProducto().isBlank()) {
                throw new NegocioException("Cada ítem debe indicar un código de producto válido");
            }
            if (!validarCantidadPositiva(item.cantidad())) {
                throw new NegocioException("La cantidad debe ser un número entero positivo");
            }
            String codigo = item.codigoProducto().trim();
            try {
                cantidadesPorCodigo.merge(codigo, item.cantidad(), Math::addExact);
            } catch (ArithmeticException ex) {
                throw new NegocioException("La cantidad total del producto excede el límite permitido");
            }
        }
        return crearYGuardarPedido(cliente, cantidadesPorCodigo);
    }

    /**
     * Confirma un pedido del cliente autenticado a partir de detalles ya construidos,
     * revalidando disponibilidad y precio vigente de cada producto, consolidando ítems
     * repetidos, generando un código de retiro único y registrando el pedido en estado
     * {@link EstadoPedido#PENDIENTE} (RFP-03, RN-NEG-01 a RN-NEG-06).
     *
     * @param contexto identidad autenticada del solicitante (debe ser {@link RolUsuario#CLIENTE})
     * @param detalles líneas de productos seleccionadas
     * @return pedido confirmado y persistido
     */
    @Transactional
    public Pedido confirmarPedido(ContextoAutenticado contexto, List<DetallePedido> detalles) {
        Cliente cliente = autenticacionService.obtenerClienteAutenticado(contexto);
        if (!validarSeleccion(detalles)) {
            throw new NegocioException("El pedido debe contener al menos un producto");
        }
        Map<String, Integer> cantidadesPorCodigo = consolidarCantidades(detalles);
        return crearYGuardarPedido(cliente, cantidadesPorCodigo);
    }

    /**
     * Consulta el estado actual de un pedido por su código de retiro (RFP-04).
     *
     * <p>Un cliente solo puede consultar el estado de sus propios pedidos; el personal
     * puede consultar el estado de cualquier pedido.</p>
     *
     * @param contexto identidad autenticada del solicitante
     * @param codigoRetiro código único de retiro del pedido
     * @return estado actual del pedido
     */
    @Transactional(readOnly = true)
    public EstadoPedido consultarEstado(ContextoAutenticado contexto, String codigoRetiro) {
        return buscarPorCodigoRetiro(contexto, codigoRetiro).getEstado();
    }

    /**
     * Devuelve el historial de pedidos confirmados del cliente autenticado (RFP-04).
     *
     * @param contexto identidad autenticada del cliente
     * @return lista de pedidos del cliente ordenados del más reciente al más antiguo
     */
    @Transactional(readOnly = true)
    public List<Pedido> consultarMisPedidos(ContextoAutenticado contexto) {
        Cliente cliente = autenticacionService.obtenerClienteAutenticado(contexto);
        return pedidoRepository.buscarPorCliente(cliente.obtenerCodigo());
    }

    /**
     * Devuelve todos los pedidos confirmados para el personal de cafetería (RFP-05-1).
     *
     * @param contexto identidad autenticada del personal
     * @return lista completa de pedidos confirmados
     */
    @Transactional(readOnly = true)
    public List<Pedido> consultarPedidos(ContextoAutenticado contexto) {
        exigirPersonal(contexto);
        return pedidoRepository.buscarTodos();
    }

    /**
     * Devuelve los pedidos filtrados por un estado específico para el personal de cafetería (RFP-05-1).
     *
     * @param contexto identidad autenticada del personal
     * @param estado estado por el cual filtrar; si es {@code null} devuelve todos
     * @return lista de pedidos en el estado solicitado
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(ContextoAutenticado contexto, EstadoPedido estado) {
        exigirPersonal(contexto);
        if (estado == null) {
            return pedidoRepository.buscarTodos();
        }
        return pedidoRepository.buscarPorEstado(estado);
    }

    /**
     * Busca un pedido por su código de retiro aplicando control de acceso por rol (RFP-04, RFP-05-1).
     *
     * <p>Si un cliente intenta consultar un pedido que pertenece a otro cliente, se responde
     * con {@link RecursoNoEncontradoException} (404) para no revelar su existencia.</p>
     *
     * @param contexto identidad autenticada del solicitante
     * @param codigoRetiro código de retiro del pedido
     * @return pedido encontrado
     */
    @Transactional(readOnly = true)
    public Pedido buscarPorCodigoRetiro(ContextoAutenticado contexto, String codigoRetiro) {
        if (contexto == null) {
            throw new NoAutenticadoException("Debe iniciar sesión");
        }
        if (contexto.rol() == RolUsuario.CLIENTE) {
            Cliente cliente = autenticacionService.obtenerClienteAutenticado(contexto);
            Pedido pedido = buscarPedidoExistente(codigoRetiro);
            if (!perteneceAlCliente(pedido, cliente)) {
                throw new RecursoNoEncontradoException("Pedido no encontrado");
            }
            return pedido;
        }
        if (contexto.rol() == RolUsuario.PERSONAL) {
            exigirPersonal(contexto);
            return buscarPedidoExistente(codigoRetiro);
        }
        throw new AccesoDenegadoException("No tiene permiso para esta operación");
    }

    /**
     * Avanza un pedido exactamente un paso en su flujo de preparación:
     * {@link EstadoPedido#PENDIENTE} &rarr; {@link EstadoPedido#EN_PREPARACION} &rarr;
     * {@link EstadoPedido#LISTO_PARA_RETIRAR} (RFP-05-2, RN-NEG-06).
     *
     * <p>No permite pasar a {@link EstadoPedido#ENTREGADO} sin validar el código de retiro
     * mediante {@link #entregarPedido(ContextoAutenticado, String, String)} (RN-NEG-07).</p>
     *
     * @param contexto identidad autenticada del personal
     * @param codigoRetiro código de retiro del pedido a avanzar
     * @return pedido actualizado con el nuevo estado
     */
    @Transactional
    public Pedido avanzarEstado(ContextoAutenticado contexto, String codigoRetiro) {
        Personal personal = exigirPersonal(contexto);
        Pedido pedido = buscarPedidoExistente(codigoRetiro);
        EstadoPedido estadoActual = pedido.getEstado();

        EstadoPedido siguienteEstado = switch (estadoActual) {
            case PENDIENTE -> EstadoPedido.EN_PREPARACION;
            case EN_PREPARACION -> EstadoPedido.LISTO_PARA_RETIRAR;
            case LISTO_PARA_RETIRAR -> throw new NegocioException(
                    "Para entregar el pedido debe validar el código de retiro del cliente");
            case ENTREGADO -> throw new NegocioException(
                    "El pedido ya fue entregado y no puede cambiar de estado");
        };

        if (!validarTransicionEstado(estadoActual, siguienteEstado)) {
            throw new NegocioException("Transición de estado no permitida");
        }

        pedido.asignarPersonal(personal);
        pedido.cambiarEstado(siguienteEstado);
        return pedidoRepository.save(pedido);
    }

    /**
     * Marca un pedido como {@link EstadoPedido#ENTREGADO} únicamente si se encuentra en
     * {@link EstadoPedido#LISTO_PARA_RETIRAR} y el código presentado coincide con su
     * código de retiro (RFP-05-2, RN-NEG-06, RN-NEG-07).
     *
     * @param contexto identidad autenticada del personal
     * @param codigoRetiro código de retiro del pedido a entregar
     * @param codigoPresentado código presentado por el cliente al retirar
     * @return pedido actualizado en estado {@link EstadoPedido#ENTREGADO}
     */
    @Transactional
    public Pedido entregarPedido(ContextoAutenticado contexto, String codigoRetiro, String codigoPresentado) {
        Personal personal = exigirPersonal(contexto);
        Pedido pedido = buscarPedidoExistente(codigoRetiro);

        if (!pedido.estaListoParaRetirar()) {
            throw new NegocioException("Solo se pueden entregar pedidos que estén listos para retirar");
        }
        if (!validarCodigoRetiro(pedido, codigoPresentado)) {
            throw new NegocioException("El código de retiro presentado no coincide con el pedido");
        }
        if (!validarTransicionEstado(pedido.getEstado(), EstadoPedido.ENTREGADO)) {
            throw new NegocioException("Transición de estado no permitida");
        }

        pedido.asignarPersonal(personal);
        pedido.cambiarEstado(EstadoPedido.ENTREGADO);
        return pedidoRepository.save(pedido);
    }

    private Pedido crearYGuardarPedido(Cliente cliente, Map<String, Integer> cantidadesPorCodigo) {
        Pedido pedido = new Pedido(generarCodigoRetiro(), cliente);

        for (Map.Entry<String, Integer> entrada : cantidadesPorCodigo.entrySet()) {
            Producto productoVigente = productoService.buscarPorCodigo(entrada.getKey());
            if (!productoVigente.estaDisponible()) {
                throw new NegocioException("El producto no está disponible: " + productoVigente.getNombre());
            }
            DetallePedido detalleConfirmado = new DetallePedido(
                    productoVigente,
                    entrada.getValue(),
                    productoVigente.getPrecio()
            );
            pedido.agregarDetalle(detalleConfirmado);
        }

        pedido.calcularTotal();
        return pedidoRepository.save(pedido);
    }

    private Personal exigirPersonal(ContextoAutenticado contexto) {
        return (Personal) autenticacionService.exigirRol(contexto, RolUsuario.PERSONAL);
    }

    private Pedido buscarPedidoExistente(String codigoRetiro) {
        if (codigoRetiro == null || codigoRetiro.isBlank()) {
            throw new RecursoNoEncontradoException("Pedido no encontrado");
        }
        String codigoNormalizado = codigoRetiro.trim().toUpperCase(Locale.ROOT);
        return pedidoRepository.buscarPorCodigoRetiro(codigoNormalizado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
    }

    private boolean perteneceAlCliente(Pedido pedido, Cliente cliente) {
        if (pedido.getCliente() == null || cliente == null) {
            return false;
        }
        String codigoPedido = pedido.getCliente().obtenerCodigo();
        String codigoCliente = cliente.obtenerCodigo();
        if (codigoPedido != null && codigoCliente != null) {
            return codigoPedido.equals(codigoCliente);
        }
        return pedido.getCliente().getId() != null && pedido.getCliente().getId().equals(cliente.getId());
    }

    private Map<String, Integer> consolidarCantidades(List<DetallePedido> detalles) {
        Map<String, Integer> cantidadesPorCodigo = new LinkedHashMap<>();
        for (DetallePedido detalle : detalles) {
            if (detalle == null || detalle.getProducto() == null
                    || detalle.getProducto().getCodigo() == null
                    || detalle.getProducto().getCodigo().isBlank()) {
                throw new NegocioException("Cada detalle debe indicar un producto válido");
            }
            if (detalle.getCantidad() == null || !validarCantidadPositiva(detalle.getCantidad())) {
                throw new NegocioException("La cantidad debe ser un número entero positivo");
            }
            String codigo = detalle.getProducto().getCodigo().trim();
            try {
                cantidadesPorCodigo.merge(codigo, detalle.getCantidad(), Math::addExact);
            } catch (ArithmeticException ex) {
                throw new NegocioException("La cantidad total del producto excede el límite permitido");
            }
        }
        return cantidadesPorCodigo;
    }

    private boolean validarSeleccion(List<DetallePedido> detalles) {
        return detalles != null && !detalles.isEmpty();
    }

    private boolean validarCantidadPositiva(int cantidad) {
        return cantidad > 0;
    }

    private boolean validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        if (estadoActual == null || nuevoEstado == null) {
            return false;
        }
        return (estadoActual == EstadoPedido.PENDIENTE && nuevoEstado == EstadoPedido.EN_PREPARACION)
                || (estadoActual == EstadoPedido.EN_PREPARACION && nuevoEstado == EstadoPedido.LISTO_PARA_RETIRAR)
                || (estadoActual == EstadoPedido.LISTO_PARA_RETIRAR && nuevoEstado == EstadoPedido.ENTREGADO);
    }

    private boolean validarCodigoRetiro(Pedido pedido, String codigoPresentado) {
        return pedido != null && pedido.verificarCodigoRetiro(codigoPresentado);
    }

    private String generarCodigoRetiro() {
        for (int intento = 0; intento < MAX_INTENTOS_CODIGO; intento++) {
            StringBuilder sb = new StringBuilder(PREFIJO_CODIGO_RETIRO);
            for (int i = 0; i < LONGITUD_SUFIJO_CODIGO; i++) {
                sb.append(ALFABETO_CODIGO.charAt(RANDOM.nextInt(ALFABETO_CODIGO.length())));
            }
            String candidato = sb.toString();
            if (!pedidoRepository.existeCodigoRetiro(candidato)) {
                return candidato;
            }
        }
        throw new NegocioException("No se pudo generar un código de retiro único");
    }
}
