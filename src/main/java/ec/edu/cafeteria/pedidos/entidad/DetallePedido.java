package ec.edu.cafeteria.pedidos.entidad;

import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.productos.entidad.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Línea de detalle de un pedido que vincula un {@link Producto} con su cantidad
 * solicitada y el precio unitario congelado al momento del pedido (RN-NEG-01, RN-NEG-04).
 */
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", insertable = false, updatable = false)
    private BigDecimal subtotal;

    /**
     * Constructor requerido por JPA. No debe usarse directamente desde la lógica de negocio.
     */
    protected DetallePedido() {
    }

    /**
     * Crea una línea de detalle con el precio unitario vigente al momento de la selección
     * o confirmación del pedido.
     *
     * @param producto producto seleccionado del menú
     * @param cantidad cantidad solicitada, debe ser un entero positivo
     * @param precioUnitario precio unitario registrado para el detalle (RN-NEG-04)
     */
    public DetallePedido(Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        if (producto == null) {
            throw new NegocioException("El producto del detalle es obligatorio");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new NegocioException("La cantidad debe ser un número entero positivo");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("El precio unitario debe ser mayor que cero");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    /**
     * Calcula el subtotal de la línea multiplicando el precio unitario almacenado
     * en el detalle por la cantidad solicitada (RN-NEG-03, RN-NEG-04).
     *
     * @return subtotal calculado de la línea
     */
    public BigDecimal calcularSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    /**
     * Actualiza la cantidad solicitada del producto en este detalle y recalcula el subtotal.
     *
     * @param nuevaCantidad nueva cantidad solicitada, debe ser un entero positivo (RN-NEG-01)
     */
    public void actualizarCantidad(Integer nuevaCantidad) {
        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new NegocioException("La cantidad debe ser un número entero positivo");
        }
        this.cantidad = nuevaCantidad;
        this.subtotal = calcularSubtotal();
    }

    /**
     * Asocia este detalle a su pedido padre.
     *
     * @param pedido pedido al que pertenece el detalle
     */
    public void asignarPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /**
     * Devuelve el identificador interno del detalle.
     *
     * @return identificador del detalle
     */
    public Long getId() {
        return id;
    }

    /**
     * Devuelve el pedido al que pertenece este detalle.
     *
     * @return pedido padre
     */
    public Pedido getPedido() {
        return pedido;
    }

    /**
     * Devuelve el producto referenciado por este detalle.
     *
     * @return producto del menú
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Devuelve la cantidad solicitada del producto.
     *
     * @return cantidad entera positiva
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Devuelve el precio unitario registrado para el detalle (inmutable tras confirmar).
     *
     * @return precio unitario congelado
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Devuelve el subtotal vigente del detalle.
     *
     * @return subtotal calculado
     */
    public BigDecimal getSubtotal() {
        return calcularSubtotal();
    }
}
