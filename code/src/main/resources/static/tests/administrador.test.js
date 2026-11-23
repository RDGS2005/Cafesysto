import { describe, it, expect, afterEach, vi } from "vitest";
import { setupEnv, jsonResponse, textResponse, flush, loginAs, submit } from "./helpers.js";

const HTML = `
<form id="staff-form">
  <input id="staff-first-name"><input id="staff-last-name"><input id="staff-id">
  <input id="staff-phone"><input id="staff-email"><input id="staff-password">
  <p id="staff-status"></p>
</form>
<form id="password-form">
  <input id="current-password"><input id="new-password"><input id="confirm-password">
  <p id="password-status"></p>
</form>`;

const $ = id => document.getElementById(id);

async function load() {
    const env = setupEnv(HTML);
    loginAs({ rol: "ADMINISTRADOR", token: "admin-token" });
    await import("../administrador/administrador.js");
    return env;
}

afterEach(() => vi.unstubAllGlobals());

describe("registro de personal", () => {
    function fill() {
        $("staff-first-name").value = "Luis";
        $("staff-last-name").value = "Mora";
        $("staff-id").value = "1798765432";
        $("staff-phone").value = "0987654321";
        $("staff-email").value = "luis@cafe.com";
        $("staff-password").value = "Clave456";
    }

    it("envía los datos con el token del administrador", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValue(jsonResponse({}, 201));
        fill();

        submit($("staff-form"));
        await flush();

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe("/api/usuarios/personal");
        expect(options.method).toBe("POST");
        expect(options.headers.Authorization).toBe("Bearer admin-token");
        expect(JSON.parse(options.body)).toEqual({
            nombre: "Luis",
            apellido: "Mora",
            cedula: "1798765432",
            celular: "0987654321",
            correo: "luis@cafe.com",
            clave: "Clave456"
        });
        expect($("staff-status").textContent).toBe("Registro exitoso de empleado");
        expect($("staff-first-name").value).toBe("");
    });

    it("muestra el error del servidor", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValue(textResponse(JSON.stringify({ mensaje: "Correo duplicado" }), 409));
        fill();

        submit($("staff-form"));
        await flush();

        expect($("staff-status").textContent).toBe("Correo duplicado");
        expect($("staff-first-name").value).toBe("Luis");
    });
});

describe("cambio de contraseña", () => {
    function fill() {
        $("current-password").value = "vieja";
        $("new-password").value = "nueva";
        $("confirm-password").value = "nueva";
    }

    it("envía actual, nueva y confirmación", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValue(jsonResponse({}));
        fill();

        submit($("password-form"));
        await flush();

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe("/api/auth/administrador/clave");
        expect(options.method).toBe("POST");
        expect(JSON.parse(options.body)).toEqual({ actual: "vieja", nueva: "nueva", confirmacion: "nueva" });
        expect($("password-status").textContent).toBe("Cambio de contraseña exitoso");
        expect($("new-password").value).toBe("");
    });

    it("muestra el error si la contraseña actual es incorrecta", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValue(textResponse(JSON.stringify({ mensaje: "Clave actual incorrecta" }), 400));
        fill();

        submit($("password-form"));
        await flush();

        expect($("password-status").textContent).toBe("Clave actual incorrecta");
        expect($("current-password").value).toBe("vieja");
    });

    it("sin sesión muestra que no está autorizado y no llama a la API", async () => {
        const { fetchMock } = await load();
        sessionStorage.clear();

        submit($("password-form"));
        await flush();

        expect(fetchMock).not.toHaveBeenCalled();
        expect($("password-status").textContent).toBe("No está autorizado para hacer esto");
    });
});
