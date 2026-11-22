package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.config.CafeteriaProperties;
import ec.edu.cafeteria.usuarios.entidad.Administrador;
import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RFU-06: crea el administrador inicial una sola vez. Guarda solo el hash y
 * exige cambiar la
 * contraseña en el primer acceso. Reiniciar la aplicación no restablece las
 * credenciales.
 */
@Component
public class AdministradorInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdministradorInicializador.class);

    private final UsuarioRepository usuarioRepository;
    private final ClaveHasher claveHasher;
    private final CafeteriaProperties propiedades;

    public AdministradorInicializador(UsuarioRepository usuarioRepository, ClaveHasher claveHasher,
            CafeteriaProperties propiedades) {
        this.usuarioRepository = usuarioRepository;
        this.claveHasher = claveHasher;
        this.propiedades = propiedades;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existeAdministrador()) {
            return;
        }
        Administrador admin = new Administrador();
        admin.setNombreUsuario(propiedades.admin().usuario());
        admin.setClaveHash(claveHasher.generarHash(propiedades.admin().clave()));
        admin.setRequiereCambioClave(true);
        usuarioRepository.save(admin);
        log.info("Administrador inicial creado: '{}'. Debe cambiar la contraseña en el primer acceso.",
                propiedades.admin().usuario());
    }
}
