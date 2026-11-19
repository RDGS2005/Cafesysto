package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.servicio.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void registrarCliente_delegaEnElServicioYDevuelveElCliente() {
    }

    @Test
    void registrarPersonal_delegaEnElServicioConElContexto() {
    }
}
