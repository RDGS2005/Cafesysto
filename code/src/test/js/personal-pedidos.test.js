import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { setupEnv, jsonResponse, textResponse, flush, loginAs, submit } from "./helpers.js";

const HTML = `
<button class="header_button" data-view="orders" type="button">Pedidos</button>
<form id="deliver-form">
  <input id="deliver-code"><button type="submit">Entregar</button>
</form>
<p id="orders-status"></p>
<div id="delivery-info" hidden>
  <span id="delivery-code"></span><ul id="delivery-items"></ul>
  <span id="delivery-total"></span><span id="delivery-time"></span>
</div>
<div class="kanban-column" data-status="pendiente"><span id="count-pendiente"></span><ul id="list-pendiente"></ul></div>
<div class="kanban-column" data-status="preparacion"><span id="count-preparacion"></span><ul id="list-preparacion"></ul></div>
<div class="kanban-column" data-status="listo"><span id="count-listo"></span><ul id="list-listo"></ul></div>`;

const $ = id => document.getElementById(id);
const card = code => document.querySelector(`.kanban-card[data-codigo="${code}"]`);
const count = key => $(`count-${key}`).textContent;

function makeOrder(codigoRetiro, estado, extra = {}) {
    return {
        codigoRetiro,
        estado,
        total: 5,
        detalles: [{ cantidad: 2, nombreProducto: "Café", precioUnitario: 2.5, subtotal: 5 }],
        ...extra
    };
}

let env;
let serverOrders;

/**
 * Simula la API: GET /pedidos devuelve serverOrders; POST /avanzar y /entrega
 * se resuelven con los manejadores que reciba cada prueba.
 */
async function load(orders, handlers = {}) {
    env = setupEnv(HTML);
    loginAs({ rol: "PERSONAL", token: "tok" });
    serverOrders = orders;
    env.fetchMock.mockImplementation(async (url, options = {}) => {
        if (options.method === "POST" && url.endsWith("/avanzar") && handlers.avanzar) return handlers.avanzar(url, options);
        if (options.method === "POST" && url.endsWith("/entrega") && handlers.entrega) return handlers.entrega(url, options);
        return jsonResponse(serverOrders);
    });
    vi.useFakeTimers({ toFake: ["setInterval", "clearInterval"] });
    await import("@static/personal/personal-pedidos.js");
    // Abrir la vista de pedidos carga el tablero
    document.querySelector(".header_button").click();
    await flush();
    return env;
}

beforeEach(() => {
    vi.spyOn(console, "error").mockImplementation(() => {});
});

afterEach(() => {
    vi.useRealTimers();
    vi.unstubAllGlobals();
});

describe("tablero", () => {
    it("reparte los pedidos en su columna, cuenta y oculta los entregados", async () => {
        await load([
            makeOrder("A1", "PENDIENTE"),
            makeOrder("A2", "PENDIENTE"),
            makeOrder("B1", "EN_PREPARACION"),
            makeOrder("C1", "LISTO_PARA_RETIRAR"),
            makeOrder("D1", "ENTREGADO")
        ]);

        expect(count("pendiente")).toBe("2");
        expect(count("preparacion")).toBe("1");
        expect(count("listo")).toBe("1");
        expect(card("D1")).toBeNull();
        expect($("list-pendiente").contains(card("A1"))).toBe(true);
        expect($("list-preparacion").contains(card("B1"))).toBe(true);
        expect($("list-listo").contains(card("C1"))).toBe(true);
    });

    it("pide los pedidos con el token del personal", async () => {
        const { fetchMock } = await load([]);
        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe("/api/pedidos");
        expect(options.headers.Authorization).toBe("Bearer tok");
    });

    it("muestra 'Sin pedidos' en columnas vacías", async () => {
        await load([makeOrder("A1", "PENDIENTE")]);
        expect($("list-listo").querySelector(".kanban-empty").textContent).toBe("Sin pedidos");
        expect(count("listo")).toBe("0");
    });

    it("la tarjeta muestra código, resumen de productos y total", async () => {
        await load([makeOrder("A1", "PENDIENTE")]);
        const el = card("A1");
        expect(el.querySelector("strong").textContent).toBe("A1");
        expect(el.querySelector(".kanban-items").textContent).toBe("2 × Café");
        expect(el.querySelector(".kanban-total").textContent).toMatch(/5[.,]00/);
    });

    it("solo los pedidos que pueden avanzar tienen botón y son arrastrables", async () => {
        await load([makeOrder("A1", "PENDIENTE"), makeOrder("C1", "LISTO_PARA_RETIRAR")]);

        expect(card("A1").draggable).toBe(true);
        expect(card("A1").querySelector("button").textContent).toBe("Pasar a En preparación");
        expect(card("C1").draggable).toBe(false);
        expect(card("C1").querySelector("button")).toBeNull();
    });

    it("informa si no se pueden cargar los pedidos", async () => {
        const { fetchMock } = await load([]);
        fetchMock.mockImplementation(async () => textResponse(JSON.stringify({ mensaje: "Servidor caído" }), 500));

        document.querySelector(".header_button").click();
        await flush();

        expect($("orders-status").textContent).toBe("Servidor caído");
    });

    it("no vuelve a dibujar el tablero si los datos no cambiaron", async () => {
        await load([makeOrder("A1", "PENDIENTE")]);
        const before = card("A1");

        document.querySelector(".header_button").click();
        await flush();

        expect(card("A1")).toBe(before);
    });
});

describe("avanzar pedido", () => {
    it("el botón pasa el pedido al siguiente estado con lo que devuelve la API", async () => {
        const { fetchMock } = await load([makeOrder("A1", "PENDIENTE")], {
            avanzar: async () => jsonResponse(makeOrder("A1", "EN_PREPARACION"))
        });

        card("A1").querySelector("button").click();
        await flush();

        const call = fetchMock.mock.calls.find(([url]) => url.endsWith("/avanzar"));
        expect(call[0]).toBe("/api/pedidos/A1/avanzar");
        expect(call[1].method).toBe("POST");
        expect($("list-preparacion").contains(card("A1"))).toBe(true);
        expect($("list-pendiente").contains(card("A1"))).toBe(false);
        expect($("orders-status").textContent).toBe('El pedido A1 pasó a "En preparación".');
        // El foco pasa al botón nuevo de la tarjeta movida
        expect(document.activeElement).toBe(card("A1").querySelector("button"));
    });

    it("evita enviar dos avances a la vez del mismo pedido", async () => {
        let resolve;
        const { fetchMock } = await load([makeOrder("A1", "PENDIENTE")], {
            avanzar: () => new Promise(r => { resolve = r; })
        });

        const button = card("A1").querySelector("button");
        button.click();
        button.click();
        await flush();

        expect(fetchMock.mock.calls.filter(([url]) => url.endsWith("/avanzar"))).toHaveLength(1);
        resolve(jsonResponse(makeOrder("A1", "EN_PREPARACION")));
        await flush();
    });

    it("muestra el error y recarga el tablero si la API rechaza el avance", async () => {
        await load([makeOrder("A1", "PENDIENTE")], {
            avanzar: async () => textResponse(JSON.stringify({ mensaje: "El pedido ya fue movido" }), 409)
        });
        // Otro empleado ya lo había movido
        serverOrders = [makeOrder("A1", "EN_PREPARACION")];

        card("A1").querySelector("button").click();
        await flush();

        expect($("orders-status").textContent).toBe("El pedido ya fue movido");
        expect($("list-preparacion").contains(card("A1"))).toBe(true);
    });

    it("soltar la tarjeta en la columna siguiente avanza el pedido", async () => {
        const { fetchMock } = await load([makeOrder("A1", "PENDIENTE")], {
            avanzar: async () => jsonResponse(makeOrder("A1", "EN_PREPARACION"))
        });
        const dataTransfer = { setData: vi.fn(), effectAllowed: "", dropEffect: "" };
        const dispatch = (el, type) => {
            const event = new Event(type, { bubbles: true, cancelable: true });
            event.dataTransfer = dataTransfer;
            el.dispatchEvent(event);
            return event;
        };
        const target = document.querySelector('.kanban-column[data-status="preparacion"]');

        dispatch(card("A1"), "dragstart");
        const over = dispatch(target, "dragover");
        expect(over.defaultPrevented).toBe(true);
        expect(target.classList.contains("drop-ok")).toBe(true);
        dispatch(target, "drop");
        await flush();

        expect(fetchMock.mock.calls.some(([url]) => url.endsWith("/avanzar"))).toBe(true);
        expect($("list-preparacion").contains(card("A1"))).toBe(true);
    });

    it("no permite soltar en una columna que no es la siguiente", async () => {
        await load([makeOrder("A1", "PENDIENTE")]);
        const dataTransfer = { setData: vi.fn(), effectAllowed: "", dropEffect: "" };
        const dispatch = (el, type) => {
            const event = new Event(type, { bubbles: true, cancelable: true });
            event.dataTransfer = dataTransfer;
            el.dispatchEvent(event);
            return event;
        };
        const listo = document.querySelector('.kanban-column[data-status="listo"]');

        dispatch(card("A1"), "dragstart");
        const over = dispatch(listo, "dragover");

        expect(over.defaultPrevented).toBe(false);
        expect(listo.classList.contains("drop-ok")).toBe(false);

        dispatch(card("A1"), "dragend");
        expect($("orders-status").textContent).toBe('El pedido A1 solo puede pasar a "En preparación".');
    });
});

describe("entrega de pedidos", () => {
    it("pide el código si el campo está vacío", async () => {
        const { fetchMock } = await load([]);
        const callsBefore = fetchMock.mock.calls.length;

        submit($("deliver-form"));
        await flush();

        expect($("orders-status").textContent).toBe("Ingresa el código del pedido.");
        expect(fetchMock.mock.calls).toHaveLength(callsBefore);
    });

    it("avisa sin llamar a la API si el pedido del tablero aún no está listo", async () => {
        const { fetchMock } = await load([makeOrder("A1", "PENDIENTE")]);
        const callsBefore = fetchMock.mock.calls.length;
        $("deliver-code").value = "a1";

        submit($("deliver-form"));
        await flush();

        expect($("orders-status").textContent).toContain('está en "Pendiente"');
        expect($("orders-status").textContent).toContain('Solo se pueden entregar pedidos que estén en "Listo para retirar"');
        expect(fetchMock.mock.calls).toHaveLength(callsBefore);
    });

    it("entrega un pedido listo: lo retira del tablero y muestra el resumen", async () => {
        const delivered = makeOrder("C1", "ENTREGADO");
        const { fetchMock } = await load([makeOrder("C1", "LISTO_PARA_RETIRAR")], {
            entrega: async () => jsonResponse(delivered)
        });
        $("deliver-code").value = "  c1 ";

        submit($("deliver-form"));
        await flush();

        const call = fetchMock.mock.calls.find(([url]) => url.endsWith("/entrega"));
        expect(call[0]).toBe("/api/pedidos/C1/entrega");
        expect(JSON.parse(call[1].body)).toEqual({ codigoPresentado: "C1" });
        expect(card("C1")).toBeNull();
        expect(count("listo")).toBe("0");
        expect($("delivery-info").hidden).toBe(false);
        expect($("delivery-code").textContent).toBe("C1");
        expect($("delivery-items").children).toHaveLength(1);
        expect($("delivery-items").textContent).toContain("2 × Café");
        expect($("delivery-total").textContent).toMatch(/5[.,]00/);
        expect($("deliver-code").value).toBe("");
        expect($("orders-status").textContent).toBe("El pedido C1 fue entregado y se retiró del tablero.");
        expect(document.querySelector('#deliver-form button[type="submit"]').disabled).toBe(false);
    });

    it("muestra el error de la API si el código es inválido y reactiva el botón", async () => {
        await load([], {
            entrega: async () => textResponse(JSON.stringify({ mensaje: "Código no encontrado" }), 404)
        });
        $("deliver-code").value = "ZZ99";

        submit($("deliver-form"));
        await flush();

        expect($("orders-status").textContent).toBe("Código no encontrado");
        expect($("delivery-info").hidden).toBe(true);
        expect(document.querySelector('#deliver-form button[type="submit"]').disabled).toBe(false);
    });
});

describe("actualización automática", () => {
    it("refresca los pedidos cada segundo", async () => {
        const { fetchMock } = await load([makeOrder("A1", "PENDIENTE")]);
        const before = fetchMock.mock.calls.length;

        serverOrders = [makeOrder("A1", "PENDIENTE"), makeOrder("A2", "PENDIENTE")];
        await vi.advanceTimersByTimeAsync(1000);
        await flush();

        expect(fetchMock.mock.calls.length).toBeGreaterThan(before);
        expect(card("A2")).not.toBeNull();
        expect(count("pendiente")).toBe("2");
    });

    it("no consulta mientras la pestaña está oculta", async () => {
        const { fetchMock } = await load([]);
        const before = fetchMock.mock.calls.length;
        vi.spyOn(document, "hidden", "get").mockReturnValue(true);

        await vi.advanceTimersByTimeAsync(3000);
        await flush();

        expect(fetchMock.mock.calls).toHaveLength(before);
    });

    it("conserva el mensaje actual al refrescar", async () => {
        await load([makeOrder("A1", "PENDIENTE")]);
        $("orders-status").textContent = "Mensaje importante";

        await vi.advanceTimersByTimeAsync(1000);
        await flush();

        expect($("orders-status").textContent).toBe("Mensaje importante");
    });
});
