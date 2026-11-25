import { describe, it, expect, afterEach, vi } from "vitest";
import { setupEnv, jsonResponse, textResponse, flush, loginAs, submit, USER_KEY } from "./helpers.js";

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
    await import("@static/administrador/administrador.js");
    return env;
}

afterEach(() => vi.unstubAllGlobals());

describe("registro de personal", () => {
    function fill() {
        $("staff-first-name").value = "Luis";
        $("staff-last-name").value = "Mora";
        $("staff-id").value = "1710034065";
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
            cedula: "1710034065",
            celular: "0987654321",
            correo: "luis@cafe.com",
            clave: "Clave456"
        });
        expect($("staff-status").textContent).toBe("Registro exitoso de empleado");
        expect($("staff-first-name").value).toBe("");
    });

    it("rechaza una cédula inválida sin llamar a la API", async () => {
        const { fetchMock } = await load();
        fill();
        $("staff-id").value = "1710034066";

        submit($("staff-form"));
        await flush();

        expect(fetchMock).not.toHaveBeenCalled();
        expect($("staff-status").textContent).toBe("La cédula no es válida");
        expect($("staff-first-name").value).toBe("Luis");
    });

    it("rechaza un celular inválido sin llamar a la API", async () => {
        const { fetchMock } = await load();
        fill();
        $("staff-phone").value = "1234567890";

        submit($("staff-form"));
        await flush();

        expect(fetchMock).not.toHaveBeenCalled();
        expect($("staff-status").textContent).toBe("El número de celular no es válido");
    });

    it("no llama a la API si el formulario no cumple la validación del navegador", async () => {
        const { fetchMock } = await load();
        fill();
        $("staff-email").required = true;
        $("staff-email").value = "";
        const reportValidity = vi.spyOn($("staff-form"), "reportValidity");

        submit($("staff-form"));
        await flush();

        expect(reportValidity).toHaveBeenCalled();
        expect(fetchMock).not.toHaveBeenCalled();
        expect($("staff-status").textContent).toBe("");
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
    
        vi.stubGlobal("location", {
            reload: vi.fn()
        });
    
        fetchMock.mockResolvedValue(jsonResponse({}));
        fill();
    
        submit($("password-form"));
        await flush();
    
        const [url, options] = fetchMock.mock.calls[0];
    
        expect(url).toBe("/api/auth/administrador/clave");
        expect(options.method).toBe("POST");
        expect(JSON.parse(options.body)).toEqual({
            actual: "vieja",
            nueva: "nueva",
            confirmacion: "nueva"
        });
    
        expect($("password-status").textContent).toBe("Cambio de contraseña exitoso");
        expect($("new-password").value).toBe("");
        expect(location.reload).toHaveBeenCalled();
    });

    it("quita la marca de cambio obligatorio de la sesión guardada", async () => {
        const { fetchMock } = await load();
        loginAs({ rol: "ADMINISTRADOR", token: "admin-token", requiereCambioClave: true });
        fetchMock.mockResolvedValue(jsonResponse({}));
        fill();

        submit($("password-form"));
        await flush();

        expect(JSON.parse(sessionStorage.getItem(USER_KEY))).toEqual({
            rol: "ADMINISTRADOR",
            token: "admin-token",
            requiereCambioClave: false
        });
    });

    it("conserva la marca de cambio obligatorio si el servidor rechaza el cambio", async () => {
        const { fetchMock } = await load();
        loginAs({ rol: "ADMINISTRADOR", token: "admin-token", requiereCambioClave: true });
        fetchMock.mockResolvedValue(textResponse(JSON.stringify({ mensaje: "Clave actual incorrecta" }), 400));
        fill();

        submit($("password-form"));
        await flush();

        expect(JSON.parse(sessionStorage.getItem(USER_KEY)).requiereCambioClave).toBe(true);
    });

    it("no llama a la API si el formulario no cumple la validación del navegador", async () => {
        const { fetchMock } = await load();
        fill();
        $("new-password").required = true;
        $("new-password").value = "";

        submit($("password-form"));
        await flush();

        expect(fetchMock).not.toHaveBeenCalled();
        expect($("password-status").textContent).toBe("");
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