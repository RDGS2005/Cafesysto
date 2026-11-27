import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
    stages: [
        { duration: "30s", target: 1250 },   // carga normal
        { duration: "1m", target: 6250 },   // estrés
        { duration: "30s", target: 0 }     // bajada
    ],
    thresholds: {
        http_req_failed: ["rate<0.01"],     // menos de 1 % de errores
        http_req_duration: ["p(95)<2000"]    // el 95 % responde en menos de 2000 ms
    }
};

const BASE = "http://localhost:8080";

export default function () {

    const menu = http.get(`${BASE}/api/productos/menu`);
    check(menu, { "menú 200": (r) => r.status === 200 });

    sleep(1);
}