package ec.edu.cafeteria.usuarios.entidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorTest {

    @Test
    void debeCambiarClave_devuelveTrueCuandoHayCambioPendiente() {
        Administrador administrador = new Administrador();
        administrador.setRequiereCambioClave(true);

        assertTrue(administrador.debeCambiarClave());
    }

    @Test
    void debeCambiarClave_devuelveFalseCuandoYaCambioLaClave() {
        Administrador administrador = new Administrador();
        administrador.setRequiereCambioClave(false);

        assertFalse(administrador.debeCambiarClave());
        administrador.setRequiereCambioClave(null);
        assertFalse(administrador.debeCambiarClave(), "Un indicador nulo no debe activar el bloqueo");
    }

    @Test
    void actualizarClave_guardaElNuevoHashYRetiraLaMarcaDeCambioPendiente() {
        Administrador administrador = new Administrador();
        administrador.setClaveHash("hash-antiguo");
        administrador.setRequiereCambioClave(true);

        administrador.actualizarClave("hash-nuevo");

        assertEquals("hash-nuevo", administrador.obtenerClaveHash());
        assertFalse(administrador.debeCambiarClave());
    }

    @Test
    void obtenerTokenAcceso_devuelveElNombreDeUsuario() {
        Administrador administrador = new Administrador();
        administrador.setNombreUsuario("admin");

        assertEquals("admin", administrador.obtenerTokenAcceso());
    }

    @Test
    void obtenerRol_devuelveAdministrador() {
        assertEquals(RolUsuario.ADMINISTRADOR, new Administrador().obtenerRol());
    }
}
