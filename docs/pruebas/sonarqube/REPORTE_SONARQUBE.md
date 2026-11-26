# Reporte de Calidad de Código — Cafesysto

> **Proyecto:** Cafesysto (`cafesysto`) <br>
> **Descripción:** Gestión de pedidos de una cafetería universitaria <br>
> **Fuente:** Análisis de SonarQube (última actualización de issues: 26/11/2026) <br>
> **Versión:** 1.1 <br>

> ⚠️ **Aviso:** Este documento fue generado mediante inteligencia artificial a partir de las exportaciones JSON de SonarQube (métricas e issues del proyecto). Las cifras provienen directamente de esos archivos; las interpretaciones, recomendaciones y ejemplos de código son orientativos y deben revisarse contra el código fuente real antes de aplicarse.

---

## 1. Resumen ejecutivo

El proyecto mantiene **buenos indicadores estructurales**: cobertura de pruebas alta (91,1 %), duplicación mínima (0,9 %) y **ninguna vulnerabilidad de seguridad**.

Hay **51 issues abiertos** (11 bugs y 40 code smells), con una deuda técnica estimada de **250 minutos (~4 h 10 min)**. Los puntos a atender primero son:

1. **11 bugs en el frontend JavaScript**: promesas sin manejar, que pueden dejar la interfaz en estados inconsistentes sin avisar al usuario.
2. **7 issues CRÍTICOS en el backend**: métodos `@Transactional` invocados mediante `this`, lo que hace que Spring ignore la configuración transaccional de esos métodos.
3. **11 code smells en las pruebas**: lambdas de `assertThrows` con más de una llamada que puede lanzar excepción, lo que permite que un test pase por el motivo equivocado.

El resto son mejoras de accesibilidad, registro (logging), manejo de zona horaria y limpieza de CSS.

---

## 2. Métricas generales

| Métrica | Valor | Valoración |
|---|---|---|
| Líneas de código (NCLOC) | 4 625 | — |
| Cobertura de pruebas | **91,1 %** | ✅ Excelente |
| Líneas duplicadas | **0,9 %** | ✅ Excelente |
| Vulnerabilidades | **0** | ✅ Óptimo |
| Bugs | **11** | ⚠️ Requiere atención |
| Code smells | **40** | 🟡 Moderado |
| Deuda técnica total | **250 min** | 🟡 Moderada |

---

## 3. Distribución de issues

### 3.1 Por severidad

| Severidad | Cantidad |
|---|---|
| 🔴 CRITICAL | 7 |
| 🟠 MAJOR | 41 |
| 🟡 MINOR | 1 |
| 🔵 INFO | 2 |
| **Total** | **51** |

### 3.2 Por regla

| # | Regla | Descripción | Tipo | Severidad | Cant. | Esfuerzo |
|---|---|---|---|---|---|---|
| 1 | `javascript:S9383` | Promesas no manejadas | 🐞 Bug | MAJOR | 11 | 55 min |
| 2 | `java:S5778` | Lambda de `assertThrows` con varias llamadas que pueden lanzar excepción | Code smell (tests) | MAJOR | 11 | 55 min |
| 3 | `Web:S6819` | Usar `<output>` en lugar de `role="status"` | Code smell | MAJOR | 9 | 45 min |
| 4 | `java:S6809` | Método transaccional llamado vía `this` | Code smell | **CRITICAL** | 7 | 35 min |
| 5 | `css:S4666` | Selector CSS duplicado | Code smell | MAJOR | 4 | 4 min |
| 6 | `java:S106` | Uso de `System.out` en lugar de un logger | Code smell | MAJOR | 3 | 30 min |
| 7 | `Web:S6840` | Atributo `autocomplete` incorrecto | Code smell | MAJOR | 2 | 10 min |
| 8 | `java:S8688` | `.now()` sin zona horaria explícita | Code smell | INFO | 2 | 10 min |
| 9 | `java:S9391` | Usar un *stream* en lugar de un bucle | Code smell | MAJOR | 1 | 5 min |
| 10 | `javascript:S1128` | Import no utilizado | Code smell | MINOR | 1 | 1 min |

### 3.3 Por capa

| Capa | Issues | Observación |
|---|---|---|
| Frontend (`static/`: JS, HTML, CSS) | 27 | Contiene los 11 bugs |
| Backend (`src/main/java`) | 13 | Contiene los 7 issues críticos |
| Pruebas (`src/test/java`) | 11 | Todos de la regla `S5778` |

### 3.4 Por archivo

| Archivo | Bugs | Code smells | Total |
|---|---|---|---|
| `static/cliente/cliente.js` | 5 | 1 | **6** |
| `test/.../usuarios/servicio/AutenticacionServiceTest.java` | — | 5 | **5** |
| `static/personal/personal-pedidos.js` | 4 | — | **4** |
| `main/.../productos/servicio/ProductoService.java` | — | 4 | **4** |
| `static/cliente/cliente.html` | — | 3 | **3** |
| `static/index.html` | — | 3 | **3** |
| `static/administrador/administrador.html` | — | 3 | **3** |
| `main/.../CafeteriaApplication.java` | — | 3 | **3** |
| `test/.../pedidos/entidad/DetallePedidoTest.java` | — | 3 | **3** |
| `static/personal/personal-productos.js` | 2 | — | **2** |
| `static/personal/personal.html` | — | 2 | **2** |
| `static/personal/personal.css` | — | 2 | **2** |
| `static/administrador/administrador.css` | — | 2 | **2** |
| `main/.../usuarios/servicio/AutenticacionService.java` | — | 2 | **2** |
| `test/.../pedidos/servicio/PedidoServiceTest.java` | — | 2 | **2** |
| `main/.../pedidos/servicio/PedidoService.java` | — | 1 | **1** |
| `main/.../pedidos/controlador/PedidoController.java` | — | 1 | **1** |
| `main/.../pedidos/entidad/Pedido.java` | — | 1 | **1** |
| `main/.../comun/excepcion/ErrorRespuesta.java` | — | 1 | **1** |
| `test/.../pedidos/entidad/PedidoTest.java` | — | 1 | **1** |

> Las rutas Java abrevian `ec/edu/cafeteria` como `...`.

---

## 4. Detalle de issues

### 4.1 🐞 Bugs — Promesas no manejadas (`javascript:S9383`) · 11 issues

**Qué dice SonarQube:** las promesas deben esperarse con `await`, terminar en `.catch`, terminar en `.then` con un manejador de rechazo, o marcarse explícitamente como ignoradas con `void`.

**Por qué importa:** si una de estas llamadas (normalmente un `fetch` al backend) falla, el error queda como *Unhandled Promise Rejection*: el usuario no recibe aviso, la interfaz puede quedar desactualizada y el fallo es difícil de depurar.

| Archivo | Líneas |
|---|---|
| `cliente/cliente.js` | 178, 476, 477, 482, 483 |
| `personal/personal-pedidos.js` | 243, 274, 357, 360 |
| `personal/personal-productos.js` | 116, 296 |

> 🔎 **Código repetido:** por el *hash* de SonarQube, `cliente.js` 476 ≡ 482 y 477 ≡ 483, y `personal-pedidos.js` 357 ≡ 360 son la misma llamada en dos lugares. Conviene extraerlas a una función común y corregirlas una sola vez.

```js
// ❌ Antes
cargarPedidos();

// ✅ Con await y manejo de error
try {
    await cargarPedidos();
} catch (error) {
    mostrarMensajeError('No se pudieron cargar los pedidos.');
}

// ✅ Con .catch (útil en manejadores de eventos)
cargarPedidos().catch(error => mostrarMensajeError(error.message));

// ✅ Ignorar explícitamente (solo si la función ya maneja sus errores)
void cargarPedidos();
```

SonarQube ofrece *quick fix* para los 11.

---

### 4.2 🔴 CRÍTICO — Métodos transaccionales llamados vía `this` (`java:S6809`) · 7 issues

**Qué dice SonarQube:** los métodos transaccionales deben invocarse a través de una dependencia inyectada, no directamente con `this`.

**Por qué importa:** Spring aplica `@Transactional` mediante un *proxy*. Cuando un método de la clase llama a otro método `@Transactional` de la **misma** clase, la llamada no pasa por el proxy y la anotación del método llamado **se ignora** (propagación, `readOnly`, reglas de *rollback*). El código puede funcionar hoy por casualidad, porque el método externo ya abrió una transacción, pero el comportamiento no es el que la anotación sugiere.

| Archivo | Líneas |
|---|---|
| `productos/servicio/ProductoService.java` | 118, 141, 170, 183 |
| `usuarios/servicio/AutenticacionService.java` | 86, 91 |
| `pedidos/servicio/PedidoService.java` | 170 |

> 🔎 `ProductoService.java` 118 ≡ 141: misma llamada repetida.

**Opciones de corrección:**

- **Si el método llamado solo necesita la transacción del llamador:** quitarle `@Transactional` o convertirlo en un método `private` auxiliar.
- **Si necesita su propia configuración transaccional** (por ejemplo `REQUIRES_NEW` o `readOnly`): moverlo a otro servicio e inyectarlo.

```java
// ❌ Antes
@Transactional
public Producto actualizar(Long id, ProductoDTO dto) {
    Producto p = this.buscarPorId(id);   // @Transactional(readOnly = true) ignorado
    ...
}

// ✅ Opción A: método auxiliar privado sin anotación
private Producto obtenerProducto(Long id) {
    return productoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(id));
}
```

---

### 4.3 🧪 Pruebas — Lambdas de `assertThrows` con varias llamadas (`java:S5778`) · 11 issues

**Qué dice SonarQube:** la lambda debe tener una sola invocación que pueda lanzar una excepción en tiempo de ejecución.

**Por qué importa:** si dentro de `assertThrows(...)` hay dos o más llamadas que pueden lanzar la excepción, el test puede pasar porque falló **la llamada equivocada** (por ejemplo, el constructor de un objeto de prueba) y no el método que realmente se quiere probar. Es un falso positivo silencioso que infla la confianza en la cobertura.

| Archivo | Líneas |
|---|---|
| `usuarios/servicio/AutenticacionServiceTest.java` | 69, 92, 116, 158, 161 |
| `pedidos/entidad/DetallePedidoTest.java` | 62, 63, 65 |
| `pedidos/servicio/PedidoServiceTest.java` | 244, 498 |
| `pedidos/entidad/PedidoTest.java` | 148 |

> 🔎 `AutenticacionServiceTest.java` 69 ≡ 92 ≡ 116: el mismo patrón repetido en tres tests.

```java
// ❌ Antes: el error puede venir de crearSolicitud() o de login()
assertThrows(CredencialesInvalidasException.class,
        () -> service.login(crearSolicitud("user", "mala")));

// ✅ Después: preparar fuera, ejecutar dentro
SolicitudLogin solicitud = crearSolicitud("user", "mala");
assertThrows(CredencialesInvalidasException.class,
        () -> service.login(solicitud));
```

---

### 4.4 ♿ Accesibilidad · 11 issues

#### `role="status"` → `<output>` (`Web:S6819`) · 9 issues

**Por qué importa:** el elemento nativo `<output>` ya tiene el rol `status` implícito y es mejor soportado por lectores de pantalla en distintos dispositivos que un `<div role="status">`.

| Archivo | Líneas |
|---|---|
| `cliente/cliente.html` | 35, 42, 53 |
| `index.html` | 57, 118 |
| `administrador/administrador.html` | 97, 142 |
| `personal/personal.html` | 67, 88 |

```html
<!-- ❌ Antes -->
<div id="mensaje" class="alerta" role="status"></div>

<!-- ✅ Después -->
<output id="mensaje" class="alerta"></output>
```

> Revisar que los estilos CSS aplicados al `div` sigan funcionando, porque `<output>` es un elemento *inline* por defecto (puede requerir `display: block`).

#### Atributo `autocomplete` incorrecto (`Web:S6840`) · 2 issues

| Archivo | Líneas |
|---|---|
| `administrador/administrador.html` | 71–72 |
| `index.html` | 91–93 |

Usar *tokens* estándar según el propósito del campo: `username`, `current-password`, `new-password`, `email`, o `off` para desactivarlo.

---

### 4.5 ☕ Backend — Otras mejoras · 6 issues

#### `System.out` en lugar de logger (`java:S106`) · 3 issues

`CafeteriaApplication.java`, líneas 23, 24 y 25. En Spring Boot ya está disponible SLF4J:

```java
private static final Logger log = LoggerFactory.getLogger(CafeteriaApplication.class);
...
log.info("Aplicación iniciada en el puerto {}", puerto);
```

#### `.now()` sin zona horaria (`java:S8688`) · 2 issues (INFO)

| Archivo | Línea |
|---|---|
| `pedidos/entidad/Pedido.java` | 96 |
| `comun/excepcion/ErrorRespuesta.java` | 10 |

Aunque SonarQube lo marca como INFO, **en este proyecto merece atención**: si el servidor se despliega en UTC, las fechas de los pedidos quedarían 5 horas adelantadas respecto a la hora de Ecuador (UTC−5).

```java
LocalDateTime.now(ZoneId.of("America/Guayaquil"));
// o, mejor para pruebas, inyectar un Clock:
LocalDateTime.now(clock);
```

#### Bucle reemplazable por *stream* (`java:S9391`) · 1 issue

`pedidos/controlador/PedidoController.java`, línea 214. Es una mejora de legibilidad.

---

### 4.6 🎨 Frontend — Limpieza menor · 5 issues

#### Selectores CSS duplicados (`css:S4666`) · 4 issues

| Archivo | Selector | Línea duplicada | Primera aparición |
|---|---|---|---|
| `personal/personal.css` | `.product-actions` | 187 | 170 |
| `personal/personal.css` | `.badge` | 193 | 169 |
| `administrador/administrador.css` | `.product-actions` | 187 | 170 |
| `administrador/administrador.css` | `.badge` | 193 | 169 |

> 🔎 **Ambas hojas de estilo tienen exactamente los mismos issues en las mismas líneas** (y el mismo *hash*), lo que indica que una es copia de la otra. Además de fusionar los selectores duplicados, conviene mover los estilos compartidos a un CSS común.

#### Import no utilizado (`javascript:S1128`) · 1 issue

`cliente/cliente.js`, línea 1: eliminar el import de `getUser`. Tiene *quick fix*.

---

## 5. Distribución por autor

Según el campo `author` de SonarQube (último *commit* sobre la línea afectada):

| Autor | Issues | Áreas principales |
|---|---|---|
| ricardogarzon2030@gmail.com | 31 | Frontend (JS, HTML, CSS), `CafeteriaApplication`, `ErrorRespuesta` |
| bryan.ayala02@epn.edu.ec | 9 | Módulo de pedidos (servicio, controlador, entidad y tests) |
| jorge.revelo@epn.edu.ec | 7 | Módulo de autenticación (servicio y tests) |
| juan.chuga@epn.edu.ec | 4 | `ProductoService` |

---

## 6. Plan de acción recomendado

| Prioridad | Acción | Issues | Esfuerzo |
|---|---|---|---|
| 🔴 Alta | Manejar las promesas en `cliente.js`, `personal-pedidos.js` y `personal-productos.js` | 11 | 55 min |
| 🔴 Alta | Revisar las llamadas `this.` a métodos `@Transactional` en los servicios | 7 | 35 min |
| 🟠 Media | Sacar la preparación de datos fuera de las lambdas de `assertThrows` | 11 | 55 min |
| 🟠 Media | Fijar la zona horaria en `Pedido.java` y `ErrorRespuesta.java` | 2 | 10 min |
| 🟠 Media | Reemplazar `System.out` por un logger | 3 | 30 min |
| 🟢 Baja | Accesibilidad: `<output>` y `autocomplete` | 11 | 55 min |
| 🟢 Baja | Unificar CSS duplicado, usar *stream*, quitar import sin uso | 6 | 10 min |
| | **Total** | **51** | **250 min** |

Al completar las dos primeras filas (unos 90 minutos), el proyecto quedaría **sin bugs ni issues críticos**.

---

## 7. Datos del análisis

- **Estado:** 51/51 abiertos, ninguno vinculado a un ticket.
- **Alcance:** 40 en código principal, 11 en código de pruebas.
- **Quick fix disponible:** 12/51 (los 11 bugs y el import sin uso).
- **Detección:** los issues se registraron entre el 19/11/2026 y el 24/11/2026.