import { baseAPI, authFetch } from "../util.js";

(() => {
    "use strict";
    const currency = new Intl.NumberFormat("es-EC", { style: "currency", currency: "USD" });

    // APIs a utilizar
    const orderAPI = `${baseAPI}/pedidos`;

    // Estados de la API (enum EstadoPedido) → columna del tablero.
    // ENTREGADO no tiene columna: un pedido entregado sale del tablero.
    const STATUS_KEYS = {
        PENDIENTE: "pendiente",
        EN_PREPARACION: "preparacion",
        LISTO_PARA_RETIRAR: "listo"
    };
    const STATUS_LABELS = {
        pendiente: "Pendiente",
        preparacion: "En preparación",
        listo: "Listo para retirar"
    };
    // Un pedido solo puede avanzar un paso: pendiente → en preparación → listo.
    const NEXT_STATUS = { pendiente: "preparacion", preparacion: "listo" };

    // Navegación
    const ordersNavButton = document.querySelector('.header_button[data-view="orders"]');

    // Pedidos
    const deliverForm = document.getElementById("deliver-form");
    const deliverCode = document.getElementById("deliver-code");
    const ordersStatus = document.getElementById("orders-status");
    const deliveryInfo = document.getElementById("delivery-info");
    const deliveryCode = document.getElementById("delivery-code");
    const deliveryItems = document.getElementById("delivery-items");
    const deliveryTotal = document.getElementById("delivery-total");
    const deliveryTime = document.getElementById("delivery-time");

    // Tablero
    const columns = document.querySelectorAll(".kanban-column[data-status]");
    const boardLists = {};
    const boardCounts = {};
    Object.values(STATUS_KEYS).forEach(key => {
        boardLists[key] = document.getElementById(`list-${key}`);
        boardCounts[key] = document.getElementById(`count-${key}`);
    });

    // Variables temporales
    let orders = [];
    let draggedCode = null;
    let dropped = false;
    let ordersRequestId = 0;          // descarta respuestas de cargas antiguas
    const advancing = new Set();      // códigos con un avance en curso (evita doble envío)

    // ---------- Utilidades ----------

    /**
     * Obtiene la columna del tablero que corresponde al estado de un pedido
     * @param {object} order pedido con atributo estado (valor del enum de la API)
     * @returns clave de columna (pendiente, preparacion, listo) o undefined si no se muestra
     */
    function statusKey(order) {
        return STATUS_KEYS[order.estado];
    }

    function formatMoney(value) {
        return currency.format(Number(value));
    }

    function itemsSummary(order) {
        return order.detalles.map(item => `${item.cantidad} × ${item.nombreProducto}`).join(", ");
    }

    // ---------- Tablero ----------

    /**
     * Crea una tarjeta de pedido a partir de un objeto de pedido
     * @param {object} order pedido con atributos de codigoRetiro, estado, total y detalles
     * @returns tarjeta HTML de pedido para anexar a una columna del tablero
     */
    function createOrderCard(order) {
        const li = document.createElement("li");
        li.className = "kanban-card";
        li.dataset.codigo = order.codigoRetiro;
        li.tabIndex = -1;

        // Agregar los datos del pedido
        const code = document.createElement("strong");
        code.textContent = order.codigoRetiro;

        const items = document.createElement("p");
        items.className = "kanban-items";
        items.textContent = itemsSummary(order);

        const total = document.createElement("p");
        total.className = "kanban-total";
        total.textContent = formatMoney(order.total);

        li.append(code, items, total);

        // Solo los pedidos que pueden avanzar se pueden arrastrar
        const next = NEXT_STATUS[statusKey(order)];
        if (next) {
            li.draggable = true;
            li.classList.add("is-movable");

            li.addEventListener("dragstart", event => {
                draggedCode = order.codigoRetiro;
                dropped = false;
                event.dataTransfer.effectAllowed = "move";
                event.dataTransfer.setData("text/plain", order.codigoRetiro);
                li.classList.add("dragging");
            });

            li.addEventListener("dragend", () => {
                li.classList.remove("dragging");
                clearDropHighlights();
                if (!dropped) {
                    ordersStatus.textContent =
                        `El pedido ${order.codigoRetiro} solo puede pasar a "${STATUS_LABELS[next]}".`;
                }
                draggedCode = null;
            });

            // Alternativa al arrastre para teclado y pantallas táctiles.
            const advance = document.createElement("button");
            advance.type = "button";
            advance.className = "btn btn-small btn-secondary";
            advance.textContent = `Pasar a ${STATUS_LABELS[next]}`;
            advance.setAttribute("aria-label", `Pasar el pedido ${order.codigoRetiro} a ${STATUS_LABELS[next]}`);
            advance.addEventListener("click", () => advanceOrder(order.codigoRetiro, next, { moveFocus: true }));
            li.append(advance);
        }
        return li;
    }

    function clearDropHighlights() {
        columns.forEach(column => column.classList.remove("drop-ok"));
    }

    /**
     * Dibuja el tablero con los pedidos en curso, una columna por estado
     */
    function renderBoard() {
        Object.values(STATUS_KEYS).forEach(key => {
            const inColumn = orders.filter(order => statusKey(order) === key);
            boardCounts[key].textContent = inColumn.length;
            if (inColumn.length === 0) {
                const empty = document.createElement("li");
                empty.className = "kanban-empty";
                empty.textContent = "Sin pedidos";
                boardLists[key].replaceChildren(empty);
            } else {
                boardLists[key].replaceChildren(...inColumn.map(createOrderCard));
            }
        });
    }

    /**
     * Firma canónica del listado de pedidos en curso.
     * Incluye solo lo que afecta al tablero y a los totales, ordenado de forma estable.
     */
    function ordersSignature(list) {
        return list
            .map(order => [
                order.codigoRetiro,
                order.estado,
                order.total,
                (order.detalles || [])
                    .map(d => `${d.cantidad}x${d.nombreProducto}:${d.subtotal}`)
                    .join("|")
            ].join("~"))
            .sort()
            .join("§");
    }

    let ordersSignatureCache = null; // firma del último render

    /**
     * Carga los pedidos en curso (pendientes, en preparación y listos) y dibuja el tablero.
     * Los pedidos entregados que devuelva la API no se muestran.
     * @param {object} options keepMessage: conserva el mensaje actual al terminar la carga
     * @requires fetch a http://localhost:8080/api/pedidos
     */
    async function loadOrders({ keepMessage = false } = {}) {
        const requestId = ++ordersRequestId;
        try {
            const response = await authFetch(orderAPI);
            const loaded = await response.json();
            if (requestId !== ordersRequestId) return;

            const nextOrders = loaded.filter(order => statusKey(order));
            const nextSignature = ordersSignature(nextOrders);

            // Sin cambios: no se re-renderiza el tablero
            if (nextSignature === ordersSignatureCache) {
                if (!keepMessage) ordersStatus.textContent = "";
                return;
            }

            orders = nextOrders;
            ordersSignatureCache = nextSignature;
            renderBoard();
            if (!keepMessage) ordersStatus.textContent = "";
        } catch (error) {
            if (requestId !== ordersRequestId) return;
            console.error("No se pudieron cargar los pedidos:", error);
            ordersStatus.textContent = error.message || "No se pudieron cargar los pedidos. Inténtalo de nuevo más tarde.";
        }
    }

    /**
     * Pasa un pedido al siguiente estado
     * @param {string} code código de retiro del pedido
     * @param {string} target columna destino (solo se permite el siguiente paso)
     * @param {object} options moveFocus: lleva el foco a la tarjeta movida (uso con teclado)
     * @requires fetch a http://localhost:8080/api/pedidos/{codigoRetiro}/avanzar
     */
    async function advanceOrder(code, target, { moveFocus = false } = {}) {
        const index = orders.findIndex(candidate => candidate.codigoRetiro === code);
        if (index === -1 || NEXT_STATUS[statusKey(orders[index])] !== target) return;
        if (advancing.has(code)) return;

        advancing.add(code);
        try {
            const response = await authFetch(`${orderAPI}/${encodeURIComponent(code)}/avanzar`, { method: "POST" });
            const updated = await response.json();

            // El estado definitivo es el que devuelve la API
            const current = orders.findIndex(candidate => candidate.codigoRetiro === code);
            if (current !== -1) orders[current] = updated;
            ordersSignatureCache = ordersSignature(orders);
            renderBoard();
            ordersStatus.textContent = `El pedido ${code} pasó a "${STATUS_LABELS[statusKey(updated)]}".`;

            if (moveFocus) {
                // El botón pulsado ya no existe: el foco va al botón nuevo de la tarjeta, o a la tarjeta.
                const card = [...document.querySelectorAll(".kanban-card")].find(item => item.dataset.codigo === code);
                if (card) (card.querySelector("button") || card).focus();
            }
        } catch (error) {
            ordersStatus.textContent = error.message;
            // Otro empleado pudo haber movido el pedido: se actualiza el tablero sin borrar el mensaje
            loadOrders({ keepMessage: true });
        } finally {
            advancing.delete(code);
        }
    }

    columns.forEach(column => {
        const target = column.dataset.status;
        const canDrop = () => {
            const order = orders.find(candidate => candidate.codigoRetiro === draggedCode);
            return Boolean(order) && NEXT_STATUS[statusKey(order)] === target;
        };

        column.addEventListener("dragover", event => {
            if (!canDrop()) return;
            event.preventDefault();
            event.dataTransfer.dropEffect = "move";
            column.classList.add("drop-ok");
        });

        column.addEventListener("dragleave", event => {
            if (!column.contains(event.relatedTarget)) column.classList.remove("drop-ok");
        });

        column.addEventListener("drop", event => {
            if (!canDrop()) return;
            event.preventDefault();
            const code = draggedCode;
            draggedCode = null;
            dropped = true;
            clearDropHighlights();
            advanceOrder(code, target);
        });
    });

    // ---------- Entrega ----------

    /**
     * Muestra el resumen del pedido recién entregado
     * @param {object} order pedido entregado devuelto por la API
     */
    function showDelivery(order) {
        deliveryCode.textContent = order.codigoRetiro;
        deliveryItems.replaceChildren(...order.detalles.map(item => {
            const li = document.createElement("li");
            li.textContent = `${item.cantidad} × ${item.nombreProducto} — ${formatMoney(item.subtotal)}`;
            return li;
        }));
        deliveryTotal.textContent = formatMoney(order.total);
        deliveryTime.textContent = new Date().toLocaleString("es-EC", { dateStyle: "medium", timeStyle: "short" });
        deliveryInfo.hidden = false;
    }

    /**
     * Registra la entrega de un pedido con el código de retiro que presenta el cliente
     * @requires fetch a http://localhost:8080/api/pedidos/{codigoRetiro}/entrega
     */
    async function deliverOrder() {
        const code = deliverCode.value.trim().toUpperCase();

        if (!code) {
            ordersStatus.textContent = "Ingresa el código del pedido.";
            deliverCode.focus();
            return;
        }

        // Si el pedido está en el tablero y aún no está listo, se avisa sin molestar a la API
        const onBoard = orders.find(order => order.codigoRetiro.toUpperCase() === code);
        if (onBoard && statusKey(onBoard) !== "listo") {
            ordersStatus.textContent =
                `El pedido ${onBoard.codigoRetiro} está en "${STATUS_LABELS[statusKey(onBoard)]}". ` +
                `Solo se pueden entregar pedidos que estén en "${STATUS_LABELS.listo}".`;
            deliverCode.focus();
            deliverCode.select();
            return;
        }

        const submitButton = deliverForm.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        try {
            const response = await authFetch(`${orderAPI}/${encodeURIComponent(code)}/entrega`, {
                method: "POST",
                body: JSON.stringify({ codigoPresentado: code })
            });
            const delivered = await response.json();

            // El pedido entregado sale del tablero
            orders = orders.filter(order => order.codigoRetiro !== delivered.codigoRetiro);
            ordersSignatureCache = ordersSignature(orders);
            renderBoard();
            showDelivery(delivered);
            deliverCode.value = "";
            ordersStatus.textContent = `El pedido ${delivered.codigoRetiro} fue entregado y se retiró del tablero.`;
            deliverCode.focus();
        } catch (error) {
            ordersStatus.textContent = error.message;
            deliverCode.focus();
            deliverCode.select();
        } finally {
            submitButton.disabled = false;
        }
    }

    deliverForm.addEventListener("submit", async event => {
        event.preventDefault();
        await deliverOrder();
    });

    // Al abrir la vista se actualiza el tablero con los pedidos que hayan llegado
    if (ordersNavButton) ordersNavButton.addEventListener("click", () => loadOrders());

    // Refrescar los pedidos cada segundo
    const POLL_MS = 1000;
    setInterval(() => {
        if (!document.hidden) loadOrders({ keepMessage: true });
    }, POLL_MS);
    document.addEventListener("visibilitychange", () => {
        if (!document.hidden) loadOrders({ keepMessage: true });
    });
})();