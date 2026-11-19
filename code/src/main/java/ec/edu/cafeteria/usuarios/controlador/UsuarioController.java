package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.dto.ClienteResponse;
import ec.edu.cafeteria.usuarios.dto.PersonalResponse;
import ec.edu.cafeteria.usuarios.dto.RegistroClienteRequest;
import ec.edu.cafeteria.usuarios.dto.RegistroPersonalRequest;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import ec.edu.cafeteria.usuarios.servicio.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private UsuarioService usuarioService;

    @PostMapping("/clientes")
    public ClienteResponse registrarCliente(@RequestBody RegistroClienteRequest datos) {
        return null;
    }

    @PostMapping("/personal")
    public PersonalResponse registrarPersonal(ContextoAutenticado contexto,
                                              @RequestBody RegistroPersonalRequest datos) {
        return null;
    }
}
