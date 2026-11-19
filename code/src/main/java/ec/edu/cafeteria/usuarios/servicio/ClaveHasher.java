package ec.edu.cafeteria.usuarios.servicio;

public interface ClaveHasher {

    String generarHash(String clave);

    boolean verificar(String clave, String hash);
}
