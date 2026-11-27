package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.comun.excepcion.AccesoDenegadoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.comun.excepcion.NoAutenticadoException;
import ec.edu.cafeteria.usuarios.entidad.Administrador;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ClaveHasher claveHasher;
    @Mock private SesionService sesionService;
    @InjectMocks private AutenticacionService autenticacionService;

    private Cliente cliente() {
        Cliente cliente = new Cliente();
        cliente.setCorreo("ana@institucion.edu.ec");
        cliente.setClaveHash("hash-cliente");
        return cliente;
    }

    private Personal personal() {
        Personal personal = new Personal();
        personal.setCorreo("empleado@gmail.com");
        personal.setClaveHash("hash-personal");
        return personal;
    }

    private Administrador administrador(boolean requiereCambio) {
        Administrador administrador = new Administrador();
        administrador.setNombreUsuario("admin");
        administrador.setClaveHash("hash-administrador");
        administrador.setRequiereCambioClave(requiereCambio);
        return administrador;
    }

    @Test
    void ingresarCliente_devuelveElClienteConCredencialesCorrectas() {
        Cliente cliente = cliente();
        when(usuarioRepository.buscarPorCorreo(cliente.getCorreo())).thenReturn(Optional.of(cliente));
        when(claveHasher.verificar("Clave1234", "hash-cliente")).thenReturn(true);

        Cliente obtenido = autenticacionService.ingresarCliente(cliente.getCorreo(), "Clave1234");

        assertSame(cliente, obtenido);
        verify(claveHasher).verificar("Clave1234", "hash-cliente");
    }

    @Test
    void ingresarCliente_rechazaClaveIncorrecta() {
        Cliente cliente = cliente();
        when(usuarioRepository.buscarPorCorreo(cliente.getCorreo())).thenReturn(Optional.of(cliente));
        // Mockito devuelve false para verificar(..) si la contraseña no coincide.
        NoAutenticadoException error = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarCliente(cliente.getCorreo(), "incorrecta"));

        assertEquals("Credenciales inválidas", error.getMessage());
        verify(claveHasher).verificar("incorrecta", "hash-cliente");
    }

    @Test
    void ingresarCliente_rechazaUsuarioInexistente() {
        when(usuarioRepository.buscarPorCorreo("nadie@institucion.edu.ec")).thenReturn(Optional.empty());

        NoAutenticadoException error = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarCliente("nadie@institucion.edu.ec", "Clave1234"));

        assertEquals("Credenciales inválidas", error.getMessage());
        verifyNoInteractions(claveHasher);
    }

    @Test
    void ingresarCliente_rechazaSiElUsuarioNoEsCliente() {
        Personal personal = personal();
        when(usuarioRepository.buscarPorCorreo(personal.getCorreo())).thenReturn(Optional.of(personal));

        NoAutenticadoException error = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarCliente(personal.getCorreo(), "Clave1234"));

        assertEquals("Credenciales inválidas", error.getMessage());
        verifyNoInteractions(claveHasher);
    }

    @Test
    void ingresarPersonal_devuelveElPersonalConCredencialesCorrectas() {
        Personal personal = personal();
        when(usuarioRepository.buscarPorCorreo(personal.getCorreo())).thenReturn(Optional.of(personal));
        when(claveHasher.verificar("Clave1234", "hash-personal")).thenReturn(true);

        Personal obtenido = autenticacionService.ingresarPersonal(personal.getCorreo(), "Clave1234");

        assertSame(personal, obtenido);
        verify(usuarioRepository).buscarPorCorreo("empleado@gmail.com");
    }

    @Test
    void ingresarPersonal_rechazaCredencialesInvalidas() {
        Personal personal = personal();
        when(usuarioRepository.buscarPorCorreo(personal.getCorreo())).thenReturn(Optional.of(personal));

        NoAutenticadoException error = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarPersonal(personal.getCorreo(), "clave-incorrecta"));

        assertEquals("Credenciales inválidas", error.getMessage());
        verify(claveHasher).verificar("clave-incorrecta", "hash-personal");
    }

    @Test
    void ingresarAdministrador_devuelveElAdministradorConCredencialesCorrectas() {
        Administrador administrador = administrador(true);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));
        when(claveHasher.verificar("ClaveInicial123", "hash-administrador")).thenReturn(true);

        Administrador obtenido = autenticacionService.ingresarAdministrador("admin", "ClaveInicial123");

        assertSame(administrador, obtenido);
        assertTrue(obtenido.debeCambiarClave(), "El login no debe eliminar el cambio pendiente");
    }

    @Test
    void ingresarAdministrador_rechazaCredencialesInvalidas() {
        when(usuarioRepository.buscarAdministrador("desconocido")).thenReturn(Optional.empty());

        NoAutenticadoException error = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarAdministrador("desconocido", "ClaveInicial123"));

        assertEquals("Credenciales inválidas", error.getMessage());
        verifyNoInteractions(claveHasher);
    }

    @Test
    void ingresar_usaElMismoMensajeGenericoParaCualquierFallo() {
        Cliente cliente = cliente();
        Personal personal = personal();
        when(usuarioRepository.buscarPorCorreo("inexistente@institucion.edu.ec"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorCorreo(cliente.getCorreo())).thenReturn(Optional.of(cliente));
        when(usuarioRepository.buscarPorCorreo(personal.getCorreo())).thenReturn(Optional.of(personal));

        String noExiste = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarCliente("inexistente@institucion.edu.ec", "clave"))
                .getMessage();
        String claveIncorrecta = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarCliente(cliente.getCorreo(), "clave"))
                .getMessage();
        String rolIncorrecto = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.ingresarCliente(personal.getCorreo(), "clave"))
                .getMessage();

        assertEquals("Credenciales inválidas", noExiste);
        assertEquals(noExiste, claveIncorrecta);
        assertEquals(noExiste, rolIncorrecto);
    }

    @Test
    void exigirRol_devuelveElUsuarioSiElRolCoincide() {
        Cliente cliente = cliente();
        ContextoAutenticado contexto = new ContextoAutenticado(cliente.getCorreo(), RolUsuario.CLIENTE);
        when(usuarioRepository.buscarPorCorreo(cliente.getCorreo())).thenReturn(Optional.of(cliente));

        assertSame(cliente, autenticacionService.exigirRol(contexto, RolUsuario.CLIENTE));
        verify(usuarioRepository).buscarPorCorreo(cliente.getCorreo());
    }

    @Test
    void exigirRol_rechazaSinContexto() {
        NoAutenticadoException error = assertThrows(NoAutenticadoException.class,
                () -> autenticacionService.exigirRol(null, RolUsuario.CLIENTE));

        assertEquals("Debe iniciar sesión", error.getMessage());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void exigirRol_rechazaRolIncorrecto() {
        ContextoAutenticado contexto = new ContextoAutenticado("empleado@gmail.com", RolUsuario.PERSONAL);

        AccesoDenegadoException error = assertThrows(AccesoDenegadoException.class,
                () -> autenticacionService.exigirRol(contexto, RolUsuario.CLIENTE));

        assertEquals("No tiene permiso para esta operación", error.getMessage());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void exigirRol_rechazaAdministradorConCambioDeClavePendiente() {
        Administrador administrador = administrador(true);
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));

        AccesoDenegadoException error = assertThrows(AccesoDenegadoException.class,
                () -> autenticacionService.exigirRol(contexto, RolUsuario.ADMINISTRADOR));

        assertTrue(error.getMessage().contains("cambiar"));
    }

    @Test
    void obtenerClienteAutenticado_devuelveElClienteDelContexto() {
        Cliente cliente = cliente();
        ContextoAutenticado contexto = new ContextoAutenticado(cliente.getCorreo(), RolUsuario.CLIENTE);
        when(usuarioRepository.buscarPorCorreo(cliente.getCorreo())).thenReturn(Optional.of(cliente));

        assertSame(cliente, autenticacionService.obtenerClienteAutenticado(contexto));
    }

    @Test
    void obtenerClienteAutenticado_rechazaSiElContextoNoEsDeCliente() {
        ContextoAutenticado contexto = new ContextoAutenticado("empleado@gmail.com", RolUsuario.PERSONAL);

        assertThrows(AccesoDenegadoException.class,
                () -> autenticacionService.obtenerClienteAutenticado(contexto));
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void exigirAdministradorSinBloqueo_devuelveElAdministradorSinCambioPendiente() {
        Administrador administrador = administrador(false);
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));

        assertSame(administrador, autenticacionService.exigirAdministradorSinBloqueo(contexto));
    }

    @Test
    void cambiarClaveAdministrador_actualizaElHashYLaMarca() {
        Administrador administrador = administrador(true);
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));
        when(claveHasher.verificar("Anterior123", "hash-administrador")).thenReturn(true);
        when(claveHasher.generarHash("NuevaClave123")).thenReturn("hash-nuevo");

        autenticacionService.cambiarClaveAdministrador(
                contexto, "Anterior123", "NuevaClave123", "NuevaClave123");

        assertEquals("hash-nuevo", administrador.obtenerClaveHash());
        assertFalse(administrador.debeCambiarClave());
        verify(usuarioRepository).save(same(administrador));
    }

    @Test
    void cambiarClaveAdministrador_rechazaClaveActualIncorrecta() {
        Administrador administrador = administrador(true);
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));

        NegocioException error = assertThrows(NegocioException.class,
                () -> autenticacionService.cambiarClaveAdministrador(
                        contexto, "Incorrecta", "NuevaClave123", "NuevaClave123"));

        assertTrue(error.getMessage().contains("actual"));
        assertEquals("hash-administrador", administrador.obtenerClaveHash());
        verify(usuarioRepository, never()).save(any());
        verify(claveHasher, never()).generarHash(anyString());
    }

    @Test
    void cambiarClaveAdministrador_rechazaConfirmacionDistinta() {
        Administrador administrador = administrador(true);
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));
        when(claveHasher.verificar("Anterior123", "hash-administrador")).thenReturn(true);

        NegocioException error = assertThrows(NegocioException.class,
                () -> autenticacionService.cambiarClaveAdministrador(
                        contexto, "Anterior123", "NuevaClave123", "NoCoincide123"));

        assertTrue(error.getMessage().contains("coinciden"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cambiarClaveAdministrador_rechazaNuevaClaveIgualALaActual() {
        Administrador administrador = administrador(true);
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));
        when(claveHasher.verificar("Anterior123", "hash-administrador")).thenReturn(true);

        NegocioException error = assertThrows(NegocioException.class,
                () -> autenticacionService.cambiarClaveAdministrador(
                        contexto, "Anterior123", "Anterior123", "Anterior123"));

        assertTrue(error.getMessage().contains("distinta"));
        verify(claveHasher, never()).generarHash(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cambiarClaveAdministrador_conservaElEstadoAnteriorSiHayError() {
        Administrador administrador = administrador(true);
        ContextoAutenticado contexto = new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.buscarAdministrador("admin")).thenReturn(Optional.of(administrador));
        when(claveHasher.verificar("Anterior123", "hash-administrador")).thenReturn(true);

        assertThrows(NegocioException.class,
                () -> autenticacionService.cambiarClaveAdministrador(
                        contexto, "Anterior123", "NuevaClave123", "distinta"));

        assertEquals("hash-administrador", administrador.obtenerClaveHash());
        assertTrue(administrador.debeCambiarClave());
        verify(claveHasher, never()).generarHash(anyString());
        verify(usuarioRepository, never()).save(any());
    }
}
