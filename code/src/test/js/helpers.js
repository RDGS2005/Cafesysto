import { vi } from "vitest";

export const USER_KEY = "cafesysto_user";

/** Respuesta falsa de fetch con cuerpo JSON */
export function jsonResponse(data, status = 200) {
    return {
        ok: status >= 200 && status < 300,
        status,
        json: async () => data,
        text: async () => JSON.stringify(data)
    };
}

/** Respuesta falsa de fetch con cuerpo de texto plano */
export function textResponse(text, status = 400) {
    return {
        ok: status >= 200 && status < 300,
        status,
        json: async () => JSON.parse(text),
        text: async () => text
    };
}

/** Deja correr las promesas pendientes (usa el setTimeout real) */
export async function flush(times = 4) {
    for (let i = 0; i < times; i++) {
        await new Promise(resolve => setTimeout(resolve, 0));
    }
}

/** Guarda una sesión en sessionStorage como lo hace storeUser */
export function loginAs(user = { rol: "CLIENTE", token: "tok-123" }) {
    sessionStorage.setItem(USER_KEY, JSON.stringify(user));
    return user;
}

/**
 * Prepara el entorno: DOM, sesión limpia, fetch y location simulados.
 * @returns {{fetchMock: import("vitest").Mock, replace: import("vitest").Mock}}
 */
export function setupEnv(html = "") {
    document.body.innerHTML = html;
    sessionStorage.clear();
    const fetchMock = vi.fn();
    const replace = vi.fn();
    vi.stubGlobal("fetch", fetchMock);
    vi.stubGlobal("location", { replace });
    vi.resetModules();
    return { fetchMock, replace };
}

/** Dispara el evento submit de un formulario */
export function submit(form) {
    form.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));
}
