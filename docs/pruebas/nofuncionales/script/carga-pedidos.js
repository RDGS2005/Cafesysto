import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const ADMIN_USUARIO = __ENV.ADMIN_USUARIO || "admin@administracion.uni.edu.ec";
const ADMIN_CLAVE = __ENV.ADMIN_CLAVE || "admin123";
const ADMIN_CLAVE_NUEVA = __ENV.ADMIN_CLAVE_NUEVA || "NuevaClave123";
const NUM_CLIENTES = Number(__ENV.CLIENTES || 20);
const PEDIDOS_POR_CLIENTE = Number(__ENV.PEDIDOS_POR_CLIENTE || 5);
const CLAVE_USUARIOS = "Clave1234";

export const options = {
    setupTimeout: "300s",
    scenarios: {
        historial: {
            executor: "ramping-vus",
            exec: "consultarMisPedidos",
            startVUs: 0,
            stages: [
                { duration: "30s", target: 250 },  // carga normal
                { duration: "1m", target: 1250 },   // carga alta
                { duration: "1m", target: 1250 },   // sostenida
                { duration: "30s", target: 0 }    // bajada
            ]
        }
    },
    thresholds: {
        http_req_failed: ["rate<0.01"],
        "http_req_duration{name:mis_pedidos}": ["p(95)<2000"]
    }
};

function cabeceras(token) {
    const h = { "Content-Type": "application/json" };
    if (token) h.Authorization = `Bearer ${token}`;
    return h;
}

function enviar(metodo, ruta, cuerpo, token, nombre) {
    const params = { headers: cabeceras(token), tags: { name: nombre || `${metodo} ${ruta}` } };
    const datos = cuerpo === null || cuerpo === undefined ? null : JSON.stringify(cuerpo);
    return http.request(metodo, `${BASE_URL}${ruta}`, datos, params);
}

function exigir(respuesta, estadoEsperado, descripcion) {
    if (respuesta.status !== estadoEsperado) {
        throw new Error(`${descripcion}: se esperaba ${estadoEsperado} pero fue ${respuesta.status} (${respuesta.body})`);
    }
    return respuesta;
}

function ocho(n) {
    return String(n % 1e8).padStart(8, "0");
}

function login(rol, identificador, clave) {
    const r = exigir(enviar("POST", `/api/auth/${rol}`, { identificador, clave }, null), 200, `login ${rol}`);
    return r.json();
}

// Se ejecuta una vez antes de la prueba: crea el menú, los clientes y sus pedidos
export function setup() {
    // Administrador: si el sistema exige cambiar la clave, se cambia
    let clave = ADMIN_CLAVE;
    const admin = login("administrador", ADMIN_USUARIO, clave);
    if (admin.requiereCambioClave) {
        exigir(enviar("POST", "/api/auth/administrador/clave", {
            actual: clave, nueva: ADMIN_CLAVE_NUEVA, confirmacion: ADMIN_CLAVE_NUEVA
        }, admin.token), 204, "cambio de clave del administrador");
        clave = ADMIN_CLAVE_NUEVA;
    }

    const corrida = Date.now();
    const base = corrida % 1e8;

    // Personal, necesario para cargar el menú
    const correoPersonal = `personal${corrida}@carga.com`;
    exigir(enviar("POST", "/api/usuarios/personal", {
        nombre: "Personal", apellido: "Carga", cedula: `17${ocho(base)}`, celular: `09${ocho(base)}`,
        correo: correoPersonal, clave: CLAVE_USUARIOS
    }, admin.token), 201, "registro de personal");
    const personal = login("personal", correoPersonal, CLAVE_USUARIOS);

    // Menú
    const productos = [];
    [["Cafe", 2.5], ["Sanduche", 4], ["Jugo", 1.75], ["Pastel", 3]].forEach(([nombre, precio]) => {
        const r = exigir(enviar("POST", "/api/productos",
            { nombre: `${nombre} ${corrida}`, precio }, personal.token), 201, "registro de producto");
        productos.push(r.json().codigo);
    });

    // Clientes, cada uno con algunos pedidos para que el historial no esté vacío
    // (el costo de BCrypt del registro y del login queda fuera de la medición)
    const clientes = [];
    for (let i = 1; i <= NUM_CLIENTES; i++) {
        const correo = `cliente${corrida}_${i}@uni.edu.ec`;
        exigir(enviar("POST", "/api/usuarios/clientes", {
            nombre: "Cliente", apellido: `Carga${i}`, cedula: `17${ocho(base + i)}`, celular: `09${ocho(base + i)}`,
            correo, clave: CLAVE_USUARIOS
        }, null), 201, "registro de cliente");
        const token = login("cliente", correo, CLAVE_USUARIOS).token;

        for (let p = 0; p < PEDIDOS_POR_CLIENTE; p++) {
            const items = [
                { codigoProducto: productos[p % productos.length], cantidad: 1 + (p % 3) },
                { codigoProducto: productos[(p + 1) % productos.length], cantidad: 1 }
            ];
            exigir(enviar("POST", "/api/pedidos", { items }, token), 201, "pedido inicial");
        }
        clientes.push(token);
    }

    return { clientes };
}

export function consultarMisPedidos(datos) {
    const token = datos.clientes[(__VU - 1) % datos.clientes.length];

    const respuesta = enviar("GET", "/api/pedidos/mis-pedidos", null, token, "mis_pedidos");
    check(respuesta, {
        "historial 200": (r) => r.status === 200,
        "devuelve los pedidos del cliente": (r) => r.status === 200 && r.json().length >= PEDIDOS_POR_CLIENTE
    });
    sleep(1);
}