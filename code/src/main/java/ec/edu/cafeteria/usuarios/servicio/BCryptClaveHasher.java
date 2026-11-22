package ec.edu.cafeteria.usuarios.servicio;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptClaveHasher implements ClaveHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String generarHash(String clave) {
        return encoder.encode(clave);
    }

    @Override
    public boolean verificar(String clave, String hash) {
        return encoder.matches(clave, hash);
    }
}
