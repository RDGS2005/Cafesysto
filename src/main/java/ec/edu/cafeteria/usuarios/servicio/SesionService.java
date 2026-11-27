package ec.edu.cafeteria.usuarios.servicio;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacén de sesiones en memoria, suficiente para el esqueleto.
 * Se pierde al reiniciar y no tiene vencimiento: reemplazar por JWT o Spring
 * Session si se necesita.
 */
@Service
public class SesionService {

    private final Map<String, ContextoAutenticado> sesiones = new ConcurrentHashMap<>();

    public String abrir(ContextoAutenticado contexto) {
        String token = UUID.randomUUID().toString();
        sesiones.put(token, contexto);
        return token;
    }

    public Optional<ContextoAutenticado> obtener(String token) {
        return Optional.ofNullable(sesiones.get(token));
    }

    public void cerrar(String token) {
        sesiones.remove(token);
    }
}
