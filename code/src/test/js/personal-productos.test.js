import { describe, it, expect, afterEach, vi } from "vitest";
import { setupEnv, jsonResponse, textResponse, flush, loginAs, submit } from "./helpers.js";

const HTML = `
<form id="product-form">
  <input id="product-name"><input id="product-price">
  <input id="product-available" type="checkbox">
  <p id="products-status"></p>
</form>
<p id="products-empty" hidden></p>
<ul id="product-list"></ul>`;

const $ = id => document.getElementById(id);
const PRODUCTS = () => [
    { codigo: "P1", nombre: "Café", precio: 2.5, estado: "DISPONIBLE" },
    { codigo: "P2", nombre: "Sánduche", precio: 3, estado: "NO_DISPONIBLE" }
];
const item = code => document.querySelector(`#product-list [data-codigo="${code}"]`);

/** Carga el módulo con un listado inicial de productos */
async function load(initial = PRODUCTS()) {
    const env = setupEnv(HTML);
    loginAs({ rol: "PERSONAL", token: "tok" });
    env.fetchMock.mockResolvedValue(jsonResponse(initial));
    await import("@static/personal/personal-productos.js");
    await flush();
    return env;
}

afterEach(() => vi.unstubAllGlobals());

describe("carga y listado", () => {
    it("pide los productos con el token y los dibuja", async () => {
        const { fetchMock } = await load();

        const [url, options] = fetchMock.mock.calls[0];
        expect(url).toBe("/api/productos");
        expect(options.headers.Authorization).toBe("Bearer tok");
        expect(document.querySelectorAll(".product-item")).toHaveLength(2);
        expect(item("P1").querySelector(".product-name").textContent).toBe("Café");
        expect(item("P1").querySelector(".product-price").textContent).toMatch(/2[.,]50/);
        expect($("products-empty").hidden).toBe(true);
    });

    it("muestra la etiqueta de disponibilidad correcta", async () => {
        await load();

        const ok = item("P1").querySelector(".badge");
        const no = item("P2").querySelector(".badge");
        expect(ok.textContent).toBe("Disponible");
        expect(ok.classList.contains("badge-yes")).toBe(true);
        expect(no.textContent).toBe("No disponible");
        expect(no.classList.contains("badge-no")).toBe(true);
    });

    it("muestra el mensaje de lista vacía", async () => {
        await load([]);
        expect($("products-empty").hidden).toBe(false);
        expect(document.querySelectorAll(".product-item")).toHaveLength(0);
    });

    it("informa si no se pudieron cargar los productos", async () => {
        const env = setupEnv(HTML);
        loginAs({ rol: "PERSONAL", token: "tok" });
        env.fetchMock.mockResolvedValue(textResponse("boom", 500));
        vi.spyOn(console, "error").mockImplementation(() => {});
        await import("@static/personal/personal-productos.js");
        await flush();

        expect($("products-status").textContent).toBe("No se pudieron cargar los productos. Inténtalo de nuevo más tarde.");
    });
});

describe("registrar producto", () => {
    it("envía el producto como DISPONIBLE si la casilla está marcada y lo agrega", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(jsonResponse({ codigo: "P3", nombre: "Té", precio: 1.25, estado: "DISPONIBLE" }, 201));
        $("product-name").value = "Té";
        $("product-price").value = "1.25";
        $("product-available").checked = true;

        submit($("product-form"));
        await flush();

        const [url, options] = fetchMock.mock.calls[1];
        expect(url).toBe("/api/productos");
        expect(options.method).toBe("POST");
        expect(JSON.parse(options.body)).toEqual({ nombre: "Té", precio: "1.25", estado: "DISPONIBLE" });
        expect(item("P3")).not.toBeNull();
        expect($("products-status").textContent).toBe("Producto registrado exitosamente");
        expect($("product-name").value).toBe("");
    });

    it("envía NO_DISPONIBLE si la casilla está desmarcada", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(jsonResponse({ codigo: "P3", nombre: "Té", precio: 1, estado: "NO_DISPONIBLE" }, 201));
        $("product-name").value = "Té";
        $("product-price").value = "1";

        submit($("product-form"));
        await flush();

        expect(JSON.parse(fetchMock.mock.calls[1][1].body).estado).toBe("NO_DISPONIBLE");
    });

    it("muestra el error y no agrega nada si la API rechaza el producto", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(textResponse(JSON.stringify({ mensaje: "Nombre repetido" }), 409));
        $("product-name").value = "Café";

        submit($("product-form"));
        await flush();

        expect($("products-status").textContent).toBe("Nombre repetido");
        expect(document.querySelectorAll(".product-item")).toHaveLength(2);
    });
});

describe("cambiar precio", () => {
    it("muestra el formulario de edición con el precio actual y puede cancelarse", async () => {
        await load();

        item("P1").querySelector('[data-action="edit"]').click();

        const input = item("P1").querySelector(".price-input");
        expect(input).not.toBeNull();
        expect(input.value).toBe("2.50");
        expect(document.activeElement).toBe(input);
        expect(item("P1").querySelector('[data-action="edit"]')).toBeNull();

        item("P1").querySelector(".price-form .btn-secondary").click();

        expect(item("P1").querySelector(".price-input")).toBeNull();
        expect(document.activeElement).toBe(item("P1").querySelector('[data-action="edit"]'));
    });

    it("guarda el nuevo precio redondeado a dos decimales", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(jsonResponse({}));

        item("P1").querySelector('[data-action="edit"]').click();
        const input = item("P1").querySelector(".price-input");
        input.value = "3.456";
        submit(item("P1").querySelector(".price-form"));
        await flush();

        const [url, options] = fetchMock.mock.calls[1];
        expect(url).toBe("/api/productos/P1/precio");
        expect(options.method).toBe("PATCH");
        expect(JSON.parse(options.body)).toEqual({ precio: 3.46 });
        expect(item("P1").querySelector(".product-price").textContent).toMatch(/3[.,]46/);
        expect($("products-status").textContent).toMatch(/El precio de Café se cambió a .*3[.,]46/);
        expect(item("P1").querySelector(".price-input")).toBeNull();
    });

    it("muestra el error y mantiene el precio anterior si la API falla", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(textResponse(JSON.stringify({ mensaje: "Precio inválido" }), 400));

        item("P1").querySelector('[data-action="edit"]').click();
        item("P1").querySelector(".price-input").value = "-1";
        submit(item("P1").querySelector(".price-form"));
        await flush();

        expect($("products-status").textContent).toBe("Precio inválido");
        expect(item("P1").querySelector(".price-input")).not.toBeNull();
    });
});

describe("cambiar disponibilidad", () => {
    it("marca como no disponible un producto disponible", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(jsonResponse({}));

        const toggle = item("P1").querySelector('[data-action="toggle"]');
        expect(toggle.textContent).toBe("Marcar no disponible");
        toggle.click();
        await flush();

        const [url, options] = fetchMock.mock.calls[1];
        expect(url).toBe("/api/productos/P1/disponibilidad");
        expect(options.method).toBe("PATCH");
        expect(JSON.parse(options.body)).toEqual({ estado: "NO_DISPONIBLE" });
        expect(item("P1").querySelector(".badge").textContent).toBe("No disponible");
        expect($("products-status").textContent).toBe("Café ahora está no disponible.");
        expect(document.activeElement).toBe(item("P1").querySelector('[data-action="toggle"]'));
    });

    it("marca como disponible un producto no disponible", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(jsonResponse({}));

        item("P2").querySelector('[data-action="toggle"]').click();
        await flush();

        expect(JSON.parse(fetchMock.mock.calls[1][1].body)).toEqual({ estado: "DISPONIBLE" });
        expect(item("P2").querySelector(".badge").textContent).toBe("Disponible");
    });

    it("conserva el estado y muestra el error si la API falla", async () => {
        const { fetchMock } = await load();
        fetchMock.mockResolvedValueOnce(textResponse(JSON.stringify({ mensaje: "No se pudo actualizar" }), 500));

        item("P1").querySelector('[data-action="toggle"]').click();
        await flush();

        expect($("products-status").textContent).toBe("No se pudo actualizar");
        expect(item("P1").querySelector(".badge").textContent).toBe("Disponible");
    });
});
