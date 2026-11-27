package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.dto.ClienteResponse;
import ec.edu.cafeteria.usuarios.dto.PersonalResponse;
import ec.edu.cafeteria.usuarios.dto.RegistroClienteRequest;
import ec.edu.cafeteria.usuarios.dto.RegistroPersonalRequest;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import ec.edu.cafeteria.usuarios.servicio.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse registrarCliente(@Valid @RequestBody RegistroClienteRequest datos) {
        return ClienteResponse.desde(usuarioService.registrarCliente(datos));
    }

    @PostMapping("/personal")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonalResponse registrarPersonal(ContextoAutenticado contexto,
            @Valid @RequestBody RegistroPersonalRequest datos) {
        return PersonalResponse.desde(usuarioService.registrarPersonal(contexto, datos));
    }
}
