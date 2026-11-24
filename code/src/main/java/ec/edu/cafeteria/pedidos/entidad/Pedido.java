package ec.edu.cafeteria.pedidos.entidad;

import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pedido confirmado realizado por un {@link Cliente} en la cafetería universitaria.
 *
 * <p>Al confirmarse inicia en estado {@link EstadoPedido#PENDIENTE} con un código de
 * retiro único (RN-NEG-05, RN-NEG-06) y conserva sus {@link DetallePedido} con el
 * precio unitario del momento de la confirmación (RN-NEG-04).</p>
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_retiro", nullable = false, updatable = false)
    private String codigoRetiro;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private OffsetDateTime fechaRegistro;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "cliente_rol", nullable = false, updatable = false)
    private RolUsuario clienteRol = RolUsuario.CLIENTE;

    @ManyToOne
    @JoinColumn(name = "personal_id")
    private Personal personal;

    @Enumerated(EnumType.STRING)
    @Column(name = "personal_rol")
    private RolUsuario personalRol;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetallePedido> detalles = new ArrayList<>();

    /**
     * Constructor requerido por JPA. No debe usarse directamente desde la lógica de negocio.
     */
    protected Pedido() {
    }

    /**
     * Crea un pedido confirmado en estado inicial {@link EstadoPedido#PENDIENTE}.
     *
     * @param codigoRetiro código único de retiro asignado al pedido (RN-NEG-05)
     * @param cliente cliente autenticado propietario del pedido
     */
    public Pedido(String codigoRetiro, Cliente cliente) {
        if (codigoRetiro == null || codigoRetiro.isBlank()) {
            throw new NegocioException("El código de retiro es obligatorio");
        }
        if (cliente == null) {
            throw new NegocioException("El cliente del pedido es obligatorio");
        }
        this.codigoRetiro = codigoRetiro.trim();
        this.cliente = cliente;
        this.clienteRol = RolUsuario.CLIENTE;
        this.fechaRegistro = OffsetDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.total = BigDecimal.ZERO;
        this.detalles = new ArrayList<>();
    }

    /**
     * Calcula y actualiza el importe total del pedido sumando los subtotales de
     * todos sus detalles (RN-NEG-03).
     *
     * @return importe total del pedido, o {@link BigDecimal#ZERO} si no tiene detalles
     */
    public BigDecimal calcularTotal() {
        if (detalles == null || detalles.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return this.total;
        }
        BigDecimal suma = BigDecimal.ZERO;
        for (DetallePedido detalle : detalles) {
            suma = suma.add(detalle.calcularSubtotal());
        }
        this.total = suma;
        return this.total;
    }

    /**
     * Agrega una línea de detalle al pedido, establece el vínculo bidireccional
     * y recalcula el importe total.
     *
     * @param detalle línea de detalle a incorporar
     */
    public void agregarDetalle(DetallePedido detalle) {
        if (detalle == null) {
            throw new NegocioException("El detalle del pedido es obligatorio");
        }
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        detalle.asignarPedido(this);
        this.detalles.add(detalle);
        calcularTotal();
    }

    /**
     * Elimina una línea de detalle del pedido y recalcula el importe total.
     *
     * @param detalle línea de detalle a retirar
     */
    public void eliminarDetalle(DetallePedido detalle) {
        if (detalle == null || this.detalles == null) {
            return;
        }
        if (this.detalles.remove(detalle)) {
            detalle.asignarPedido(null);
            calcularTotal();
        }
    }

    /**
     * Actualiza el estado del pedido respetando estrictamente el orden secuencial
     * definido en RN-NEG-06 (sin saltos ni retrocesos).
     *
     * @param nuevoEstado nuevo estado del pedido
     */
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new NegocioException("El nuevo estado del pedido es obligatorio");
        }
        boolean transicionValida = (this.estado == EstadoPedido.PENDIENTE && nuevoEstado == EstadoPedido.EN_PREPARACION)
                || (this.estado == EstadoPedido.EN_PREPARACION && nuevoEstado == EstadoPedido.LISTO_PARA_RETIRAR)
                || (this.estado == EstadoPedido.LISTO_PARA_RETIRAR && nuevoEstado == EstadoPedido.ENTREGADO);
        if (!transicionValida) {
            throw new NegocioException("Transición de estado no permitida: " + this.estado + " -> " + nuevoEstado);
        }
        this.estado = nuevoEstado;
    }

    /**
     * Registra al miembro del personal que procesa o entrega el pedido,
     * manteniendo coherencia con las restricciones de rol en base de datos.
     *
     * @param personal personal de cafetería que atiende el pedido
     */
    public void asignarPersonal(Personal personal) {
        this.personal = personal;
        this.personalRol = (personal != null) ? RolUsuario.PERSONAL : null;
    }

    /**
     * Indica si el pedido se encuentra en estado {@link EstadoPedido#LISTO_PARA_RETIRAR},
     * condición previa para su entrega (RN-NEG-07).
     *
     * @return {@code true} si el pedido está listo para retirar
     */
    public boolean estaListoParaRetirar() {
        return this.estado == EstadoPedido.LISTO_PARA_RETIRAR;
    }

    /**
     * Verifica si el código presentado por el cliente coincide con el código de retiro
     * del pedido (RN-NEG-07).
     *
     * @param codigoPresentado código presentado al momento del retiro
     * @return {@code true} si coincide ignorando espacios laterales y mayúsculas/minúsculas
     */
    public boolean verificarCodigoRetiro(String codigoPresentado) {
        if (this.codigoRetiro == null || codigoPresentado == null || codigoPresentado.isBlank()) {
            return false;
        }
        return this.codigoRetiro.equalsIgnoreCase(codigoPresentado.trim());
    }

    /**
     * Devuelve el identificador interno del pedido.
     *
     * @return identificador del pedido
     */
    public Long getId() {
        return id;
    }

    /**
     * Devuelve el código único de retiro del pedido.
     *
     * @return código de retiro
     */
    public String getCodigoRetiro() {
        return codigoRetiro;
    }

    /**
     * Devuelve la fecha y hora de confirmación del pedido.
     *
     * @return fecha de registro
     */
    public OffsetDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Devuelve el importe total del pedido.
     *
     * @return importe total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Devuelve el estado actual del pedido.
     *
     * @return estado del pedido
     */
    public EstadoPedido getEstado() {
        return estado;
    }

    /**
     * Devuelve el cliente propietario del pedido.
     *
     * @return cliente del pedido
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Devuelve el miembro del personal que procesó o entregó el pedido.
     *
     * @return personal asignado, o {@code null} si aún está pendiente
     */
    public Personal getPersonal() {
        return personal;
    }

    /**
     * Devuelve una vista inmutable de los detalles del pedido.
     *
     * @return lista de detalles del pedido
     */
    public List<DetallePedido> getDetalles() {
        if (detalles == null) {
            return List.of();
        }
        return Collections.unmodifiableList(detalles);
    }
}
