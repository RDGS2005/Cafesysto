package ec.edu.cafeteria.productos.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Artículo ofrecido en el menú de la cafetería.
 *
 * <p>El código y el nombre se asignan al registrar el producto y no se modifican
 * después (RFM-02, RFM-03-1, RFM-03-2). Solo cambian el precio y la disponibilidad.</p>
 */
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, updatable = false)
    private String codigo;

    @Column(name = "nombre", nullable = false, updatable = false)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoProducto estado = EstadoProducto.DISPONIBLE;

    /**
     * Constructor requerido por JPA. No debe usarse desde el código de la aplicación.
     */
    protected Producto() {
    }

    /**
     * Crea un producto del menú.
     *
     * @param codigo código único del producto, generado por el sistema
     * @param nombre nombre del producto
     * @param precio precio del producto, mayor que cero
     * @param estado disponibilidad inicial del producto
     */
    public Producto(String codigo, String nombre, BigDecimal precio, EstadoProducto estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.estado = estado;
    }

    /**
     * Reemplaza el precio vigente del producto. Los pedidos ya confirmados conservan
     * el precio con el que se registraron (RN-NEG-04).
     *
     * @param nuevoPrecio nuevo precio del producto
     */
    public void cambiarPrecio(BigDecimal nuevoPrecio) {
        this.precio = nuevoPrecio;
    }

    /**
     * Cambia la disponibilidad del producto en el menú.
     *
     * @param nuevoEstado nuevo estado de disponibilidad
     */
    public void cambiarDisponibilidad(EstadoProducto nuevoEstado) {
        this.estado = nuevoEstado;
    }

    /**
     * Indica si el producto puede incluirse en nuevos pedidos (RN-NEG-02).
     *
     * @return {@code true} si el estado es {@link EstadoProducto#DISPONIBLE}
     */
    public boolean estaDisponible() {
        return estado == EstadoProducto.DISPONIBLE;
    }

    /**
     * Devuelve el identificador interno de la base de datos.
     *
     * @return identificador del producto
     */
    public Long getId() {
        return id;
    }

    /**
     * Devuelve el código único del producto.
     *
     * @return código del producto
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el precio vigente del producto.
     *
     * @return precio del producto
     */
    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * Devuelve la disponibilidad del producto.
     *
     * @return estado del producto
     */
    public EstadoProducto getEstado() {
        return estado;
    }
}
