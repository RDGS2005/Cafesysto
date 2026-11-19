package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.dto.CambioClaveRequest;
import ec.edu.cafeteria.usuarios.dto.LoginRequest;
import ec.edu.cafeteria.usuarios.dto.LoginResponse;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    private AutenticacionService autenticacionService;

    @PostMapping("/cliente")
    public LoginResponse autenticarCliente(@RequestBody LoginRequest datos) {
        return null;
    }

    @PostMapping("/personal")
    public LoginResponse autenticarPersonal(@RequestBody LoginRequest datos) {
        return null;
    }

    @PostMapping("/administrador")
    public LoginResponse autenticarAdministrador(@RequestBody LoginRequest datos) {
        return null;
    }

    @PostMapping("/administrador/clave")
    public void cambiarClaveAdministrador(ContextoAutenticado contexto, @RequestBody CambioClaveRequest datos) {
    }
}
