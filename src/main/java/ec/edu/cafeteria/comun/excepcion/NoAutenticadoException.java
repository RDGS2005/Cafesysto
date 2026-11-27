package ec.edu.cafeteria.comun.excepcion;

import org.springframework.http.HttpStatus;

/** Sin sesión válida o credenciales incorrectas (401). El mensaje es genérico a propósito. */
public class NoAutenticadoException extends ApiException {

    public NoAutenticadoException(String mensaje) {
        super(HttpStatus.UNAUTHORIZED, mensaje);
    }
}
