package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.dto.CambioClaveRequest;
import ec.edu.cafeteria.usuarios.dto.LoginRequest;
import ec.edu.cafeteria.usuarios.dto.LoginResponse;
import ec.edu.cafeteria.usuarios.entidad.Administrador;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Un endpoint por rol: el controlador define el rol esperado y el servicio lo
 * comprueba.
 */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    public AutenticacionController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/cliente")
    public LoginResponse autenticarCliente(@Valid @RequestBody LoginRequest datos) {
        Cliente cliente = autenticacionService.ingresarCliente(datos.identificador(), datos.clave());
        return new LoginResponse(autenticacionService.abrirSesion(cliente), cliente.obtenerRol(), false);
    }

    @PostMapping("/personal")
    public LoginResponse autenticarPersonal(@Valid @RequestBody LoginRequest datos) {
        Personal personal = autenticacionService.ingresarPersonal(datos.identificador(), datos.clave());
        return new LoginResponse(autenticacionService.abrirSesion(personal), personal.obtenerRol(), false);
    }

    @PostMapping("/administrador")
    public LoginResponse autenticarAdministrador(@Valid @RequestBody LoginRequest datos) {
        Administrador admin = autenticacionService.ingresarAdministrador(datos.identificador(), datos.clave());
        return new LoginResponse(autenticacionService.abrirSesion(admin), admin.obtenerRol(),
                admin.debeCambiarClave());
    }

    @PostMapping("/administrador/clave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarClaveAdministrador(ContextoAutenticado contexto,
            @Valid @RequestBody CambioClaveRequest datos) {
        autenticacionService.cambiarClaveAdministrador(contexto, datos.actual(), datos.nueva(),
                datos.confirmacion());
    }
}
