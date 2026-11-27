package ec.edu.cafeteria.comun.excepcion;

import org.springframework.http.HttpStatus;

/** Dato duplicado, por ejemplo cédula, correo o nombre de producto (409). */
public class ConflictoException extends ApiException {

    public ConflictoException(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
