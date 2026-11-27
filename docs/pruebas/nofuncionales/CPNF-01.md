# CPNF-01 — Tiempo de Respuesta de Menú

## Descripción

| Campo | Descripción |
|---|---|
| **Tipo de prueba** | Carga |
| **Herramienta** | Grafana K6 |
| **Componente evaluado** | Endpoint /api/productos/menu |
| **Objetivo** | Medir el tiempo de respuesta de consultas al menú |

## Versión

v1.0

## Trazabilidad

- Requisito no funcional: RNF-01
- Issue: #37
- Pull Request: #42

**Criterio(s) de aceptación relacionado(s):**

- Tiempo transcurrido desde la solicitud del menú hasta que la información se encuentre disponible en la interfaz menor a 2 segundos

## Procedimiento de prueba

### Preparación

1. Se creó el script [`carga-menu.js`](./script/carga-menu.js)
2. Se creó y ejecutó un contenedor PostgreSQL para la base de datos

    ```powershell
    docker run -d --name cafesysto-db -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:18-alpine
    ```

3. En el directorio [`code`](../../../code/) se creó un archivo .env con las variables de entorno necesarias

    ```bash
    DB_URL=jdbc:postgresql://localhost:5432/cafesysto_db?stringtype=unspecified
    DB_USER=postgres
    DB_PASSWORD=postgres
    CAFESYSTO_CORREO_DOMINIO=uni.edu.ec

    CAFESYSTO_ADMIN_USUARIO=admin@administracion.${CAFESYSTO_CORREO_DOMINIO}
    CAFESYSTO_ADMIN_CLAVE=admin123
    ```

### Ejecución

1. Se modificó los parámetros de carga base y carga pico del script [`carga-menu.js`](./script/carga-menu.js)
2. Se ejecutó la aplicación desde el directorio [`code`](../../../code/)

    ```powershell
    mvn spring-boot:run
    ```

3. Se creó y ejecutó un contenedor de K6 desde el directorio [`script`](./script/) con el script [`carga-menu.js`](./script/carga-menu.js)

    ```powershell
    docker run --rm -i --network host grafana/k6 run - < carga.js
    ```

4. Se esperó a que termine la evaluación y se genere el reporte

## Ejecuciones del Caso de Prueba

### Ejecución 1

**Fecha:** 2026-11-25  
**Responsable:** Ricardo Garzón

#### Parámetros

- **Carga Base:** 10 consultas simultáneas
- **Carga Pico:** 50 consultas simultáneas

#### Resultados obtenidos

Con unas 2694 peticiones en total se determinó que el 95% de las consultas demoraban menos de 4.91 milisegundos en concretarse, lo que se encuentra por debajo del umbral de 2 segundos. Además el 0% de las peticiones HTTP falló.

#### Estado

- [X] Aprobado
- [ ] Reprobado

#### Capturas o evidencias

![Primera ejecución de CPNF-01](./img/cpnf-01-ejecucion1.png)

---

### Ejecución 2

**Fecha:** 2026-11-25  
**Responsable:** Ricardo Garzón

#### Parámetros

- **Carga Base:** 50 consultas simultáneas
- **Carga Pico:** 250 consultas simultáneas

#### Resultados obtenidos

Con unas 13540 peticiones en total se determinó que el 95% de las consultas demoraban menos de 6.82 milisegundos en concretarse, lo que se encuentra por debajo del umbral de 2 segundos. Además el 0% de las peticiones HTTP falló.

#### Estado

- [X] Aprobado
- [ ] Reprobado

#### Capturas o evidencias

![Segunda ejecución de CPNF-01](./img/cpnf-01-ejecucion2.png)

---

### Ejecución 3

**Fecha:** 2026-11-25  
**Responsable:** Ricardo Garzón

#### Parámetros

- **Carga Base:** 250 consultas simultáneas
- **Carga Pico:** 1250 consultas simultáneas

#### Resultados obtenidos

Con unas 59012 peticiones en total se determinó que el 95% de las consultas demoraban menos de 620.07 milisegundos en concretarse, lo que se encuentra por debajo del umbral de 2 segundos. Además el 0% de las peticiones HTTP falló.

#### Estado

- [X] Aprobado
- [ ] Reprobado

#### Capturas o evidencias

![Tercera ejecución de CPNF-01](./img/cpnf-01-ejecucion3.png)

---

### Ejecución 4

**Fecha:** 2026-11-25  
**Responsable:** Ricardo Garzón

#### Parámetros

- **Carga Base:** 1250 consultas simultáneas
- **Carga Pico:** 6250 consultas simultáneas

#### Resultados obtenidos

Con unas 86625 peticiones en total se determinó que el 95% de las consultas demoraban menos de 6.39 segundos en concretarse, lo que se encuentra por encima del umbral de 2 segundos.

#### Estado

- [ ] Aprobado
- [X] Reprobado

#### Capturas o evidencias

![Cuarta ejecución de CPNF-01](./img/cpnf-01-ejecucion4.png)

---

## Observaciones

- El tiempo de respuesta del menú menor a 2 segundos está garantizado con al menos una carga base de 250 consultas simultáneas y una carga pico de 1250 consultas simultáneas.
- El tiempo de respuesta se puede mejorar implementando caching en el cliente ya que no se espera que el menú cambie con una frecuencia tan alta.