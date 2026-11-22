import { baseAPI, getUser, logout, findResponseErrors, showView } from "../util.js";

(() => {
    "use strict";
    const currency = new Intl.NumberFormat("es-EC", { style: "currency", currency: "USD" });

    // APIs a utilizar
    const productAPI = `${baseAPI}/productos`;

    // Productos
    const productForm = document.getElementById("product-form");
    const productName = document.getElementById("product-name");
    const productPrice = document.getElementById("product-price");
    const productAvailable = document.getElementById("product-available");
    const productsStatus = document.getElementById("products-status");
    const productsEmpty = document.getElementById("products-empty");
    const productList = document.getElementById("product-list");

    // Variables temporales
    let products = [];
    let editingCode = null;

    /**
     * Registra un producto
     * @requires fetch a http://localhost:8080/api/productos
     */
    async function registerProduct() {
        // Obtener datos del formulario
        const registerInfo = {
            nombre: productName.value,
            precio: productPrice.value,
            estado: productAvailable.checked ? "DISPONIBLE" : "NO_DISPONIBLE"
        };

        try {
            const user = getUser();
            if (!user) throw new Error("No está autorizado para hacer esto");
            const token = user.token;
            // Enviar los datos a la API para el registro
            const response = await fetch(productAPI, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(registerInfo)
            });
            if (!response.ok) { await findResponseErrors(response) }

            // Indicar registro exitoso si no hay errores
            productForm.reset();
            products.push(response.json());
            renderProducts();
            productsStatus.textContent = "Producto registrado exitosamente";

        } catch (error) {
            // Mostrar error al usuario
            productsStatus.textContent = error.message;
        }
    }

    productForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await registerProduct();
    });

    /**
     * Crea una tarjeta de producto a partir de un objeto de producto
     * @param {object} product producto con atributos de codigo, nombre, precio y estado
     * @returns tarjeta HTML de producto para anexar
     */
    function createProductItem(product) {
        const li = document.createElement("li");
        li.className = "product-item";
        li.dataset.codigo = product.codigo;

        const code = document.createElement("span");
        code.className = "product-code";
        code.textContent = product.codigo;

        const name = document.createElement("strong");
        name.className = "product-name";
        name.textContent = product.nombre;

        let priceCell;
        if (editingCode === product.codigo) {
            priceCell = document.createElement("form");
            priceCell.className = "price-form";

            const input = document.createElement("input");
            input.className = "price-input";
            input.type = "number";
            input.min = "0";
            input.step = "0.01";
            input.inputMode = "decimal";
            input.required = true;
            input.value = product.precio.toFixed(2);
            input.setAttribute("aria-label", `Nuevo precio de ${product.nombre}`);

            const save = document.createElement("button");
            save.type = "submit";
            save.className = "btn btn-small";
            save.textContent = "Guardar";

            const cancel = document.createElement("button");
            cancel.type = "button";
            cancel.className = "btn btn-small btn-secondary";
            cancel.textContent = "Cancelar";
            cancel.addEventListener("click", () => cancelEdit(product.codigo));

            priceCell.addEventListener("submit", event => {
                event.preventDefault();
                savePrice(product, input);
            });
            priceCell.append(input, save, cancel);
        } else {
            priceCell = document.createElement("span");
            priceCell.className = "product-price";
            priceCell.textContent = currency.format(product.precio);
        }

        const badge = document.createElement("span");
        badge.className = `badge ${product.estado == "DISPONIBLE" ? "badge-yes" : "badge-no"}`;
        badge.textContent = product.estado == "DISPONIBLE" ? "Disponible" : "No disponible";

        const actions = document.createElement("div");
        actions.className = "product-actions";

        if (editingCode !== product.codigo) {
            const edit = document.createElement("button");
            edit.type = "button";
            edit.className = "btn btn-small";
            edit.dataset.action = "edit";
            edit.textContent = "Cambiar precio";
            edit.setAttribute("aria-label", `Cambiar precio de ${product.nombre}`);
            edit.addEventListener("click", () => startEdit(product.codigo));
            actions.append(edit);
        }

        const toggle = document.createElement("button");
        toggle.type = "button";
        toggle.className = "btn btn-small btn-secondary";
        toggle.dataset.action = "toggle";
        toggle.textContent = product.estado == "DISPONIBLE" ? "Marcar no disponible" : "Marcar disponible";
        toggle.setAttribute(
            "aria-label",
            `Marcar ${product.nombre} como ${product.estado == "DISPONIBLE" ? "no disponible" : "disponible"}`
        );
        toggle.addEventListener("click", () => toggleAvailability(product));
        actions.append(toggle);

        li.append(code, name, priceCell, badge, actions);
        return li;
    }

    function renderProducts() {
        productList.replaceChildren(...products.map(createProductItem));
        productsEmpty.hidden = products.length > 0;
    }

    async function loadProducts() {
        const user = getUser();
        if (!user) throw new Error("No está autorizado para hacer esto");
        const token = user.token;
        try {
            const response = await fetch(productAPI, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Accept": "application/json"
                }
            });

            if (!response.ok) { await findResponseErrors(response) }

            products = await response.json();
            renderProducts();
            productsStatus.textContent = "";

        } catch (error) {
            console.error("No se pudieron cargar los productos:", error);
            productsStatus.textContent = "No se pudieron cargar los productos. Inténtalo de nuevo más tarde.";
        }
    }

    function startEdit(code) {
        editingCode = code;
        renderProducts();
        const input = findInProduct(code, ".price-input");
        if (input) {
            input.focus();
            input.select();
        }
    }

    function cancelEdit(code) {
        editingCode = null;
        renderProducts();
        const button = findInProduct(code, '[data-action="edit"]');
        if (button) button.focus();
    }

    function parsePrice(rawValue) {
        if (rawValue.trim() === "") return null;
        const value = Number(rawValue);
        if (!Number.isFinite(value) || value < 0) return null;
        return Math.round(value * 100) / 100;
    }

    function findInProduct(code, selector) {
        return productList.querySelector(`[data-code="${code}"] ${selector}`);
    }

    async function savePrice(product, input) {
        const price = parsePrice(input.value);

        try {
            const user = getUser();
            if (!user) throw new Error("No está autorizado para hacer esto");
            const token = user.token;
            // Enviar los datos a la API para el cambio
            const response = await fetch(`${productAPI}/${product.codigo}/precio`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({ precio: price })
            });
            if (!response.ok) { await findResponseErrors(response) }

            // Indicar cambio exitoso si no hay errores
            product.precio = price;
            editingCode = null;
            renderProducts();
            productsStatus.textContent = `El precio de ${product.nombre} se cambió a ${currency.format(price)}.`;
            const button = findInProduct(product.codigo, '[data-action="edit"]');
            if (button) button.focus();

        } catch (error) {
            // Mostrar error al usuario
            productsStatus.textContent = error.message;
        }
    }

    async function toggleAvailability(product) {
        const currentState = product.estado;
        const newState = currentState === "DISPONIBLE" ? "NO_DISPONIBLE" : "DISPONIBLE";
        try {
            const user = getUser();
            if (!user) throw new Error("No está autorizado para hacer esto");
            const token = user.token;
            // Enviar los datos a la API para el cambio
            const response = await fetch(`${productAPI}/${product.codigo}/disponibilidad`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({ estado: newState })
            });
            if (!response.ok) { await findResponseErrors(response) }

            // Indicar cambio exitoso si no hay errores
            product.estado = newState;
            renderProducts();
            productsStatus.textContent = `${product.nombre} ahora está ${product.estado === "DISPONIBLE" ? "disponible" : "no disponible"}.`;
            const button = findInProduct(product.codigo, '[data-action="toggle"]');
            if (button) button.focus();

        } catch (error) {
            // Mostrar error al usuario
            productsStatus.textContent = error.message;
        }
    }

    loadProducts();
})();