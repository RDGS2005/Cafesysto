package ec.edu.cafeteria.comun.excepcion;

import org.springframework.http.HttpStatus;

/** Rol incorrecto o cambio de contraseña pendiente (403). */
public class AccesoDenegadoException extends ApiException {

    public AccesoDenegadoException(String mensaje) {
        super(HttpStatus.FORBIDDEN, mensaje);
    }
}
