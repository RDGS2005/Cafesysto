package ec.edu.cafeteria.usuarios.entidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonalTest {

    @Test
    void obtenerTokenAcceso_devuelveElCorreo() {
        Personal personal = new Personal();
        personal.setCorreo("empleado@gmail.com");

        assertEquals("empleado@gmail.com", personal.obtenerTokenAcceso());
    }

    @Test
    void obtenerRol_devuelvePersonal() {
        Personal personal = new Personal();

        assertEquals(RolUsuario.PERSONAL, personal.obtenerRol());
    }
}
