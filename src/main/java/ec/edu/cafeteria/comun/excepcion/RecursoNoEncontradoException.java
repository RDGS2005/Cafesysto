package ec.edu.cafeteria.comun.excepcion;

import org.springframework.http.HttpStatus;

/** El recurso no existe, o el usuario no puede saber que existe (404). */
public class RecursoNoEncontradoException extends ApiException {

    public RecursoNoEncontradoException(String mensaje) {
        super(HttpStatus.NOT_FOUND, mensaje);
    }
}
