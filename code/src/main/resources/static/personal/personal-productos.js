import { baseAPI, authFetch } from "../util.js";

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
            // Enviar los datos a la API para el registro
            const response = await authFetch(productAPI, {
                method: "POST",
                body: JSON.stringify(registerInfo)
            });

            // Indicar registro exitoso si no hay errores
            productForm.reset();
            products.push(await response.json());
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
        
        // Agregar los datos del producto
        const code = document.createElement("span");
        code.className = "product-code";
        code.textContent = product.codigo;

        const name = document.createElement("strong");
        name.className = "product-name";
        name.textContent = product.nombre;

        // Formulario de cambio de precio del producto
        let priceCell;
        if (editingCode === product.codigo) {
            priceCell = document.createElement("form");
            priceCell.className = "price-form";

            // Ingreso del nuevo precio del producto
            const input = document.createElement("input");
            input.className = "price-input";
            input.type = "number";
            input.min = "0";
            input.step = "0.01";
            input.inputMode = "decimal";
            input.required = true;
            input.value = product.precio.toFixed(2);
            input.setAttribute("aria-label", `Nuevo precio de ${product.nombre}`);

            // Botón para guardar el nuevo precio del producto
            const save = document.createElement("button");
            save.type = "submit";
            save.className = "btn btn-small";
            save.textContent = "Guardar";

            // Botón para cancelar el cambio de precio del producto
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

        // Agregar etiqueta del estado del producto
        const badge = document.createElement("span");
        badge.className = `badge ${product.estado == "DISPONIBLE" ? "badge-yes" : "badge-no"}`;
        badge.textContent = product.estado == "DISPONIBLE" ? "Disponible" : "No disponible";

        const actions = document.createElement("div");
        actions.className = "product-actions";
        
        // Botón para cambiar precio del producto
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

        // Botón para cambiar el estado del producto entre DISPONIBLE y NO DISPONIBLE
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

    /**
     * Genera las tarjetas de todos los productos
     */
    function renderProducts() {
        productList.replaceChildren(...products.map(createProductItem));
        productsEmpty.hidden = products.length > 0;
    }

    /**
     * Obtiene una lista de todos los productos
     * @requires fetch a http://localhost:8080/api/productos
     */
    async function loadProducts() {
        try {
            // Obtener la lista de productos de la API
            const response = await authFetch(productAPI);

            // Actualizar la lista de productos si no se produjeron errores
            products = await response.json();
            renderProducts();
            productsStatus.textContent = "";

        } catch (error) {
            // Mostrar errores al usuario
            console.error("No se pudieron cargar los productos:", error);
            productsStatus.textContent = "No se pudieron cargar los productos. Inténtalo de nuevo más tarde.";
        }
    }

    /**
     * Busca un elemento de un producto en la lista por código
     * @param {string} code código del producto
     * @param {*} selector elemento a buscar
     * @returns primer elemento encontrado
     */
    function findInProduct(code, selector) {
        return productList.querySelector(`[data-code="${code}"] ${selector}`);
    }

    /**
     * Empieza una operación de edición sobre un producto para cambiar precio o disponibilidad
     * @param {string} code código del producto
     */
    function startEdit(code) {
        editingCode = code;
        renderProducts();
        const input = findInProduct(code, ".price-input");
        if (input) {
            input.focus();
            input.select();
        }
    }

    /**
     * Cancela una operación de edición sobre un producto para cambiar precio o disponibilidad
     * @param {string} code código del producto
     */
    function cancelEdit(code) {
        editingCode = null;
        renderProducts();
        const button = findInProduct(code, '[data-action="edit"]');
        if (button) button.focus();
    }

    /**
     * Convertir el precio en texto a precio en decimal
     * @param {string} rawValue precio en texto
     * @returns precio en decimal
     */
    function parsePrice(rawValue) {
        if (rawValue.trim() === "") return null;
        const value = Number(rawValue);
        if (!Number.isFinite(value) || value < 0) return null;
        return Math.round(value * 100) / 100;
    }
    
    /**
     * Cambia el precio de un producto
     * @param {*} product producto a actualizar
     * @param {string} input nuevo precio
     */
    async function savePrice(product, input) {
        const price = parsePrice(input.value);

        try {
            // Enviar los datos a la API para el cambio
            await authFetch(`${productAPI}/${product.codigo}/precio`, {
                method: "PATCH",
                body: JSON.stringify({ precio: price })
            });

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

    /**
     * Cambia la disponibilidad de un producto
     * @param {*} product producto a actualizar
     */
    async function toggleAvailability(product) {
        // Obtener estado alterno
        const currentState = product.estado;
        const newState = currentState === "DISPONIBLE" ? "NO_DISPONIBLE" : "DISPONIBLE";

        try {
            // Enviar los datos a la API para el cambio
            await authFetch(`${productAPI}/${product.codigo}/disponibilidad`, {
                method: "PATCH",
                body: JSON.stringify({ estado: newState })
            });

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