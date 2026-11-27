package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.comun.excepcion.ConflictoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.config.CafeteriaProperties;
import ec.edu.cafeteria.usuarios.dto.RegistroClienteRequest;
import ec.edu.cafeteria.usuarios.dto.RegistroPersonalRequest;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Persona;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class UsuarioService {

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final ClaveHasher claveHasher;
    private final AutenticacionService autenticacionService;
    private final CafeteriaProperties propiedades;

    public UsuarioService(UsuarioRepository usuarioRepository, ClaveHasher claveHasher,
            AutenticacionService autenticacionService, CafeteriaProperties propiedades) {
        this.usuarioRepository = usuarioRepository;
        this.claveHasher = claveHasher;
        this.autenticacionService = autenticacionService;
        this.propiedades = propiedades;
    }

    /**
     * RFU-01. El código de cliente lo genera el sistema; no viene del formulario.
     */
    @Transactional
    public Cliente registrarCliente(RegistroClienteRequest datos) {
        validarDatosCliente(datos);
        validarUnicidad(datos.cedula(), datos.correo());

        Cliente cliente = new Cliente();
        copiarDatosPersona(cliente, datos.nombre(), datos.apellido(), datos.cedula(), datos.celular(),
                datos.correo());
        cliente.setClaveHash(claveHasher.generarHash(datos.clave()));
        cliente.setCodigo(generarCodigoCliente());
        return (Cliente) usuarioRepository.save(cliente);
    }

    /**
     * RFU-05. Solo un administrador autenticado y sin cambio de contraseña
     * pendiente.
     */
    @Transactional
    public Personal registrarPersonal(
            ContextoAutenticado contexto,
            RegistroPersonalRequest datos) {

        autenticacionService.exigirAdministradorSinBloqueo(contexto);

        // RFU-05: no se exige dominio institucional.
        // @Valid y @Email validan el formato en el controlador.
        validarUnicidad(datos.cedula(), datos.correo());

        Personal personal = new Personal();

        copiarDatosPersona(
                personal,
                datos.nombre(),
                datos.apellido(),
                datos.cedula(),
                datos.celular(),
                datos.correo());

        personal.setClaveHash(
                claveHasher.generarHash(datos.clave()));

        return (Personal) usuarioRepository.save(personal);
    }

    // ---------- Validaciones ----------

    private void validarDatosCliente(RegistroClienteRequest datos) {
        validarCorreoInstitucional(datos.correo());
    }


    private void validarCorreoInstitucional(String correo) {
        String sufijo = "@" + propiedades.correoInstitucionalDominio();
        if (!correo.toLowerCase().endsWith(sufijo.toLowerCase())) {
            throw new NegocioException("El correo debe ser institucional (" + sufijo + ")");
        }
    }

    /**
     * La base de datos también garantiza la unicidad ante registros simultáneos.
     */
    private void validarUnicidad(String cedula, String correo) {
        if (usuarioRepository.buscarPorCedula(cedula).isPresent()) {
            throw new ConflictoException("Ya existe un usuario con esa cédula");
        }
        if (usuarioRepository.buscarPorCorreo(correo).isPresent()) {
            throw new ConflictoException("Ya existe un usuario con ese correo");
        }
    }

    private String generarCodigoCliente() {
        String codigo;
        do {
            codigo = "CLI-" + String.format("%06d", ALEATORIO.nextInt(1_000_000));
        } while (usuarioRepository.existeCodigoCliente(codigo));
        return codigo;
    }

    private void copiarDatosPersona(Persona persona, String nombre, String apellido, String cedula,
            String celular, String correo) {
        persona.setNombre(nombre.trim());
        persona.setApellido(apellido.trim());
        persona.setCedula(cedula);
        persona.setCelular(celular);
        persona.setCorreo(correo.trim());
    }
}
