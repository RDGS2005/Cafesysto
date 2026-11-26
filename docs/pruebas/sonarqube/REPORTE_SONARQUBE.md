# Reporte de Calidad de Código — Cafesysto

> **Proyecto:** Cafesysto (`cafesysto`) <br>
> **Descripción:** Gestión de pedidos de una cafetería universitaria <br>
> **Fuente:** Análisis de SonarQube (última actualización de issues: 25/11/2026)

> ⚠️ **Aviso:** Este documento fue generado mediante inteligencia artificial a partir de los archivos `metrics.json` e `issues.json` exportados de SonarQube. Las cifras provienen directamente de esos archivos; las interpretaciones, recomendaciones y ejemplos de código son orientativos y deben revisarse contra el código fuente real antes de aplicarse.

---

## 1. Resumen ejecutivo

El proyecto muestra **buena salud general**: cobertura de pruebas alta (91,1 %), duplicación mínima (0,9 %) y **ninguna vulnerabilidad de seguridad**. El punto débil está en la **confiabilidad**: hay **11 bugs**, todos del mismo tipo (promesas sin manejar en el frontend JavaScript), más 2 *code smells* de accesibilidad en formularios HTML.

La buena noticia: los 13 problemas son de corrección rápida. La deuda técnica total estimada es de **65 minutos** (~5 min por issue), y los 11 bugs tienen *quick fix* disponible en SonarQube.

---

## 2. Métricas generales

| Métrica | Valor | Valoración |
|---|---|---|
| Líneas de código (NCLOC) | 4 625 | — |
| Cobertura de pruebas | **91,1 %** | ✅ Excelente |
| Líneas duplicadas | **0,9 %** | ✅ Excelente |
| Vulnerabilidades | **0** | ✅ Óptimo |
| Bugs | **11** | ⚠️ Requiere atención |
| Code smells | **2** | 🟡 Menor |
| Deuda técnica total | **65 min** | 🟢 Baja |

---

## 3. Distribución de issues

**Total: 13 issues abiertos** — todos con severidad **MAJOR**.

### Por tipo

| Tipo | Regla | Cantidad | Calidad afectada |
|---|---|---|---|
| 🐞 Bug | `javascript:S9383` — Promesas no manejadas | 11 | Confiabilidad (media) |
| 🧹 Code smell | `Web:S6840` — Uso incorrecto de `autocomplete` | 2 | Confiabilidad (media), Mantenibilidad (baja) |

### Por archivo

| Archivo | Bugs | Code smells | Total |
|---|---|---|---|
| `static/cliente/cliente.js` | 5 | — | **5** |
| `static/personal/personal-pedidos.js` | 4 | — | **4** |
| `static/personal/personal-productos.js` | 2 | — | **2** |
| `static/index.html` | — | 1 | **1** |
| `static/administrador/administrador.html` | — | 1 | **1** |

> Todos los issues se concentran en `src/main/resources/static/`, es decir, en el **frontend**. El backend no presenta problemas reportados.

---

## 4. Detalle de issues

### 4.1 🐞 Bugs — Promesas no manejadas (`javascript:S9383`)

**Mensaje de SonarQube:** las promesas deben esperarse con `await`, terminar en `.catch`, terminar en `.then` con un manejador de rechazo, o marcarse explícitamente como ignoradas con `void`.

**Por qué importa:** si una de estas llamadas (normalmente un `fetch` al backend) falla, el error queda como *Unhandled Promise Rejection*: el usuario no recibe ningún aviso, la interfaz puede quedar en un estado inconsistente (por ejemplo, una lista de pedidos sin refrescar) y el fallo es difícil de depurar.

#### `cliente/cliente.js` — 5 ocurrencias

| Línea | Columnas | Detectado |
|---|---|---|
| 178 | 8–28 | 22/11/2026 |
| 476 | 12–23 | 22/11/2026 |
| 477 | 12–25 | 22/11/2026 |
| 482 | 12–23 | 22/11/2026 |
| 483 | 12–25 | 22/11/2026 |

#### `personal/personal-pedidos.js` — 4 ocurrencias

| Línea | Columnas | Detectado |
|---|---|---|
| 243 | 12–46 | 22/11/2026 |
| 274 | 12–39 | 22/11/2026 |
| 357 | 30–64 | 22/11/2026 |
| 360 | 30–64 | 22/11/2026 |

#### `personal/personal-productos.js` — 2 ocurrencias

| Línea | Columnas | Detectado |
|---|---|---|
| 116 | 16–42 | 22/11/2026 |
| 296 | 4–19 | 22/11/2026 |

> 🔎 **Patrón repetido:** varias líneas comparten el mismo *hash* de código, lo que indica que son **la misma llamada copiada en dos lugares**:
> - `cliente.js` 476 ≡ 482 y 477 ≡ 483 (par de llamadas repetido dos veces)
> - `personal-pedidos.js` 357 ≡ 360
>
> Conviene revisar si ese bloque puede extraerse a una función común; así se corrige una vez y no dos.

#### Cómo corregirlo

```js
// ❌ Antes: la promesa queda "flotando"
cargarPedidos();

// ✅ Opción 1: esperar dentro de una función async con manejo de error
try {
    await cargarPedidos();
} catch (error) {
    console.error('Error al cargar pedidos:', error);
    mostrarMensajeError('No se pudieron cargar los pedidos.');
}

// ✅ Opción 2: encadenar .catch
cargarPedidos().catch(error => mostrarMensajeError(error.message));

// ✅ Opción 3: ignorar explícitamente (solo si la función ya maneja sus errores internamente)
void cargarPedidos();
```

En manejadores de eventos (`onclick`, `addEventListener`), la opción 2 suele ser la más limpia.

---

### 4.2 🧹 Code smells — Atributo `autocomplete` incorrecto (`Web:S6840`)

**Mensaje de SonarQube:** los elementos del DOM deben usar el atributo `autocomplete` correctamente.

**Por qué importa:** es un problema de **accesibilidad** (etiqueta `accessibility`). Un valor ausente o no válido impide que navegadores, gestores de contraseñas y tecnologías de asistencia rellenen y reconozcan correctamente los campos.

| Archivo | Líneas | Detectado |
|---|---|---|
| `administrador/administrador.html` | 71–72 | 24/11/2026 |
| `index.html` | 91–93 | 24/11/2026 |

#### Cómo corregirlo

Usar los *tokens* estándar de HTML según el propósito del campo:

```html
<input type="text"     name="usuario"   autocomplete="username">
<input type="password" name="clave"     autocomplete="current-password">
<input type="password" name="nuevaClave" autocomplete="new-password">
<input type="email"    name="correo"    autocomplete="email">
```

Valores inventados (por ejemplo `autocomplete="no"`) no son válidos; si se quiere desactivar, usar `autocomplete="off"`.

---

## 5. Plan de acción recomendado

| Prioridad | Acción | Esfuerzo estimado |
|---|---|---|
| 🔴 Alta | Corregir los 5 bugs de `cliente.js` (vista del cliente, la de más uso) | 25 min |
| 🔴 Alta | Corregir los 4 bugs de `personal-pedidos.js` (flujo crítico de pedidos) | 20 min |
| 🟠 Media | Corregir los 2 bugs de `personal-productos.js` | 10 min |
| 🟠 Media | Extraer a funciones comunes las llamadas duplicadas (476/482, 477/483, 357/360) | — |
| 🟢 Baja | Ajustar `autocomplete` en `index.html` y `administrador.html` | 10 min |

Al resolver los 11 bugs, el indicador de **confiabilidad** pasaría a estar limpio y el proyecto quedaría sin bugs ni vulnerabilidades abiertas.

---

## 6. Datos del análisis

- **Estado de todos los issues:** abiertos, sin ticket vinculado.
- **Severidad:** 13/13 MAJOR.
- **Quick fix disponible:** 11/13 (todos los bugs).
- **Origen:** los bugs se detectaron el 22/11/2026; los code smells, el 24/11/2026.