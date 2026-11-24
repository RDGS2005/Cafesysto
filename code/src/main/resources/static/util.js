export const baseAPI = "/api";
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
        window.location.replace("/index.html");
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
 * Valida una cédula ecuatoriana: 10 dígitos, provincia válida (01-24 o 30),
 * tercer dígito menor a 6 y dígito verificador correcto (módulo 10)
 * @param {string} cedula cédula a validar
 * @returns {boolean} true si la cédula es válida
 */
export function isValidCedula(cedula) {
    if (typeof cedula !== "string" || !/^\d{10}$/.test(cedula)) return false;

    const province = Number(cedula.slice(0, 2));
    if (!((province >= 1 && province <= 24) || province === 30)) return false;
    if (Number(cedula[2]) >= 6) return false;

    // Coeficientes 2,1,2,1,... sobre los primeros 9 dígitos; si el producto supera 9 se resta 9
    let sum = 0;
    for (let i = 0; i < 9; i++) {
        let product = Number(cedula[i]) * (i % 2 === 0 ? 2 : 1);
        if (product > 9) product -= 9;
        sum += product;
    }
    const checkDigit = (10 - (sum % 10)) % 10;
    return checkDigit === Number(cedula[9]);
}

/**
 * Valida un número de celular ecuatoriano: 10 dígitos que empiezan con 09
 * @param {string} celular número a validar
 * @returns {boolean} true si el número es válido
 */
export function isValidCelular(celular) {
    return typeof celular === "string" && /^09\d{8}$/.test(celular);
}

const FIELD_LABELS = {
    nombre: "Nombre",
    apellido: "Apellido",
    cedula: "Cédula",
    celular: "Celular",
    correo: "Correo",
    clave: "Contraseña",
    actual: "Contraseña actual",
    nueva: "Nueva contraseña",
    confirmacion: "Confirmación"
};

/**
 * Convierte un detalle del servidor con formato "campo: mensaje" a un texto legible
 * @param {string} detalle detalle de validación, por ejemplo "clave: debe tener al menos 8 caracteres"
 * @returns {string} detalle con el nombre del campo en lenguaje natural
 */
function describeFieldError(detalle) {
    const separator = detalle.indexOf(": ");
    if (separator === -1) return detalle;
    const field = detalle.slice(0, separator);
    return `${FIELD_LABELS[field] ?? field}: ${detalle.slice(separator + 2)}`;
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
        // Agregar el detalle de cada campo inválido (por ejemplo "clave: debe tener al menos 8 caracteres")
        if (errorJson.mensaje && Array.isArray(errorJson.detalles) && errorJson.detalles.length > 0) {
            const detalles = errorJson.detalles.map(describeFieldError).join("; ");
            mensajeError = `${errorJson.mensaje}: ${detalles}`;
        }
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