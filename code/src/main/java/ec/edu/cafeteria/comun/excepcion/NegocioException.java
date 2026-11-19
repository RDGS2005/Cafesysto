package ec.edu.cafeteria.comun.excepcion;

import org.springframework.http.HttpStatus;

/** Regla de negocio incumplida (400). */
public class NegocioException extends ApiException {

    public NegocioException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, mensaje);
    }
}
