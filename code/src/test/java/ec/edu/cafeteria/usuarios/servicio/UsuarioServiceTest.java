package ec.edu.cafeteria.usuarios.servicio;

import ec.edu.cafeteria.usuarios.repositorio.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClaveHasher claveHasher;

    @Mock
    private AutenticacionService autenticacionService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void registrarCliente_guardaElClienteConClaveHasheadaYCodigoGenerado() {
    }

    @Test
    void registrarCliente_rechazaCamposObligatoriosVacios() {
    }

    @Test
    void registrarCliente_rechazaCedulaOCelularQueNoTenganDiezDigitos() {
    }

    @Test
    void registrarCliente_rechazaCorreoNoInstitucional() {
    }

    @Test
    void registrarCliente_rechazaCedulaDuplicada() {
    }

    @Test
    void registrarCliente_rechazaCorreoDuplicado() {
    }

    @Test
    void registrarCliente_generaUnCodigoDeClienteUnico() {
    }

    @Test
    void registrarPersonal_guardaElPersonalSiElAdministradorEstaAutenticado() {
    }

    @Test
    void registrarPersonal_rechazaSiNoHayAdministradorAutenticado() {
    }

    @Test
    void registrarPersonal_rechazaSiElAdministradorTieneCambioDeClavePendiente() {
    }

    @Test
    void registrarPersonal_rechazaDatosInvalidosODuplicados() {
    }
}
