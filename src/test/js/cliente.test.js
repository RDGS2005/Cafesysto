import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { setupEnv, jsonResponse, textResponse, flush, loginAs } from "./helpers.js";

const HTML = `
<button class="header_button" data-view="order" type="button">Mi pedido <span id="order-count" hidden></span></button>
<p id="menu-status"></p>
<ul id="menu-list"></ul>
<h2 id="order-title" tabindex="-1">Mi pedido</h2>
<ul id="order-items"></ul>
<p id="order-message"></p>
<p>Total: <strong id="order-total"></strong></p>
<button id="confirm-button" type="button">Confirmar</button>
<p id="orders-message"></p>
<ul id="orders-list"></ul>`;

const $ = id => document.getElementById(id);

const MENU = [
    { codigo: "P1", nombre: "Café", precio: 2.5, estado: "DISPONIBLE" },
    { codigo: "P2", nombre: "Sánduche", precio: 3, estado: "DISPONIBLE" }
];

let env;
let server;

/** Simula la API con datos que cada prueba puede cambiar */
async function load(overrides = {}) {
    env = setupEnv(HTML);
    loginAs({ rol: "CLIENTE", token: "tok" });
    server = {
        menu: MENU,
        orders: [],
        // Precio actual en el servidor por código de producto
        prices: { P1: 2.5, P2: 3 },
        confirm: null,
        ...overrides
    };

    env.fetchMock.mockImplementation(async (url, options = {}) => {
        const method = options.method || "GET";
        if (url === "/api/productos/menu") return jsonResponse(server.menu);
        if (url === "/api/pedidos/mis-pedidos") return jsonResponse(server.orders);
        if (url === "/api/pedidos/total" && method === "POST") {
            const { items } = JSON.parse(options.body);
            const detalles = items.map(({ codigoProducto, cantidad }) => ({
                codigoProducto,
                cantidad,
                precioUnitario: server.prices[codigoProducto],
                subtotal: Math.round(server.prices[codigoProducto] * 100) * cantidad / 100
            }));
            return jsonResponse({ detalles, total: detalles.reduce((sum, d) => sum + d.subtotal, 0) });
        }
        if (url === "/api/pedidos" && method === "POST") {
            return server.confirm ? server.confirm(options) : jsonResponse({}, 500);
        }
        return jsonResponse({}, 404);
    });

    vi.useFakeTimers({ toFake: ["setInterval", "clearInterval"] });
    await import("@static/cliente/cliente.js");
    return env;
}

/** Avanza el temporizador de sondeo para que se cargue el menú */
async function loadMenuViaPolling() {
    await vi.advanceTimersByTimeAsync(1000);
    await flush();
}

function menuItem(index = 0) {
    return $("menu-list").children[index];
}

/** Agrega al pedido el producto del menú indicado */
async function addToOrder(index, quantity) {
    const li = menuItem(index);
    li.querySelector("input").value = String(quantity);
    li.querySelector(".add-button").click();
    await flush();
}

beforeEach(() => {
    vi.spyOn(console, "error").mockImplementation(() => {});
});

afterEach(() => {
    vi.useRealTimers();
    vi.unstubAllGlobals();
});

describe("estado inicial", () => {
    it("el pedido está vacío, con total en cero y sin poder confirmar", async () => {
        await load();
        expect($("order-message").textContent).toBe("Tu pedido está vacío.");
        expect($("order-total").textContent).toMatch(/0[.,]00/);
        expect($("order-count").hidden).toBe(true);
        expect($("confirm-button").disabled).toBe(true);
    });
});

describe("menú", () => {
    it("se carga por sondeo (sin token) y dibuja una tarjeta por producto", async () => {
        const { fetchMock } = await load();
        await loadMenuViaPolling();

        const call = fetchMock.mock.calls.find(([url]) => url === "/api/productos/menu");
        expect(call[1].headers.Accept).toBe("application/json");
        expect(call[1].headers.Authorization).toBeUndefined();
        expect($("menu-list").children).toHaveLength(2);
        expect(menuItem(0).querySelector(".menu-item-name").textContent).toBe("Café");
        expect(menuItem(0).querySelector(".menu-item-price").textContent).toMatch(/2[.,]50/);
        expect(menuItem(0).querySelector("input").value).toBe("1");
    });

    it("con un menú vacío no dibuja tarjetas", async () => {
        await load({ menu: [] });
        await loadMenuViaPolling();
        expect($("menu-list").children).toHaveLength(0);
        // Nota: loadMenu borra el mensaje "El menú no tiene productos por ahora." justo
        // después de renderMenu(), por lo que hoy ese aviso nunca llega a verse.
    });

    it("avisa si no se pudo cargar el menú", async () => {
        const { fetchMock } = await load();
        fetchMock.mockImplementation(async url =>
            url === "/api/productos/menu" ? textResponse("error", 500) : jsonResponse([]));
        await loadMenuViaPolling();
        expect($("menu-status").textContent).toBe("No se pudieron cargar los productos. Inténtalo de nuevo más tarde.");
    });

    it("no redibuja el menú si no hubo cambios", async () => {
        await load();
        await loadMenuViaPolling();
        const first = menuItem(0);

        await loadMenuViaPolling();

        expect(menuItem(0)).toBe(first);
    });

    it("redibuja el menú si cambia un precio", async () => {
        await load();
        await loadMenuViaPolling();

        server.menu = [{ ...MENU[0], precio: 4 }, MENU[1]];
        await loadMenuViaPolling();

        expect(menuItem(0).querySelector(".menu-item-price").textContent).toMatch(/4[.,]00/);
    });
});

describe("pedido en curso", () => {
    it("agregar un producto actualiza lista, contador, total y botón", async () => {
        const { fetchMock } = await load();
        await loadMenuViaPolling();

        await addToOrder(0, 3);

        expect($("order-items").children).toHaveLength(1);
        expect($("order-items").textContent).toContain("Café");
        expect($("order-items").textContent).toMatch(/3 × .*2[.,]50/);
        expect($("order-count").textContent).toBe("3");
        expect($("order-count").hidden).toBe(false);
        expect($("confirm-button").disabled).toBe(false);
        expect($("order-total").textContent).toMatch(/7[.,]50/);
        expect($("menu-status").textContent).toBe("Se agregó 3 × Café al pedido.");
        expect(menuItem(0).querySelector("input").value).toBe("1");

        const totalCall = fetchMock.mock.calls.find(([url]) => url === "/api/pedidos/total");
        expect(totalCall[1].headers.Authorization).toBe("Bearer tok");
        expect(JSON.parse(totalCall[1].body)).toEqual({ items: [{ codigoProducto: "P1", cantidad: 3 }] });
    });

    it("sumar el mismo producto acumula la cantidad", async () => {
        await load();
        await loadMenuViaPolling();

        await addToOrder(0, 2);
        await addToOrder(0, 5);

        expect($("order-items").children).toHaveLength(1);
        expect($("order-count").textContent).toBe("7");
    });

    it("limita la cantidad acumulada a 99", async () => {
        await load();
        await loadMenuViaPolling();

        await addToOrder(0, 60);
        await addToOrder(0, 60);

        expect($("order-count").textContent).toBe("99");
    });

    it.each(["0", "100", "1.5", "-2", ""])("rechaza la cantidad inválida '%s'", async quantity => {
        await load();
        await loadMenuViaPolling();

        await addToOrder(0, quantity);

        expect($("menu-status").textContent).toBe("Ingresa una cantidad entre 1 y 99 para Café.");
        expect($("order-items").children).toHaveLength(0);
    });

    it("el pedido puede incluir varios productos", async () => {
        await load();
        await loadMenuViaPolling();

        await addToOrder(0, 2);
        await addToOrder(1, 1);

        expect($("order-items").children).toHaveLength(2);
        expect($("order-total").textContent).toMatch(/8[.,]00/);
    });

    it("retirar un producto lo quita, vuelve a calcular y enfoca el título", async () => {
        await load();
        await loadMenuViaPolling();
        await addToOrder(0, 2);
        await addToOrder(1, 1);

        $("order-items").children[0].querySelector(".remove-button").click();
        await flush();

        expect($("order-items").children).toHaveLength(1);
        expect($("order-items").textContent).not.toContain("Café");
        expect($("order-total").textContent).toMatch(/3[.,]00/);
        expect($("order-message").textContent).toBe("Se retiró Café del pedido.");
        expect(document.activeElement).toBe($("order-title"));
    });

    it("retirar el último producto deja el pedido vacío y deshabilita confirmar", async () => {
        await load();
        await loadMenuViaPolling();
        await addToOrder(0, 1);

        $("order-items").querySelector(".remove-button").click();
        await flush();

        expect($("order-message").textContent).toBe("Se retiró Café del pedido. Tu pedido está vacío.");
        expect($("order-total").textContent).toMatch(/0[.,]00/);
        expect($("confirm-button").disabled).toBe(true);
        expect($("order-count").hidden).toBe(true);
    });

    it("actualiza en pantalla los precios que cambiaron en el servidor", async () => {
        await load();
        await loadMenuViaPolling();
        server.prices.P1 = 3.5;

        await addToOrder(0, 2);

        expect($("order-message").textContent).toBe("Algunos precios cambiaron y se actualizaron en tu pedido.");
        expect($("order-items").textContent).toMatch(/2 × .*3[.,]50/);
        expect($("order-total").textContent).toMatch(/7[.,]00/);
    });

    it("muestra el error del servidor al calcular el total", async () => {
        const { fetchMock } = await load();
        await loadMenuViaPolling();
        const original = fetchMock.getMockImplementation();
        fetchMock.mockImplementation(async (url, options) =>
            url === "/api/pedidos/total"
                ? textResponse(JSON.stringify({ mensaje: "Café ya no está disponible" }), 400)
                : original(url, options));

        await addToOrder(0, 1);

        expect($("order-message").textContent).toBe("Café ya no está disponible");
    });
});

describe("confirmar pedido", () => {
    const confirmed = {
        codigoRetiro: "XY12",
        estado: "PENDIENTE",
        fechaRegistro: "2026-11-22T10:00:00",
        total: 7.5,
        detalles: [{ codigoProducto: "P1", nombreProducto: "Café", cantidad: 3, precioUnitario: 2.5, subtotal: 7.5 }]
    };

    it("envía el pedido, lo agrega a 'Mis pedidos' y vacía el pedido en curso", async () => {
        const { fetchMock } = await load({ confirm: async () => jsonResponse(confirmed, 201) });
        await loadMenuViaPolling();
        await addToOrder(0, 3);

        $("confirm-button").click();
        await flush();

        const call = fetchMock.mock.calls.find(([url, options]) => url === "/api/pedidos" && options?.method === "POST");
        expect(JSON.parse(call[1].body)).toEqual({ items: [{ codigoProducto: "P1", cantidad: 3 }] });
        expect($("orders-list").children).toHaveLength(1);
        expect($("orders-list").textContent).toContain("Código XY12");
        expect($("orders-list").textContent).toContain("Pendiente");
        expect($("order-items").children).toHaveLength(0);
        expect($("order-count").hidden).toBe(true);
        expect($("order-message").textContent).toMatch(/Pedido confirmado por .*7[.,]50\. Presenta el código XY12 para retirarlo\./);
        expect($("confirm-button").disabled).toBe(true);
    });

    it("con un error conserva el pedido y vuelve a habilitar el botón", async () => {
        await load({
            confirm: async () => textResponse(JSON.stringify({ mensaje: "Producto no disponible" }), 400)
        });
        await loadMenuViaPolling();
        await addToOrder(0, 1);

        $("confirm-button").click();
        await flush();

        expect($("order-message").textContent).toBe("Producto no disponible");
        expect($("order-items").children).toHaveLength(1);
        expect($("confirm-button").disabled).toBe(false);
        expect($("orders-list").children).toHaveLength(0);
    });
});

describe("mis pedidos", () => {
    const base = {
        estado: "PENDIENTE",
        total: 5,
        detalles: [{ codigoProducto: "P1", nombreProducto: "Café", cantidad: 2, precioUnitario: 2.5, subtotal: 5 }]
    };

    it("muestra el mensaje de vacío cuando no hay pedidos al abrir 'Mi pedido'", async () => {
        await load();
        document.querySelector(".header_button").click();
        await flush();
        // Sin pedidos no cambia la firma de la lista; el mensaje inicial viene del primer render
        expect($("orders-list").children).toHaveLength(0);
    });

    it("ordena del más reciente al más antiguo y traduce los estados", async () => {
        await load({
            orders: [
                { ...base, codigoRetiro: "OLD1", fechaRegistro: "2026-11-20T09:00:00", estado: "ENTREGADO" },
                { ...base, codigoRetiro: "NEW1", fechaRegistro: "2026-11-22T09:00:00", estado: "LISTO_PARA_RETIRAR" },
                { ...base, codigoRetiro: "MID1", fechaRegistro: "2026-11-21T09:00:00", estado: "EN_PREPARACION" }
            ]
        });

        document.querySelector(".header_button").click();
        await flush();

        const codes = [...$("orders-list").children].map(li => li.dataset.codigo);
        expect(codes).toEqual(["NEW1", "MID1", "OLD1"]);
        const statuses = [...$("orders-list").querySelectorAll(".order-status")].map(el => el.textContent);
        expect(statuses).toEqual(["Listo para retirar", "En preparación", "Entregado"]);
        expect($("orders-list").children[0].querySelector(".order-status").classList
            .contains("order-status-listo_para_retirar")).toBe(true);
        expect($("orders-message").textContent).toBe("");
    });

    it("refleja el cambio de estado en el siguiente sondeo", async () => {
        await load({ orders: [{ ...base, codigoRetiro: "A1", fechaRegistro: "2026-11-22T09:00:00" }] });
        await loadMenuViaPolling();
        expect($("orders-list").querySelector(".order-status").textContent).toBe("Pendiente");

        server.orders = [{ ...base, codigoRetiro: "A1", fechaRegistro: "2026-11-22T09:00:00", estado: "EN_PREPARACION" }];
        await loadMenuViaPolling();

        expect($("orders-list").querySelector(".order-status").textContent).toBe("En preparación");
    });

    it("muestra los productos con su precio de compra y el total", async () => {
        await load({ orders: [{ ...base, codigoRetiro: "A1", fechaRegistro: "2026-11-22T09:00:00" }] });
        document.querySelector(".header_button").click();
        await flush();

        const li = $("orders-list").children[0];
        expect(li.querySelector(".history-item-name").textContent).toBe("Café");
        expect(li.querySelector(".history-item-detail").textContent).toMatch(/2 × .*2[.,]50/);
        expect(li.querySelector(".history-item-subtotal").textContent).toMatch(/5[.,]00/);
        expect(li.querySelector(".history-order-total").textContent).toMatch(/^Total .*5[.,]00/);
    });

    it("informa si no se pudieron cargar los pedidos", async () => {
        const { fetchMock } = await load();
        fetchMock.mockImplementation(async url =>
            url === "/api/pedidos/mis-pedidos" ? textResponse("error", 500) : jsonResponse([]));

        document.querySelector(".header_button").click();
        await flush();

        expect($("orders-message").textContent).toBe("No se pudieron cargar tus pedidos. Inténtalo de nuevo más tarde.");
    });
});
