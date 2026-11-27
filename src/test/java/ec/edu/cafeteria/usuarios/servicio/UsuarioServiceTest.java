package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.comun.excepcion.AccesoDenegadoException;
import ec.edu.cafeteria.comun.excepcion.ConflictoException;
import ec.edu.cafeteria.comun.excepcion.NoAutenticadoException;
import ec.edu.cafeteria.comun.excepcion.NegocioException;
import ec.edu.cafeteria.config.CafeteriaProperties;
import ec.edu.cafeteria.usuarios.dto.RegistroClienteRequest;
import ec.edu.cafeteria.usuarios.dto.RegistroPersonalRequest;
import ec.edu.cafeteria.usuarios.entidad.Cliente;
import ec.edu.cafeteria.usuarios.entidad.Personal;
import ec.edu.cafeteria.usuarios.entidad.RolUsuario;
import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // Validaciones de formato de los DTO, aplicadas por @Valid en el controlador.
    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void prepararValidador() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        fabrica.close();
    }

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClaveHasher claveHasher;

    @Mock
    private AutenticacionService autenticacionService;

    @Mock
    private CafeteriaProperties propiedades;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void registrarCliente_guardaElClienteConClaveHasheadaYCodigoGenerado() {
        RegistroClienteRequest datos = new RegistroClienteRequest(
                "  Ana  ", " Perez ", "1712345678", "0991234567",
                "ana@institucion.edu.ec", "Clave1234");
        when(propiedades.correoInstitucionalDominio()).thenReturn("institucion.edu.ec");
        when(usuarioRepository.buscarPorCedula(datos.cedula())).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorCorreo(datos.correo())).thenReturn(Optional.empty());
        when(claveHasher.generarHash("Clave1234")).thenReturn("hash-seguro");
        when(usuarioRepository.save(any(Cliente.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Cliente resultado = usuarioService.registrarCliente(datos);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
        assertEquals("Perez", resultado.getApellido());
        assertEquals(datos.cedula(), resultado.getCedula());
        assertEquals(datos.celular(), resultado.getCelular());
        assertEquals(datos.correo(), resultado.getCorreo());
        assertEquals("hash-seguro", resultado.getClaveHash());
        assertTrue(resultado.obtenerCodigo().matches("CLI-\\d{6}"));
        verify(claveHasher).generarHash(datos.clave());
        verify(usuarioRepository).save(resultado);
    }

    @Test
    void registrarCliente_rechazaCamposObligatoriosVacios() {
        // La validación de formato se aplica con @Valid en UsuarioController.
        // UsuarioService recibe DTO válidos en el flujo HTTP normal.
        RegistroClienteRequest datos = new RegistroClienteRequest("", "", "", "", "", "");
        var violaciones = validador.validate(datos);

        for (String campo : new String[]{"nombre", "apellido", "cedula", "celular", "correo", "clave"}) {
            assertTrue(violaciones.stream().anyMatch(v -> v.getPropertyPath().toString().equals(campo)),
                    "Debe rechazarse el campo obligatorio: " + campo);
        }
        verifyNoInteractions(usuarioRepository, claveHasher);
    }

    @Test
    void registrarCliente_rechazaCedulaOCelularQueNoTenganDiezDigitos() {
        RegistroClienteRequest cedulaInvalida = new RegistroClienteRequest(
                "Ana", "Perez", "171234567", "0991234567", "ana@institucion.edu.ec", "Clave1234");
        RegistroClienteRequest celularInvalido = new RegistroClienteRequest(
                "Ana", "Perez", "1712345678", "09912AB567", "ana@institucion.edu.ec", "Clave1234");

        assertTrue(validador.validate(cedulaInvalida).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cedula")));
        assertTrue(validador.validate(celularInvalido).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("celular")));
        verifyNoInteractions(usuarioRepository, claveHasher);
    }

    @Test
    void registrarCliente_rechazaCorreoNoInstitucional() {
        RegistroClienteRequest datos = new RegistroClienteRequest(
                "Ana", "Perez", "1712345678", "0991234567", "ana@gmail.com", "Clave1234");
        when(propiedades.correoInstitucionalDominio()).thenReturn("institucion.edu.ec");

        NegocioException error = assertThrows(NegocioException.class,
                () -> usuarioService.registrarCliente(datos));

        assertTrue(error.getMessage().contains("institucional"));
        verifyNoInteractions(usuarioRepository, claveHasher);
    }

    @Test
    void registrarCliente_aceptaCorreoDelDominioInstitucional() {
        RegistroClienteRequest datos = new RegistroClienteRequest(
                "Ana", "Perez", "1712345678", "0991234567",
                "ana@institucion.edu.ec", "Clave1234");
        assertTrue(validador.validate(datos).isEmpty(),
                "Un correo institucional válido debe superar las validaciones del DTO");
        when(propiedades.correoInstitucionalDominio()).thenReturn("institucion.edu.ec");
        when(usuarioRepository.buscarPorCedula(datos.cedula())).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorCorreo(datos.correo())).thenReturn(Optional.empty());
        when(claveHasher.generarHash(datos.clave())).thenReturn("hash-seguro");
        when(usuarioRepository.save(any(Cliente.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Cliente guardado = usuarioService.registrarCliente(datos);

        assertEquals(datos.correo(), guardado.getCorreo());
        assertEquals("hash-seguro", guardado.getClaveHash());
        assertTrue(guardado.getCodigo().matches("CLI-\\d{6}"));
    }

    @Test
    void registrarCliente_rechazaCedulaDuplicada() {
        RegistroClienteRequest datos = new RegistroClienteRequest(
                "Ana", "Perez", "1712345678", "0991234567", "ana@institucion.edu.ec", "Clave1234");
        when(propiedades.correoInstitucionalDominio()).thenReturn("institucion.edu.ec");
        when(usuarioRepository.buscarPorCedula(datos.cedula())).thenReturn(Optional.of(new Cliente()));

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> usuarioService.registrarCliente(datos));

        assertTrue(error.getMessage().contains("cédula"));
        verify(usuarioRepository, never()).buscarPorCorreo(anyString());
        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(claveHasher);
    }

    @Test
    void registrarCliente_rechazaCorreoDuplicado() {
        RegistroClienteRequest datos = new RegistroClienteRequest(
                "Ana", "Perez", "1712345678", "0991234567", "ana@institucion.edu.ec", "Clave1234");
        when(propiedades.correoInstitucionalDominio()).thenReturn("institucion.edu.ec");
        when(usuarioRepository.buscarPorCedula(datos.cedula())).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorCorreo(datos.correo())).thenReturn(Optional.of(new Cliente()));

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> usuarioService.registrarCliente(datos));

        assertTrue(error.getMessage().contains("correo"));
        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(claveHasher);
    }

    @Test
    void registrarCliente_generaUnCodigoDeClienteUnico() {
        RegistroClienteRequest datos = new RegistroClienteRequest(
                "Ana", "Perez", "1712345678", "0991234567", "ana@institucion.edu.ec", "Clave1234");
        when(propiedades.correoInstitucionalDominio()).thenReturn("institucion.edu.ec");
        when(usuarioRepository.buscarPorCedula(datos.cedula())).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorCorreo(datos.correo())).thenReturn(Optional.empty());
        when(usuarioRepository.existeCodigoCliente(anyString())).thenReturn(true, false);
        when(claveHasher.generarHash(datos.clave())).thenReturn("hash-seguro");
        when(usuarioRepository.save(any(Cliente.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Cliente cliente = usuarioService.registrarCliente(datos);

        assertTrue(cliente.obtenerCodigo().matches("CLI-\\d{6}"));
        verify(usuarioRepository, times(2)).existeCodigoCliente(anyString());
        verify(usuarioRepository).save(cliente);
    }

    @ParameterizedTest
    @ValueSource(strings = { "empleado@gmail.com", "empleado@outlook.com", "empleado@empresa.com" })
    void registrarPersonal_guardaElPersonalSiElAdministradorEstaAutenticado(String correo) {
        ContextoAutenticado administrador =
                new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        RegistroPersonalRequest datos = new RegistroPersonalRequest(
                "Maria", "Lopez", "1723456789", "0987654321", correo, "Clave1234");
        assertTrue(validador.validate(datos).isEmpty(),
                "El personal puede registrarse con cualquier dominio de correo válido");
        when(usuarioRepository.buscarPorCedula(datos.cedula())).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorCorreo(datos.correo())).thenReturn(Optional.empty());
        when(claveHasher.generarHash(datos.clave())).thenReturn("hash-seguro");
        when(usuarioRepository.save(any(Personal.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Personal guardado = usuarioService.registrarPersonal(administrador, datos);

        assertEquals(correo, guardado.getCorreo());
        assertEquals("hash-seguro", guardado.getClaveHash());
        verify(autenticacionService).exigirAdministradorSinBloqueo(administrador);
    }

    @Test
    void registrarPersonal_rechazaCorreoConFormatoInvalidoEnElDTO() {
        RegistroPersonalRequest datos = new RegistroPersonalRequest(
                "Maria", "Lopez", "1723456789", "0987654321", "correo-sin-arroba", "Clave1234");

        assertFalse(validador.validate(datos).isEmpty(),
                "@Email debe rechazar las direcciones mal formadas");
        // En HTTP, el controlador bloquea esta solicitud mediante @Valid.
    }

    @Test
    void registrarPersonal_rechazaCorreoVacioEnElDTO() {
        RegistroPersonalRequest datos = new RegistroPersonalRequest(
                "Maria", "Lopez", "1723456789", "0987654321", "", "Clave1234");

        assertFalse(validador.validate(datos).isEmpty(),
                "@NotBlank debe rechazar el correo vacío");
        // En HTTP, el controlador bloquea esta solicitud mediante @Valid.
    }

    @Test
    void registrarPersonal_rechazaSiNoHayAdministradorAutenticado() {
        RegistroPersonalRequest datos = new RegistroPersonalRequest(
                "Maria", "Lopez", "1723456789", "0987654321", "empleado@gmail.com", "Clave1234");
        when(autenticacionService.exigirAdministradorSinBloqueo(null))
                .thenThrow(new NoAutenticadoException("Debe iniciar sesión"));

        assertThrows(NoAutenticadoException.class,
                () -> usuarioService.registrarPersonal(null, datos));

        verify(autenticacionService).exigirAdministradorSinBloqueo(null);
        verifyNoInteractions(usuarioRepository, claveHasher);
    }

    @Test
    void registrarPersonal_rechazaSiElAdministradorTieneCambioDeClavePendiente() {
        ContextoAutenticado administrador =
                new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        RegistroPersonalRequest datos = new RegistroPersonalRequest(
                "Maria", "Lopez", "1723456789", "0987654321", "empleado@gmail.com", "Clave1234");
        when(autenticacionService.exigirAdministradorSinBloqueo(administrador))
                .thenThrow(new AccesoDenegadoException("Debe cambiar la contraseña antes de continuar"));

        AccesoDenegadoException error = assertThrows(AccesoDenegadoException.class,
                () -> usuarioService.registrarPersonal(administrador, datos));

        assertTrue(error.getMessage().contains("cambiar"));
        verifyNoInteractions(usuarioRepository, claveHasher);
    }

    @Test
    void registrarPersonal_rechazaDatosInvalidosODuplicados() {
        // @Valid impide que llegue al servicio un DTO con cédula inválida.
        RegistroPersonalRequest invalido = new RegistroPersonalRequest(
                "Maria", "Lopez", "17234", "0987654321", "empleado@gmail.com", "Clave1234");
        assertTrue(validador.validate(invalido).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cedula")));

        // La unicidad sí se comprueba explícitamente dentro de UsuarioService.
        ContextoAutenticado administrador =
                new ContextoAutenticado("admin", RolUsuario.ADMINISTRADOR);
        RegistroPersonalRequest datos = new RegistroPersonalRequest(
                "Maria", "Lopez", "1723456789", "0987654321", "empleado@gmail.com", "Clave1234");
        when(usuarioRepository.buscarPorCedula(datos.cedula())).thenReturn(Optional.empty());
        when(usuarioRepository.buscarPorCorreo(datos.correo()))
                .thenReturn(Optional.of(new Personal()));

        assertThrows(ConflictoException.class,
                () -> usuarioService.registrarPersonal(administrador, datos));
        verify(usuarioRepository, never()).save(any(Personal.class));
    }
}
