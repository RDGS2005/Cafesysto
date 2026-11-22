package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.dto.CambioClaveRequest;
import ec.edu.cafeteria.usuarios.dto.LoginRequest;
import ec.edu.cafeteria.usuarios.dto.LoginResponse;
import ec.edu.cafeteria.usuarios.entidad.Administrador;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacionControllerTest {

    @Mock private AutenticacionService autenticacionService;
    @InjectMocks private AutenticacionController autenticacionController;

    @Test
    void autenticarCliente_devuelveElTokenYElRol() {
        LoginRequest solicitud = new LoginRequest("ana@institucion.edu.ec", "Clave1234");
        Cliente cliente = new Cliente();
        when(autenticacionService.ingresarCliente(solicitud.identificador(), solicitud.clave()))
                .thenReturn(cliente);
        when(autenticacionService.abrirSesion(cliente)).thenReturn("token-cliente");

        LoginResponse respuesta = autenticacionController.autenticarCliente(solicitud);

        assertEquals("token-cliente", respuesta.token());
        assertEquals(RolUsuario.CLIENTE, respuesta.rol());
        assertFalse(respuesta.requiereCambioClave());
        verify(autenticacionService).ingresarCliente(solicitud.identificador(), solicitud.clave());
        verify(autenticacionService).abrirSesion(cliente);
    }

    @Test
    void autenticarPersonal_devuelveElTokenYElRol() {
        LoginRequest solicitud = new LoginRequest("empleado@gmail.com", "Clave1234");
        Personal personal = new Personal();
        when(autenticacionService.ingresarPersonal(solicitud.identificador(), solicitud.clave()))
                .thenReturn(personal);
        when(autenticacionService.abrirSesion(personal)).thenReturn("token-personal");

        LoginResponse respuesta = autenticacionController.autenticarPersonal(solicitud);

        assertEquals("token-personal", respuesta.token());
        assertEquals(RolUsuario.PERSONAL, respuesta.rol());
        assertFalse(respuesta.requiereCambioClave());
        verify(autenticacionService).ingresarPersonal(solicitud.identificador(), solicitud.clave());
        verify(autenticacionService).abrirSesion(personal);
    }

    @Test
    void autenticarAdministrador_indicaSiRequiereCambioDeClave() {
        LoginRequest solicitud = new LoginRequest("admin", "ClaveInicial123");
        Administrador administrador = new Administrador();
        administrador.setRequiereCambioClave(true);
        when(autenticacionService.ingresarAdministrador(solicitud.identificador(), solicitud.clave()))
                .thenReturn(administrador);
        when(autenticacionService.abrirSesion(administrador)).thenReturn("token-admin");

        LoginResponse respuesta = autenticacionController.autenticarAdministrador(solicitud);

        assertEquals("token-admin", respuesta.token());
        assertEquals(RolUsuario.ADMINISTRADOR, respuesta.rol());
        assertTrue(respuesta.requiereCambioClave());
        verify(autenticacionService).ingresarAdministrador(solicitud.identificador(), solicitud.clave());
        verify(autenticacionService).abrirSesion(administrador);
    }

    @Test
    void cambiarClaveAdministrador_delegaEnElServicio() {
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        CambioClaveRequest solicitud = new CambioClaveRequest("Anterior123", "NuevaClave123", "NuevaClave123");

        assertDoesNotThrow(() -> autenticacionController.cambiarClaveAdministrador(contexto, solicitud));

        verify(autenticacionService).cambiarClaveAdministrador(
                contexto, solicitud.actual(), solicitud.nueva(), solicitud.confirmacion());
    }
}
