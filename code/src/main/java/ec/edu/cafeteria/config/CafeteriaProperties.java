package ec.edu.cafeteria.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del sistema Cafesysto.
 * RFU-01: correo institucional de clientes.
 * RFU-06: credenciales iniciales del administrador.
 */
@ConfigurationProperties(prefix = "cafesysto")
public record CafeteriaProperties(
        String correoInstitucionalDominio,
        Admin admin) {

    public record Admin(String usuario, String clave) {
    }
}
