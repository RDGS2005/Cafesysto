package ec.edu.cafeteria.comun.excepcion;

import org.springframework.http.HttpStatus;

/** Error de negocio o de acceso con el estado HTTP que debe recibir el cliente. */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus estado;

    protected ApiException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}
