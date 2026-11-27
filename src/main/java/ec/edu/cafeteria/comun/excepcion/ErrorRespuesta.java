package ec.edu.cafeteria.comun.excepcion;

import java.time.OffsetDateTime;
import java.util.List;

/** Cuerpo de error que devuelven todos los controladores. */
public record ErrorRespuesta(int estado, String mensaje, List<String> detalles, OffsetDateTime fecha) {

    public static ErrorRespuesta de(int estado, String mensaje, List<String> detalles) {
        return new ErrorRespuesta(estado, mensaje, detalles, OffsetDateTime.now());
    }
}
