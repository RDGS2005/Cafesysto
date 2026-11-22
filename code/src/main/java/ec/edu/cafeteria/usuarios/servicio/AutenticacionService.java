package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.comun.excepcion.AccesoDenegadoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.comun.excepcion.NoAutenticadoException;
import ec.edu.cafeteria.usuarios.entidad.Administrador;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.entidad.Usuario;
import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AutenticacionService {

    private static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    private final UsuarioRepository usuarioRepository;
    private final ClaveHasher claveHasher;
    private final SesionService sesionService;

    public AutenticacionService(UsuarioRepository usuarioRepository, ClaveHasher claveHasher,
            SesionService sesionService) {
        this.usuarioRepository = usuarioRepository;
        this.claveHasher = claveHasher;
        this.sesionService = sesionService;
    }

    // ---------- Ingreso (RFU-02, RFU-04, RFU-06) ----------
    // Usuario inexistente, clave incorrecta o rol incorrecto producen el mismo
    // mensaje genérico.

    @Transactional(readOnly = true)
    public Cliente ingresarCliente(String correo, String clave) {
        Usuario u = usuarioRepository.buscarPorCorreo(correo).orElse(null);
        return verificar(u, clave, Cliente.class);
    }

    @Transactional(readOnly = true)
    public Personal ingresarPersonal(String correo, String clave) {
        Usuario u = usuarioRepository.buscarPorCorreo(correo).orElse(null);
        return verificar(u, clave, Personal.class);
    }

    @Transactional(readOnly = true)
    public Administrador ingresarAdministrador(String nombreUsuario, String clave) {
        Usuario u = usuarioRepository.buscarAdministrador(nombreUsuario).orElse(null);
        return verificar(u, clave, Administrador.class);
    }

    /**
     * Abre una sesión para un usuario ya autenticado y devuelve el token de acceso.
     */
    public String abrirSesion(Usuario usuario) {
        return sesionService.abrir(new ContextoAutenticado(usuario.obtenerTokenAcceso(), usuario.obtenerRol()));
    }

    // ---------- Control de acceso ----------

    /**
     * Comprueba que el contexto pertenezca a un usuario persistido con el rol
     * esperado.
     * Rechaza al administrador con cambio de contraseña pendiente.
     */
    @Transactional(readOnly = true)
    public Usuario exigirRol(ContextoAutenticado contexto, RolUsuario rol) {
        if (contexto == null) {
            throw new NoAutenticadoException("Debe iniciar sesión");
        }
        if (contexto.rol() != rol) {
            throw new AccesoDenegadoException("No tiene permiso para esta operación");
        }
        Usuario usuario = buscarUsuario(contexto);
        if (usuario instanceof Administrador admin && admin.debeCambiarClave()) {
            throw new AccesoDenegadoException("Debe cambiar la contraseña antes de continuar");
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public Cliente obtenerClienteAutenticado(ContextoAutenticado contexto) {
        return (Cliente) exigirRol(contexto, RolUsuario.CLIENTE);
    }

    @Transactional(readOnly = true)
    public Administrador exigirAdministradorSinBloqueo(ContextoAutenticado contexto) {
        return (Administrador) exigirRol(contexto, RolUsuario.ADMINISTRADOR);
    }

    // ---------- Cambio de contraseña del administrador (RFU-06) ----------

    /**
     * Es la excepción al bloqueo por cambio pendiente. La actualización es atómica.
     */
    @Transactional
    public void cambiarClaveAdministrador(ContextoAutenticado contexto, String actual, String nueva,
            String confirmacion) {
        if (contexto == null) {
            throw new NoAutenticadoException("Debe iniciar sesión");
        }
        if (contexto.rol() != RolUsuario.ADMINISTRADOR) {
            throw new AccesoDenegadoException("No tiene permiso para esta operación");
        }
        Administrador admin = (Administrador) buscarUsuario(contexto);
        validarCambioClave(actual, nueva, confirmacion, admin);
        admin.actualizarClave(claveHasher.generarHash(nueva));
        usuarioRepository.save(admin);
    }

    private void validarCambioClave(String actual, String nueva, String confirmacion, Administrador admin) {
        if (!claveHasher.verificar(actual, admin.obtenerClaveHash())) {
            throw new NegocioException("La contraseña actual no es correcta");
        }
        if (!nueva.equals(confirmacion)) {
            throw new NegocioException("La nueva contraseña y su confirmación no coinciden");
        }
        if (nueva.equals(actual)) {
            throw new NegocioException("La nueva contraseña debe ser distinta de la actual");
        }
    }

    // ---------- Internos ----------

    private Usuario buscarUsuario(ContextoAutenticado contexto) {
        Optional<? extends Usuario> encontrado = contexto.rol() == RolUsuario.ADMINISTRADOR
                ? usuarioRepository.buscarAdministrador(contexto.identificador())
                : usuarioRepository.buscarPorCorreo(contexto.identificador());
        Usuario usuario = encontrado.orElseThrow(() -> new NoAutenticadoException("Sesión no válida"));
        if (usuario.obtenerRol() != contexto.rol()) {
            throw new NoAutenticadoException("Sesión no válida");
        }
        return usuario;
    }

    private <T extends Usuario> T verificar(Usuario usuario, String clave, Class<T> tipo) {
        if (!tipo.isInstance(usuario) || !claveHasher.verificar(clave, usuario.obtenerClaveHash())) {
            throw new NoAutenticadoException(CREDENCIALES_INVALIDAS);
        }
        return tipo.cast(usuario);
    }
}
