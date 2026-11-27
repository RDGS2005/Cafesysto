package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.dto.ClienteResponse;
import ec.edu.cafeteria.usuarios.dto.PersonalResponse;
import ec.edu.cafeteria.usuarios.dto.RegistroClienteRequest;
import ec.edu.cafeteria.usuarios.dto.RegistroPersonalRequest;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.servicio.ContextoAutenticado;
import ec.edu.cafeteria.usuarios.servicio.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock private UsuarioService usuarioService;
    @InjectMocks private UsuarioController usuarioController;

    @Test
    void registrarCliente_delegaEnElServicioYDevuelveElCliente() {
        RegistroClienteRequest solicitud = new RegistroClienteRequest(
                "Ana", "Perez", "1712345678", "0991234567", "ana@institucion.edu.ec", "Clave1234");
        Cliente cliente = new Cliente();
        cliente.setCodigo("CLI-000001");
        cliente.setNombre("Ana");
        cliente.setApellido("Perez");
        cliente.setCedula("1712345678");
        cliente.setCelular("0991234567");
        cliente.setCorreo("ana@institucion.edu.ec");
        when(usuarioService.registrarCliente(solicitud)).thenReturn(cliente);

        ClienteResponse respuesta = usuarioController.registrarCliente(solicitud);

        assertNotNull(respuesta);
        assertEquals("CLI-000001", respuesta.codigo());
        assertEquals(solicitud.nombre(), respuesta.nombre());
        assertEquals(solicitud.apellido(), respuesta.apellido());
        assertEquals(solicitud.cedula(), respuesta.cedula());
        assertEquals(solicitud.celular(), respuesta.celular());
        assertEquals(solicitud.correo(), respuesta.correo());
        verify(usuarioService).registrarCliente(solicitud);
    }

    @Test
    void registrarPersonal_delegaEnElServicioConElContexto() {
        ContextoAutenticado administrador = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        RegistroPersonalRequest solicitud = new RegistroPersonalRequest(
                "Maria", "Lopez", "1723456789", "0987654321", "empleada@gmail.com", "Clave1234");
        Personal personal = new Personal();
        personal.setNombre("Maria");
        personal.setApellido("Lopez");
        personal.setCedula("1723456789");
        personal.setCelular("0987654321");
        personal.setCorreo("empleada@gmail.com");
        when(usuarioService.registrarPersonal(administrador, solicitud)).thenReturn(personal);

        PersonalResponse respuesta = usuarioController.registrarPersonal(administrador, solicitud);

        assertNotNull(respuesta);
        assertEquals(solicitud.nombre(), respuesta.nombre());
        assertEquals(solicitud.apellido(), respuesta.apellido());
        assertEquals(solicitud.cedula(), respuesta.cedula());
        assertEquals(solicitud.celular(), respuesta.celular());
        assertEquals(solicitud.correo(), respuesta.correo());
        verify(usuarioService).registrarPersonal(administrador, solicitud);
    }
}
