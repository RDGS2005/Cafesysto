import { describe, it, expect, afterEach, vi } from "vitest";
import { setupEnv, jsonResponse, textResponse, flush, loginAs, submit, USER_KEY } from "./helpers.js";

const HTML = `
<section id="view-login">
  <h1 tabindex="-1">Ingreso</h1>
  <form id="login-form">
    <input id="login-email"><input id="login-password">
    <select id="login-role">
      <option value="cliente">Cliente</option>
      <option value="personal">Personal</option>
      <option value="administrador">Administrador</option>
    </select>
    <p id="login-status"></p>
  </form>
  <button id="show-register" type="button">Registrarse</button>
</section>
<section id="view-register" hidden>
  <h1 tabindex="-1">Registro</h1>
  <form id="register-form">
    <input id="student-first-name"><input id="student-last-name"><input id="student-id">
    <input id="student-phone"><input id="student-email"><input id="student-password">
    <p id="register-status"></p>
  </form>
  <button id="show-login" type="button">Ya tengo cuenta</button>
</section>`;

const $ = id => document.getElementById(id);

async function load(prepare) {
    const env = setupEnv(HTML);
    if (prepare) prepare(env);
    await import("@static/ingreso.js");
    return env;
}

afterEach(() => vi.unstubAllGlobals());

describe("redirección inicial", () => {
    it.each([
        ["CLIENTE", "./cliente/cliente.html"],
        ["PERSONAL", "./personal/personal.html"],
        ["ADMINISTRADOR", "./administrador/administrador.html"]
    ])("redirige al rol %s", async (rol, destino) => {
        const { replace } = await load(() => loginAs({ rol, token: "t" }));
        expect(replace).toHaveBeenCalledWith(destino);
    });

    it("no redirige si no hay sesión", async () => {
        const { replace } = await load();
        expect(replace).not.toHaveBeenCalled();
    });

    it("no redirige con un rol desconocido", async () => {
        const { replace } = await load(() => loginAs({ rol: "OTRO", token: "t" }));
        expect(replace).not.toHaveBeenCalled();
    });
});

describe("cambio entre login y registro", () => {
    it("muestra el registro y enfoca su título", async () => {
        await load();
        $("login-status").textContent = "error previo";

        $("show-register").click();

        expect($("view-login").hidden).toBe(true);
        expect($("view-register").hidden).toBe(false);
        expect($("login-status").textContent).toBe("");
        expect(document.activeElement).toBe($("view-register").querySelector("h1"));
    });

    it("vuelve al login", async () => {
        await load();
        $("show-register").click();
        $("show-login").click();

        expect($("view-login").hidden).toBe(false);
        expect($("view-register").hidden).toBe(true);
        expect(document.activeElement).toBe($("view-login").querySelector("h1"));
    });
});

describe("registro de cliente", () => {
    function fillRegister() {
        $("student-first-name").value = "Ana";
        $("student-last-name").value = "Pérez";
        $("student-id").value = "1712345678";
        $("student-phone").value = "0991234567";
        $("student-email").value = "ana@correo.com";
        $("student-password").value = "Clave123";
    }

    it("envía los datos a la API y confirma el registro", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValue(jsonResponse({}, 201));
        fillRegister();

        submit($("register-form"));
        await flush();

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe("/api/usuarios/clientes");
        expect(options.method).toBe("POST");
        expect(options.headers["Content-Type"]).toBe("application/json");
        expect(JSON.parse(options.body)).toEqual({
            nombre: "Ana",
            apellido: "Pérez",
            cedula: "1712345678",
            celular: "0991234567",
            correo: "ana@correo.com",
            clave: "Clave123"
        });
        expect($("register-status").textContent).toBe("Registro exitoso. Ya puede iniciar sesión");
        expect($("student-first-name").value).toBe("");
    });

    it("muestra el error del servidor y conserva el formulario", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValue(textResponse(JSON.stringify({ mensaje: "La cédula ya existe" }), 409));
        fillRegister();

        submit($("register-form"));
        await flush();

        expect($("register-status").textContent).toBe("La cédula ya existe");
        expect($("student-first-name").value).toBe("Ana");
    });

    it("muestra el error si falla la red", async () => {
        const { fetchMock } = await load();
        fetchMock.mockRejectedValue(new Error("Failed to fetch"));

        submit($("register-form"));
        await flush();

        expect($("register-status").textContent).toBe("Failed to fetch");
    });
});

describe("inicio de sesión", () => {
    it("llama al endpoint según el rol, guarda la sesión y redirige", async () => {
        const { fetchMock, replace } = await load();
        const user = { rol: "PERSONAL", token: "jwt" };
        fetchMock.mockResolvedValue(jsonResponse(user));
        $("login-email").value = "emp@correo.com";
        $("login-password").value = "secreta";
        $("login-role").value = "personal";

        submit($("login-form"));
        await flush();

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe("/api/auth/personal");
        expect(JSON.parse(options.body)).toEqual({ identificador: "emp@correo.com", clave: "secreta" });
        expect(JSON.parse(sessionStorage.getItem(USER_KEY))).toEqual(user);
        expect(replace).toHaveBeenCalledWith("./personal/personal.html");
    });

    it("muestra el mensaje de error con credenciales inválidas y no guarda sesión", async () => {
        const { fetchMock, replace } = await load();
        fetchMock.mockResolvedValue(textResponse(JSON.stringify({ mensaje: "Credenciales incorrectas" }), 401));

        submit($("login-form"));
        await flush();

        expect($("login-status").textContent).toBe("Credenciales incorrectas");
        expect(sessionStorage.getItem(USER_KEY)).toBeNull();
        expect(replace).not.toHaveBeenCalled();
    });
});
