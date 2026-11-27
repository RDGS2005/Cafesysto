package ec.edu.cafeteria.usuarios.servicio;

/**
 * Contrato para el hash seguro de contraseñas. Nunca se guarda una clave en
 * texto plano.
 */
public interface ClaveHasher {

    String generarHash(String clave);

    boolean verificar(String clave, String hash);
}
