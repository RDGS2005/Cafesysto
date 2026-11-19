package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.usuarios.dto.RegistroClienteRequest;
import ec.edu.cafeteria.usuarios.dto.RegistroPersonalRequest;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private ClaveHasher claveHasher;
    private AutenticacionService autenticacionService;

    public Cliente registrarCliente(RegistroClienteRequest datos) {
        return null;
    }

    public Personal registrarPersonal(ContextoAutenticado contexto, RegistroPersonalRequest datos) {
        return null;
    }

    private void validarDatosCliente(RegistroClienteRequest datos) {
    }

    private void validarDatosPersonal(RegistroPersonalRequest datos) {
    }

    private void validarUnicidad(String cedula, String correo) {
    }

    private String generarCodigoCliente() {
        return null;
    }
}
