package ec.edu.cafeteria.pedidos.entidad;

/**
 * Estados del ciclo de vida de un pedido confirmado en la cafetería (RN-NEG-06).
 *
 * <p>La progresión válida es estrictamente secuencial:
 * {@link #PENDIENTE} &rarr; {@link #EN_PREPARACION} &rarr;
 * {@link #LISTO_PARA_RETIRAR} &rarr; {@link #ENTREGADO}.</p>
 */
public enum EstadoPedido {

    /** Estado inicial asignado al confirmar el pedido. */
    PENDIENTE,

    /** El personal de cafetería inició la preparación del pedido. */
    EN_PREPARACION,

    /** El pedido está terminado y disponible para retiro en mostrador. */
    LISTO_PARA_RETIRAR,

    /** El pedido fue entregado tras validar el código de retiro (RN-NEG-07). */
    ENTREGADO
}
