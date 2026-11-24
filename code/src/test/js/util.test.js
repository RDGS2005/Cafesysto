import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import * as util from "@static/util.js";
import { jsonResponse, textResponse, loginAs, USER_KEY } from "./helpers.js";

let fetchMock;
let replace;

beforeEach(() => {
    sessionStorage.clear();
    fetchMock = vi.fn();
    replace = vi.fn();
    vi.stubGlobal("fetch", fetchMock);
    vi.stubGlobal("location", { replace });
});

afterEach(() => {
    vi.unstubAllGlobals();
});

describe("sesión: storeUser / getUser", () => {
    it("guarda y recupera el usuario", () => {
        const user = { rol: "CLIENTE", token: "abc" };
        util.storeUser(user);
        expect(sessionStorage.getItem(USER_KEY)).toBe(JSON.stringify(user));
        expect(util.getUser()).toEqual(user);
    });

    it("devuelve null si no hay sesión", () => {
        expect(util.getUser()).toBeNull();
    });
});

describe("redirectUser", () => {
    it("redirige a /index.html si no hay sesión", () => {
        util.redirectUser();
        expect(replace).toHaveBeenCalledWith("/index.html");
    });

    it("no redirige si hay sesión", () => {
        loginAs();
        util.redirectUser();
        expect(replace).not.toHaveBeenCalled();
    });
});

describe("logout", () => {
    it("limpia la sesión y redirige al login", () => {
        loginAs();
        util.logout();
        expect(util.getUser()).toBeNull();
        expect(replace).toHaveBeenCalledWith("/index.html");
    });
});

describe("findResponseErrors", () => {
    it("lanza el campo mensaje cuando el cuerpo es JSON", async () => {
        await expect(util.findResponseErrors(textResponse(JSON.stringify({ mensaje: "Cédula inválida" }))))
            .rejects.toThrow("Cédula inválida");
    });

    it("agrega los detalles de validación con el nombre legible del campo", async () => {
        const body = JSON.stringify({
            mensaje: "Datos de la solicitud no válidos",
            detalles: ["clave: debe tener al menos 8 caracteres", "cedula: debe tener exactamente 10 dígitos"]
        });
        await expect(util.findResponseErrors(textResponse(body))).rejects.toThrow(
            "Datos de la solicitud no válidos: Contraseña: debe tener al menos 8 caracteres; Cédula: debe tener exactamente 10 dígitos");
    });

    it("lanza solo el mensaje si los detalles están vacíos", async () => {
        const body = JSON.stringify({ mensaje: "Cédula inválida", detalles: [] });
        await expect(util.findResponseErrors(textResponse(body))).rejects.toThrow("Cédula inválida");
    });

    it("lanza el texto completo si el JSON no tiene mensaje", async () => {
        const body = JSON.stringify({ otro: 1 });
        await expect(util.findResponseErrors(textResponse(body))).rejects.toThrow(body);
    });

    it("lanza el texto plano si no es JSON", async () => {
        await expect(util.findResponseErrors(textResponse("Servidor caído", 500)))
            .rejects.toThrow("Servidor caído");
    });

    it("usa un mensaje por defecto si el cuerpo está vacío", async () => {
        await expect(util.findResponseErrors(textResponse("", 500)))
            .rejects.toThrow("No se pudo completar el registro");
    });
});

describe("authFetch", () => {
    it("falla si no hay sesión y no llama a fetch", async () => {
        await expect(util.authFetch("/api/x")).rejects.toThrow("No está autorizado para hacer esto");
        expect(fetchMock).not.toHaveBeenCalled();
    });

    it("envía el token y Accept; sin body no pone Content-Type", async () => {
        loginAs({ rol: "PERSONAL", token: "tok-1" });
        fetchMock.mockResolvedValue(jsonResponse({ ok: true }));

        const response = await util.authFetch("/api/x");

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe("/api/x");
        expect(options.headers.Authorization).toBe("Bearer tok-1");
        expect(options.headers.Accept).toBe("application/json");
        expect(options.headers["Content-Type"]).toBeUndefined();
        expect(await response.json()).toEqual({ ok: true });
    });

    it("agrega Content-Type cuando hay body y conserva method y body", async () => {
        loginAs({ rol: "PERSONAL", token: "tok-1" });
        fetchMock.mockResolvedValue(jsonResponse({}));

        await util.authFetch("/api/x", { method: "POST", body: "{}" });

        const options = fetchMock.mock.calls[0][1];
        expect(options.method).toBe("POST");
        expect(options.body).toBe("{}");
        expect(options.headers["Content-Type"]).toBe("application/json");
    });

    it("permite sobrescribir cabeceras pero no Authorization", async () => {
        loginAs({ rol: "PERSONAL", token: "tok-1" });
        fetchMock.mockResolvedValue(jsonResponse({}));

        await util.authFetch("/api/x", { headers: { Accept: "text/plain", Authorization: "Bearer falso" } });

        const headers = fetchMock.mock.calls[0][1].headers;
        expect(headers.Accept).toBe("text/plain");
        expect(headers.Authorization).toBe("Bearer tok-1");
    });

    it("ante un 401 cierra la sesión y avisa que expiró", async () => {
        loginAs();
        fetchMock.mockResolvedValue(jsonResponse({}, 401));

        await expect(util.authFetch("/api/x")).rejects.toThrow("Tu sesión expiró. Inicia sesión de nuevo.");
        expect(util.getUser()).toBeNull();
        expect(replace).toHaveBeenCalledWith("/index.html");
    });

    it("lanza el mensaje del servidor si la respuesta no es correcta", async () => {
        loginAs();
        fetchMock.mockResolvedValue(textResponse(JSON.stringify({ mensaje: "Producto no disponible" }), 400));

        await expect(util.authFetch("/api/x")).rejects.toThrow("Producto no disponible");
        expect(util.getUser()).not.toBeNull();
    });
});

describe("showView", () => {
    let panels;
    let buttons;

    beforeEach(() => {
        document.body.innerHTML = `
            <button data-view="a"></button>
            <button data-view="b"></button>
            <section data-view-panel="a"><h1 tabindex="-1">A</h1></section>
            <section data-view-panel="b" hidden><h1 tabindex="-1">B</h1></section>`;
        panels = document.querySelectorAll("[data-view-panel]");
        buttons = document.querySelectorAll("[data-view]");
    });

    it("muestra solo el panel pedido y marca el botón activo", () => {
        util.showView(panels, buttons, "b");

        expect(panels[0].hidden).toBe(true);
        expect(panels[1].hidden).toBe(false);
        expect(buttons[0].classList.contains("active")).toBe(false);
        expect(buttons[1].classList.contains("active")).toBe(true);
        expect(buttons[1].getAttribute("aria-current")).toBe("page");
        expect(buttons[0].hasAttribute("aria-current")).toBe(false);
    });

    it("si la vista no existe no marca ningún botón como activo ni mueve el foco", () => {
        util.showView(panels, buttons, "zzz", { moveFocus: true });

        // Nota: el código actual oculta todos los paneles antes de comprobar que la vista exista
        buttons.forEach(button => {
            expect(button.classList.contains("active")).toBe(false);
            expect(button.hasAttribute("aria-current")).toBe(false);
        });
        expect(document.activeElement).toBe(document.body);
    });

    it("mueve el foco al h1 cuando moveFocus es true", () => {
        util.showView(panels, buttons, "b", { moveFocus: true });
        expect(document.activeElement).toBe(panels[1].querySelector("h1"));
    });

    it("no mueve el foco por defecto", () => {
        util.showView(panels, buttons, "b");
        expect(document.activeElement).toBe(document.body);
    });
});

describe("isValidCedula", () => {
    it("acepta cédulas con dígito verificador correcto", () => {
        expect(util.isValidCedula("1710034065")).toBe(true);
        expect(util.isValidCedula("0926687856")).toBe(true);
    });

    it("rechaza un dígito verificador incorrecto", () => {
        expect(util.isValidCedula("1710034066")).toBe(false);
    });

    it("rechaza una provincia inválida", () => {
        expect(util.isValidCedula("2510034065")).toBe(false);
        expect(util.isValidCedula("0010034065")).toBe(false);
    });

    it("rechaza un tercer dígito mayor o igual a 6", () => {
        expect(util.isValidCedula("1760034065")).toBe(false);
    });

    it("rechaza longitudes incorrectas, letras y valores no texto", () => {
        expect(util.isValidCedula("171003406")).toBe(false);
        expect(util.isValidCedula("17100340655")).toBe(false);
        expect(util.isValidCedula("17100340a5")).toBe(false);
        expect(util.isValidCedula("")).toBe(false);
        expect(util.isValidCedula(1710034065)).toBe(false);
        expect(util.isValidCedula(null)).toBe(false);
    });

    it("rechaza 0000000000", () => {
        expect(util.isValidCedula("0000000000")).toBe(false);
    });
});

describe("isValidCelular", () => {
    it("acepta 10 dígitos que empiezan con 09", () => {
        expect(util.isValidCelular("0991234567")).toBe(true);
    });

    it("rechaza otro prefijo", () => {
        expect(util.isValidCelular("1234567890")).toBe(false);
        expect(util.isValidCelular("0891234567")).toBe(false);
    });

    it("rechaza longitudes incorrectas, letras y valores no texto", () => {
        expect(util.isValidCelular("099123456")).toBe(false);
        expect(util.isValidCelular("09912345678")).toBe(false);
        expect(util.isValidCelular("099123456a")).toBe(false);
        expect(util.isValidCelular("")).toBe(false);
        expect(util.isValidCelular(undefined)).toBe(false);
    });
});