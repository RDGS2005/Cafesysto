package ec.edu.cafeteria.usuarios.controlador;

import ec.edu.cafeteria.usuarios.servicio.AutenticacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutenticacionControllerTest {

    @Mock
    private AutenticacionService autenticacionService;

    @InjectMocks
    private AutenticacionController autenticacionController;

    @Test
    void autenticarCliente_devuelveElTokenYElRol() {
    }

    @Test
    void autenticarPersonal_devuelveElTokenYElRol() {
    }

    @Test
    void autenticarAdministrador_indicaSiRequiereCambioDeClave() {
    }

    @Test
    void cambiarClaveAdministrador_delegaEnElServicio() {
    }
}
