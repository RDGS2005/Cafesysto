package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.usuarios.entidad.Administrador;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.entidad.Usuario;
import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

    private UsuarioRepository usuarioRepository;
    private ClaveHasher claveHasher;

    public Cliente ingresarCliente(String correo, String clave) {
        return null;
    }

    public Personal ingresarPersonal(String correo, String clave) {
        return null;
    }

    public Administrador ingresarAdministrador(String nombreUsuario, String clave) {
        return null;
    }

    public Usuario exigirRol(ContextoAutenticado contexto, RolUsuario rol) {
        return null;
    }

    public Cliente obtenerClienteAutenticado(ContextoAutenticado contexto) {
        return null;
    }

    public Administrador exigirAdministradorSinBloqueo(ContextoAutenticado contexto) {
        return null;
    }

    public void cambiarClaveAdministrador(ContextoAutenticado contexto, String actual, String nueva,
                                          String confirmacion) {
    }

    private void validarCambioClave(String actual, String nueva, String confirmacion,
                                    Administrador administrador) {
    }
}
