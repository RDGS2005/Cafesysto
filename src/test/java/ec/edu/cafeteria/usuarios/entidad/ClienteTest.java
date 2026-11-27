package ec.edu.cafeteria.usuarios.entidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteTest {

    @Test
    void obtenerCodigo_devuelveElCodigoDelCliente() {
        Cliente cliente = new Cliente();
        cliente.setCodigo("CLI-000123");

        assertEquals("CLI-000123", cliente.obtenerCodigo());
    }

    @Test
    void obtenerTokenAcceso_devuelveElCorreo() {
        Cliente cliente = new Cliente();
        cliente.setCorreo("ana@institucion.edu.ec");

        assertEquals("ana@institucion.edu.ec", cliente.obtenerTokenAcceso());
    }

    @Test
    void obtenerRol_devuelveCliente() {
        Cliente cliente = new Cliente();

        assertEquals(RolUsuario.CLIENTE, cliente.obtenerRol());
    }
}
