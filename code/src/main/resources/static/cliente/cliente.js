import { baseAPI, getUser, findResponseErrors, authFetch } from "../util.js";

(() => {
    "use strict";

    const MAX_QUANTITY = 99;
    const currency = new Intl.NumberFormat("es-EC", { style: "currency", currency: "USD" });
    const dateFormat = new Intl.DateTimeFormat("es-EC", { dateStyle: "medium", timeStyle: "short" });

    // APIs a utilizar
    const menuAPI = `${baseAPI}/productos/menu`;
    const orderAPI = `${baseAPI}/pedidos`;
    const orderTotalAPI = `${orderAPI}/total`;
    const myOrdersAPI = `${orderAPI}/mis-pedidos`;

    // Nombres legibles de los estados que devuelve la API
    const STATUS_LABELS = {
        PENDIENTE: "Pendiente",
        EN_PREPARACION: "En preparación",
        LISTO_PARA_RETIRAR: "Listo para retirar",
        ENTREGADO: "Entregado"
    };

    // Consultar Menú
    const menuList = document.getElementById("menu-list");
    const menuStatus = document.getElementById("menu-status");

    // Gestionar Orden (pedido en curso, sin confirmar)
    const orderNavButton = document.querySelector('.header_button[data-view="order"]');
    const orderCount = document.getElementById("order-count");
    const orderTotal = document.getElementById("order-total");
    const orderMessage = document.getElementById("order-message");
    const orderItems = document.getElementById("order-items");
    const confirmButton = document.getElementById("confirm-button");

    // Historial de pedidos confirmados
    const ordersList = document.getElementById("orders-list");
    const ordersMessage = document.getElementById("orders-message");

    // Variables temporales
    let items = [];
    const order = new Map();      // pedido en curso (sin confirmar), indexado por código de producto
    let orders = [];              // pedidos ya confirmados del cliente, del más reciente al más antiguo
    let totalRequestId = 0;       // identifica la última consulta de total para descartar respuestas antiguas
    let ordersRequestId = 0;      // identifica la última carga de pedidos para descartar respuestas antiguas

    // ---------- Pedido en curso ----------

    /**
     * Arma el cuerpo que espera la API a partir de los productos del pedido en curso
     * @returns {{items: {codigoProducto: string, cantidad: number}[]}} selección de productos y cantidades
     */
    function buildSelection() {
        return {
            items: [...order.values()].map(item => ({ codigoProducto: item.codigo, cantidad: item.quantity }))
        };
    }

    /**
     * Actualiza el contador de unidades del pedido en el menú de navegación
     */
    function updateOrderBadge() {
        let total = 0;
        order.forEach(item => { total += item.quantity; });
        orderCount.textContent = total;
        orderCount.hidden = total === 0;
    }

    /**
     * Calcula el importe total del pedido en curso con los precios vigentes del servidor.
     * Si el servidor informa precios distintos a los mostrados, los actualiza en pantalla.
     * @requires fetch POST a http://localhost:8080/api/pedidos/total
     */
    async function refreshOrderTotal() {
        // Cada consulta invalida a las anteriores: solo cuenta la respuesta de la más reciente
        const requestId = ++totalRequestId;

        if (!order.size) {
            orderTotal.textContent = currency.format(0);
            return;
        }

        try {
            const response = await authFetch(orderTotalAPI, {
                method: "POST",
                body: JSON.stringify(buildSelection())
            });
            const result = await response.json();
            if (requestId !== totalRequestId) { return; }

            // Tomar los precios vigentes del servidor
            let pricesChanged = false;
            result.detalles.forEach(detail => {
                const item = order.get(detail.codigoProducto);
                if (item && item.precio !== detail.precioUnitario) {
                    item.precio = detail.precioUnitario;
                    pricesChanged = true;
                }
            });
            if (pricesChanged) {
                renderOrderItems();
                orderMessage.textContent = "Algunos precios cambiaron y se actualizaron en tu pedido.";
            }
            orderTotal.textContent = currency.format(result.total);

        } catch (error) {
            if (requestId !== totalRequestId) { return; }
            // Por ejemplo, un producto que dejó de estar disponible
            console.error("No se pudo calcular el total del pedido:", error);
            orderMessage.textContent = error.message || "No se pudo calcular el total. Inténtalo de nuevo más tarde.";
        }
    }

    /**
     * Crea la fila de un producto del pedido en curso
     * @param {object} item producto con atributos de codigo, nombre, precio y quantity (cantidad pedida)
     * @returns fila HTML del producto con su subtotal y el botón para retirarlo
     */
    function createOrderItem(item) {
        const li = document.createElement("li");
        li.className = "order-item";

        const name = document.createElement("span");
        name.className = "order-item-name";
        name.textContent = item.nombre;

        const detail = document.createElement("span");
        detail.className = "order-item-detail";
        detail.textContent = `${item.quantity} × ${currency.format(item.precio)}`;

        const subtotal = document.createElement("strong");
        subtotal.className = "order-item-subtotal";
        subtotal.textContent = currency.format(Math.round(item.precio * 100) * item.quantity / 100);

        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.className = "remove-button";
        removeButton.textContent = "Retirar";
        removeButton.setAttribute("aria-label", `Retirar ${item.nombre} del pedido`);
        removeButton.addEventListener("click", () => removeFromOrder(item));

        li.append(name, detail, subtotal, removeButton);
        return li;
    }

    /**
     * Retira un producto del pedido en curso y recalcula el total.
     * El pedido sin confirmar solo existe en el navegador, por lo que no se llama a la API para retirarlo.
     * @param {object} item producto del pedido con atributo codigo
     * @requires fetch POST a http://localhost:8080/api/pedidos/total (para el nuevo total)
     */
    function removeFromOrder(item) {
        order.delete(item.codigo);
        renderOrder();
        orderMessage.textContent = order.size
            ? `Se retiró ${item.nombre} del pedido.`
            : `Se retiró ${item.nombre} del pedido. Tu pedido está vacío.`;
        // El botón pulsado desaparece, así que el foco vuelve al título de la sección.
        document.getElementById("order-title").focus();
    }

    /**
     * Dibuja la lista de productos del pedido en curso, el contador y el botón de confirmar
     */
    function renderOrderItems() {
        orderItems.replaceChildren(...[...order.values()].map(createOrderItem));
        orderMessage.textContent = order.size ? "" : "Tu pedido está vacío.";
        updateOrderBadge();
        // No se puede confirmar un pedido vacío
        confirmButton.disabled = order.size === 0;
    }

    /**
     * Dibuja el pedido en curso y consulta a la API su importe total
     */
    function renderOrder() {
        renderOrderItems();
        refreshOrderTotal();
    }

    /**
     * Agrega un producto al pedido en curso (si ya estaba, suma la cantidad) y recalcula el total
     * @param {object} item producto con atributos de codigo, nombre, precio y estado
     * @param {number} quantity unidades a agregar, entre 1 y MAX_QUANTITY
     */
    function addToOrder(item, quantity) {
        const existing = order.get(item.codigo);
        const newQuantity = Math.min(MAX_QUANTITY, (existing ? existing.quantity : 0) + quantity);
        order.set(item.codigo, { ...item, quantity: newQuantity });
        renderOrder();
    }

    /**
     * Confirma el pedido en curso: la API lo guarda como PENDIENTE y le asigna un código de retiro.
     * El pedido confirmado se agrega al inicio de "Mis pedidos" y el pedido en curso queda vacío.
     * @requires fetch POST a http://localhost:8080/api/pedidos (requiere token de cliente)
     */
    async function confirmOrder() {
        if (!order.size) {
            orderMessage.textContent = "Agrega al menos un producto antes de confirmar tu pedido.";
            return;
        }

        // Evita enviar el pedido dos veces con un doble clic
        confirmButton.disabled = true;
        try {
            const response = await authFetch(orderAPI, {
                method: "POST",
                body: JSON.stringify(buildSelection())
            });
            const pedido = await response.json();

            // El pedido ya está guardado: se suma a la lista y se descartan las cargas de pedidos en curso,
            // que podrían traer una lista anterior a esta confirmación
            ordersRequestId++;
            orders.unshift(pedido);
            ordersSignatureCache = ordersSignature(orders);
            renderOrders();

            order.clear();
            renderOrder();
            orderMessage.textContent =
                `Pedido confirmado por ${currency.format(pedido.total)}. Presenta el código ${pedido.codigoRetiro} para retirarlo.`;

        } catch (error) {
            console.error("No se pudo confirmar el pedido:", error);
            orderMessage.textContent = error.message || "No se pudo confirmar el pedido. Inténtalo de nuevo más tarde.";
            confirmButton.disabled = order.size === 0;
        }
    }

    // ---------- Mis pedidos (pedidos confirmados) ----------

    /**
     * Crea la tarjeta de un pedido confirmado con su código de retiro, estado, fecha, productos y total
     * @param {object} pedido pedido con atributos de codigoRetiro, fechaRegistro, total, estado y detalles
     * @returns tarjeta HTML del pedido para anexar
     */
    function createOrderCard(pedido) {
        const li = document.createElement("li");
        li.className = "history-order";
        li.dataset.codigo = pedido.codigoRetiro;

        // Código de retiro y estado del pedido
        const header = document.createElement("div");
        header.className = "history-order-header";

        const code = document.createElement("strong");
        code.className = "history-order-code";
        code.textContent = `Código ${pedido.codigoRetiro}`;

        const status = document.createElement("span");
        status.className = `order-status order-status-${pedido.estado.toLowerCase()}`;
        status.textContent = STATUS_LABELS[pedido.estado] ?? pedido.estado;
        header.append(code, status);

        // Fecha en que se confirmó el pedido
        const date = document.createElement("p");
        date.className = "history-order-date";
        date.textContent = dateFormat.format(new Date(pedido.fechaRegistro));

        // Productos del pedido con el precio al que se compraron
        const details = document.createElement("ul");
        details.className = "history-items";
        details.setAttribute("aria-label", `Productos del pedido ${pedido.codigoRetiro}`);
        pedido.detalles.forEach(detalle => {
            const row = document.createElement("li");
            row.className = "history-item";

            const name = document.createElement("span");
            name.className = "history-item-name";
            name.textContent = detalle.nombreProducto;

            const detail = document.createElement("span");
            detail.className = "history-item-detail";
            detail.textContent = `${detalle.cantidad} × ${currency.format(detalle.precioUnitario)}`;

            const subtotal = document.createElement("strong");
            subtotal.className = "history-item-subtotal";
            subtotal.textContent = currency.format(detalle.subtotal);

            row.append(name, detail, subtotal);
            details.append(row);
        });

        // Importe total del pedido
        const total = document.createElement("p");
        total.className = "history-order-total";
        total.textContent = `Total ${currency.format(pedido.total)}`;

        li.append(header, date, details, total);
        return li;
    }

    /**
     * Genera las tarjetas de todos los pedidos confirmados, del más reciente al más antiguo
     */
    function renderOrders() {
        ordersList.replaceChildren(...orders.map(createOrderCard));
        ordersMessage.textContent = orders.length ? "" : "Aún no tienes pedidos confirmados.";
    }

    /**
 * Firma canónica de una lista de pedidos.
 * Incluye estado, total y detalles, ordenada de forma estable.
 */
    function ordersSignature(list) {
        return list
            .map(pedido => [
                pedido.codigoRetiro,
                pedido.estado,
                pedido.total,
                pedido.fechaRegistro,
                (pedido.detalles || [])
                    .map(d => `${d.cantidad}x${d.codigoProducto ?? d.nombreProducto}:${d.precioUnitario}:${d.subtotal}`)
                    .join("|")
            ].join("~"))
            .sort()
            .join("§");
    }

    let ordersSignatureCache = null; // firma del último render de "Mis pedidos"

    /**
     * Obtiene todos los pedidos confirmados del cliente. Se vuelve a llamar al abrir "Mi pedido"
     * para actualizar los estados, que cambian cuando el personal prepara o entrega cada pedido.
     * @requires fetch GET a http://localhost:8080/api/pedidos/mis-pedidos (requiere token de cliente)
     */
    async function loadOrders() {
        const requestId = ++ordersRequestId;

        try {
            const response = await authFetch(myOrdersAPI);
            const pedidos = await response.json();
            if (requestId !== ordersRequestId) { return; }

            pedidos.sort((a, b) => new Date(b.fechaRegistro) - new Date(a.fechaRegistro));

            const nextSignature = ordersSignature(pedidos);
            if (nextSignature === ordersSignatureCache) { return; } // sin cambios → no re-render

            orders = pedidos;
            ordersSignatureCache = nextSignature;
            renderOrders();

        } catch (error) {
            if (requestId !== ordersRequestId) { return; }
            console.error("No se pudieron cargar los pedidos:", error);
            ordersMessage.textContent = "No se pudieron cargar tus pedidos. Inténtalo de nuevo más tarde.";
        }
    }

    // ---------- Menú ----------

    /**
     * Crea una tarjeta de producto a partir de un objeto de producto
     * @param {object} item producto con atributos de codigo, nombre, precio y estado
     * @returns tarjeta HTML de producto para anexar
     */
    function createMenuItem(item) {
        const li = document.createElement("li");
        li.className = "menu-item";

        // Agregar los datos del producto
        const name = document.createElement("h2");
        name.className = "menu-item-name";
        name.textContent = item.nombre;

        const price = document.createElement("p");
        price.className = "menu-item-price";
        price.textContent = currency.format(item.precio);

        // Formulario para ingresar la cantidad a pedir
        const quantityId = `quantity-${item.codigo}`;
        const quantityLabel = document.createElement("label");
        quantityLabel.className = "menu-item-quantity";
        quantityLabel.htmlFor = quantityId;

        const labelText = document.createElement("span");
        labelText.textContent = "Cantidad";
        const input = document.createElement("input");
        input.id = quantityId;
        input.type = "number";
        input.inputMode = "numeric";
        input.min = "1";
        input.max = String(MAX_QUANTITY);
        input.step = "1";
        input.value = "1";
        quantityLabel.append(labelText, input);

        // Botón para agregar producto al pedido
        const button = document.createElement("button");
        button.type = "button";
        button.className = "add-button";
        button.textContent = "Agregar al pedido";
        button.setAttribute("aria-label", `Agregar ${item.nombre} al pedido`);

        button.addEventListener("click", () => {
            const quantity = Number(input.value);
            if (!Number.isInteger(quantity) || quantity < 1 || quantity > MAX_QUANTITY) {
                menuStatus.textContent = `Ingresa una cantidad entre 1 y ${MAX_QUANTITY} para ${item.nombre}.`;
                input.focus();
                return;
            }
            addToOrder(item, quantity);
            input.value = "1";
            menuStatus.textContent = `Se agregó ${quantity} × ${item.nombre} al pedido.`;
        });

        li.append(name, price, quantityLabel, button);
        return li;
    }

    /**
     * Genera las tarjetas de todos los productos
     */
    function renderMenu() {
        menuList.replaceChildren(...items.map(createMenuItem));
        menuStatus.textContent = items.length ? "" : "El menú no tiene productos por ahora.";
    }

    /**
     * Firma canónica del menú (productos disponibles).
     */
    function menuSignature(list) {
        return list
            .map(item => `${item.codigo}~${item.nombre}~${item.precio}~${item.estado ?? ""}`)
            .sort()
            .join("§");
    }

    let menuSignatureCache = null;   // firma del último render del menú

    /**
     * Obtiene una lista de todos los productos disponibles
     * @requires fetch a http://localhost:8080/api/productos/menu
     */
    async function loadMenu() {
        try {
            // Enviar los datos a la API para el registro
            const response = await fetch(menuAPI, {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            });

            if (!response.ok) { await findResponseErrors(response) }

            // Actualizar la lista de productos si no se produjeron errores
            items = await response.json();
            const nextSignature = menuSignature(items);
            if (nextSignature === menuSignatureCache) { menuStatus.textContent = ""; return; }

            menuSignatureCache = nextSignature;
            renderMenu();
            menuStatus.textContent = "";

        } catch (error) {
            // Mostrar errores al usuario
            console.error("No se pudieron cargar los productos:", error);
            menuStatus.textContent = "No se pudieron cargar los productos. Inténtalo de nuevo más tarde.";
        }
    }

    confirmButton.addEventListener("click", confirmOrder);
    // Al abrir "Mi pedido" se vuelven a consultar los pedidos, cuyos estados pudieron cambiar mientras el cliente estaba en otra vista
    orderNavButton.addEventListener("click", loadOrders);

    renderOrder();

    // Refrescar los pedidos cada segundo
    const POLL_MS = 1000;
    setInterval(() => {
        if (!document.hidden) {
            loadMenu();
            loadOrders();
        }
    }, POLL_MS);
    document.addEventListener("visibilitychange", () => {
        if (!document.hidden) {
            loadMenu();
            loadOrders();
        }
    });
})();