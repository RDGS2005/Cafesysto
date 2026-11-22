export const baseURL = "http://localhost:8080"
export const baseAPI = `${baseURL}/api`;
export const storedUser = "cafesysto_user";

/**
 * Guardar información de sesión
 * @param user información de sesión con rol, token y si requiere cambio de clave obtenida de la API
 */
export function storeUser(user) {
    sessionStorage.setItem(storedUser, JSON.stringify(user));
}

/**
 * Recuperar información de sesión
 * @returns información de sesión con rol, token y si requiere cambio de clave obtenida de la API
 */
export function getUser() {
    return JSON.parse(sessionStorage.getItem(storedUser) || 'null');
}

/**
 * Redirige al usuario si no ha iniciado sesión
 */
export function redirectUser() {
    const user = getUser();

    if (!user) {
        window.location.replace(`${baseURL}/index.html`);
    }
}

/**
 * Cierra la sesión y redirige al login
 */
export function logout() {
    sessionStorage.clear();
    redirectUser();
}

/**
 * Maneja errores de respuestas HTTP para mostrarlos al usuario
 * @param {Response} response respuesta obtenida de un fetch a la API
 * @throws {Error} siempre
 */
export async function findResponseErrors(response) {
    const errorText = await response.text();
    let mensajeError;
    try {
        const errorJson = JSON.parse(errorText);
        mensajeError = errorJson.mensaje || errorText;
    } catch {
        mensajeError = errorText || "No se pudo completar el registro";
    }
    throw new Error(mensajeError);
}

/**
 * Envía una petición a la API con el token de la sesión del usuario
 * @param {string} url dirección del endpoint
 * @param {RequestInit} [options] opciones de fetch, por ejemplo method y body (JSON en texto)
 * @returns {Promise<Response>} respuesta correcta (2xx) de la API
 * @throws {Error} si no hay sesión, si la sesión expiró (401, y además cierra la sesión)
 * o con el mensaje del servidor si la respuesta no es correcta
 */
export async function authFetch(url, options = {}) {
    // Obtener permisos de usuario
    const user = getUser();
    if (!user) throw new Error("No está autorizado para hacer esto");

    const response = await fetch(url, {
        ...options,
        headers: {
            "Accept": "application/json",
            ...(options.body ? { "Content-Type": "application/json" } : {}),
            ...options.headers,
            "Authorization": `Bearer ${user.token}`
        }
    });

    // La sesión ya no es válida: se cierra y se vuelve al login
    if (response.status === 401) {
        logout();
        throw new Error("Tu sesión expiró. Inicia sesión de nuevo.");
    }
    if (!response.ok) { await findResponseErrors(response) }
    return response;
}

/**
 * Cambiar de vista/panel con los botones de navegación
 * @param {NodeListOf<Element>} panels vistas del documento
 * @param {NodeListOf<Element>} navButtons botones correspondientes a cada vista
 * @param {string} name vista correspondiente al boton
 */
export function showView(panels, navButtons, name, { moveFocus = false } = {}) {
    let shown = null;
    panels.forEach(panel => {
        const active = panel.dataset.viewPanel === name;
        panel.hidden = !active;
        if (active) shown = panel;
    });
    if (!shown) return;

    navButtons.forEach(button => {
        const active = button.dataset.view === name;
        button.classList.toggle("active", active);
        if (active) button.setAttribute("aria-current", "page");
        else button.removeAttribute("aria-current");
    });

    if (moveFocus) {
        const heading = shown.querySelector("h1");
        if (heading) heading.focus();
    }
}