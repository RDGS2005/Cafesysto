# Ambiente de Desarrollo

**Versión:** v1.3
**Autor:** Equipo 6

En este documento se describe el entorno para el desarrollo del proyecto.

> Un entorno de trabajo definido garantiza:
> - Organización
> - Estandarización
> - Consistencia
> - Reproducibilidad
> - Productividad
> - Calidad

## Tabla de Contenidos

- [Ambiente de Desarrollo](#ambiente-de-desarrollo)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Requisitos del entorno](#requisitos-del-entorno)
  - [Configuración del entorno](#configuración-del-entorno)
  - [Dependencias del proyecto](#dependencias-del-proyecto)
    - [Backend Java](#backend-java)
    - [Plugins de Maven](#plugins-de-maven)
    - [Frontend y pruebas JavaScript](#frontend-y-pruebas-javascript)
  - [Comandos de compilación, pruebas y ejecución](#comandos-de-compilación-pruebas-y-ejecución)
    - [Compilar el backend](#compilar-el-backend)
    - [Ejecutar las pruebas](#ejecutar-las-pruebas)
  - [Análisis estático y reporte de cobertura con SonarQube](#análisis-estático-y-reporte-de-cobertura-con-sonarqube)
    - [Generar y publicar el análisis](#generar-y-publicar-el-análisis)
    - [Ejecutar la aplicación](#ejecutar-la-aplicación)
  - [Documentación](#documentación)
  - [Consideraciones generales](#consideraciones-generales)


## Requisitos del entorno

Para el desarrollo es necesario contar con las siguientes herramientas:

| Herramienta | Versión |
|---|---|
| Git | 2.45.1 o superior ([descargas oficiales](https://git-scm.com/downloads)) |
| GitHub | Servicio web ([sitio oficial](https://github.com/)); no requiere instalarse |
| GitHub Desktop | Opcional ([descarga oficial](https://github.com/apps/desktop)) |
| Java / JDK | 21 ([descargas oficiales de JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)) |
| Maven | 3.10.0 incluido en el wrapper ([descargas oficiales](https://maven.apache.org/download.cgi); no requiere instalación global) |
| Node.js y npm | Versiones compatibles con Vitest 2 ([descargas oficiales](https://nodejs.org/en/download)) |
| VS Code | 1.146 o superior ([descarga oficial](https://code.visualstudio.com/Download)) |
| PostgreSQL | 18 o superior ([descargas oficiales](https://www.postgresql.org/download/)) |
| Docker Engine | 29 o superior ([Docker Desktop para Windows](https://www.docker.com/products/docker-desktop/)) |
| SonarQube Community Build | Imagen Docker `sonarqube:community` ([descarga oficial](https://www.sonarsource.com/products/sonarqube/downloads/)) |

## Configuración del entorno

1. Instalar los requisitos indicados y clonar el repositorio.
2. Crear la base de datos PostgreSQL `cafesysto_db`.
3. Copiar [`.env-example`](./.env-example) como `.env` en la raíz del repositorio.
4. Configurar en `.env` al menos las variables `DB_USER` y `DB_PASSWORD`; también puede personalizarse `DB_URL` y las variables administrativas.
5. Instalar las dependencias Java con Maven Wrapper y las dependencias JavaScript con npm:

Ejecutar estos comandos desde la **raíz del repositorio** (el directorio que contiene [`DEVELOP.md`](./DEVELOP.md)):

```powershell
.\code\mvnw.cmd -f .\code\pom.xml dependency:resolve
npm.cmd --prefix .\code ci
```

Los scripts SQL de [`code/src/main/resources/sql](./code/src/main/resources/sql/)` son gestionados por Flyway al iniciar la aplicación. No es necesario ejecutarlos manualmente.

> No subir el archivo `.env` ni incluir credenciales reales en el repositorio.

## Dependencias del proyecto

### Backend Java

El backend usa Spring Boot 3.5.7 y Java 21. Las dependencias principales están declaradas en [`code/pom.xml`](./code/pom.xml):

| Dependencia | Ámbito | Uso |
|---|---|---|
| `spring-boot-starter-web` | Compilación y ejecución | API REST y servidor web |
| `spring-boot-starter-data-jpa` | Compilación y ejecución | Acceso a datos con JPA/Hibernate |
| `spring-boot-starter-validation` | Compilación y ejecución | Validación de datos de entrada |
| `spring-security-crypto` | Compilación y ejecución | Hash BCrypt de contraseñas; no activa Spring Security |
| `postgresql` | Ejecución | Controlador JDBC de PostgreSQL |
| `flyway-core` y `flyway-database-postgresql` | Compilación y ejecución | Migraciones del esquema de base de datos |
| `lombok` | Compilación | Reducción de código repetitivo en Java; dependencia opcional |
| `dotenv-java` 3.2.0 | Compilación y ejecución | Carga de variables desde `.env` |
| `spring-boot-starter-test` | Pruebas | Pruebas unitarias y de integración |

Las versiones de Spring Boot y sus dependencias gestionadas se heredan de `spring-boot-starter-parent` 3.5.7. Maven Wrapper proporciona Maven 3.10.0; no requiere una instalación global de Maven.

### Plugins de Maven

El [`pom.xml`](./code/pom.xml) configura los siguientes plugins:

| Plugin | Versión | Función y ejecución |
|---|---|---|
| `spring-boot-maven-plugin` | Gestionada por Spring Boot 3.5.7 | Ejecuta la aplicación con `spring-boot:run` y empaqueta el artefacto ejecutable. Excluye Lombok del paquete final. |
| `jacoco-maven-plugin` | 0.8.13 | Prepara el agente para recoger cobertura durante las pruebas y genera el informe en la fase `verify` (`target/site/jacoco`). |
| `exec-maven-plugin` | 3.5.0 | Ejecuta `npm ci` en la fase `initialize` y `npm run coverage` en la fase `test`. |

El `spring-boot-starter-parent` también gestiona los plugins estándar del ciclo de vida de Maven, como el compilador y el ejecutor de pruebas; no están declarados directamente en este [`pom.xml`](./code/pom.xml).

### Frontend y pruebas JavaScript

El contenido estático se encuentra en [`code/src/main/resources/static`](./code/src/main/resources/static/). Las pruebas JavaScript usan Vitest, jsdom y el proveedor de cobertura V8. Las versiones declaradas en [`code/package.json`](./code/package.json) son:

| Dependencia | Versión declarada | Uso |
|---|---|---|
| `vitest` | `^2.1.0` | Ejecución de pruebas |
| `jsdom` | `^25.0.0` | Entorno DOM simulado |
| `@vitest/coverage-v8` | `^2.1.0` | Reportes de cobertura |

Las versiones instaladas quedan fijadas en [`code/package-lock.json`](./code/package-lock.json); `npm ci` instala las versiones de ese archivo.

## Comandos de compilación, pruebas y ejecución

Ejecutar todos los siguientes comandos en PowerShell desde la **raíz del repositorio**. No es necesario cambiar a el directorio [`code`](./code/); las rutas de los comandos ya la especifican.

### Compilar el backend

```powershell
.\code\mvnw.cmd -f .\code\pom.xml clean package
```

Para omitir las pruebas Java:

```powershell
.\code\mvnw.cmd -f .\code\pom.xml clean package -DskipTests
```

`-DskipTests` no omite la ejecución JavaScript de cobertura configurada en `exec-maven-plugin`.

### Ejecutar las pruebas

Pruebas Java y, por la ejecución configurada en `exec-maven-plugin`, pruebas JavaScript con cobertura:

```powershell
.\code\mvnw.cmd -f .\code\pom.xml test
```

Para ejecutar solo las pruebas JavaScript sin generar cobertura:

```powershell
npm.cmd --prefix .\code test
```

Pruebas JavaScript en modo observado:

```powershell
npm.cmd --prefix .\code run test:watch
```

Cobertura JavaScript:

```powershell
npm.cmd --prefix .\code run coverage
```

Informe de cobertura Java con JaCoCo (también ejecuta las pruebas y la cobertura JavaScript):

```powershell
.\code\mvnw.cmd -f .\code\pom.xml verify
```

El informe HTML se genera en `code/target/site/jacoco/index.html`.

## Análisis estático y reporte de cobertura con SonarQube

Desde la raíz del repositorio, iniciar el contenedor:

```powershell
docker run -d --name sonarqube -p 9000:9000 -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:community
```

Esperar a que SonarQube esté disponible en `http://localhost:9000`. Completar la configuración inicial, crear el proyecto `Cafesysto` con la clave `cafesysto` y generar un token de análisis. En la misma sesión de PowerShell donde se ejecutará el análisis, asignar el token a una variable de entorno:

```powershell
$env:SONAR_TOKEN = "<token-generado-en-sonarqube>"
```

No guardar el token en el repositorio ni compartirlo.

### Generar y publicar el análisis

Desde la raíz del repositorio, cambiar al directorio [`code/`](./code/):

```powershell
Push-Location .\code
```

El flujo del reporte genera la cobertura JavaScript y luego ejecuta las pruebas, JaCoCo y el análisis de SonarQube:

```powershell
.\mvnw.cmd clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar `
  -Dsonar.projectKey=cafesysto `
  "-Dsonar.projectName=Cafesysto" `
  -Dsonar.host.url=http://localhost:9000 `
  "-Dsonar.token=$env:SONAR_TOKEN" `
  -Dsonar.sources=src/main `
  -Dsonar.tests=src/test `
  -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
```
Maven genera el reporte JaCoCo durante `verify`, y SonarQube recibe también el reporte JavaScript `coverage/lcov.info`.

Al finalizar, volver a la raíz del repositorio y consultar el proyecto en `http://localhost:9000`:

```powershell
Pop-Location
```

### Ejecutar la aplicación

Iniciar PostgreSQL y comprobar que las variables de `.env` sean válidas. Desde la raíz del repositorio:

```powershell
.\code\mvnw.cmd -f .\code\pom.xml spring-boot:run
```

La aplicación quedará disponible en `http://localhost:8080`.

## Documentación

Los cambios que afecten el funcionamiento, arquitectura o configuración del proyecto deben reflejarse en la documentación correspondiente.

Se deberá actualizar la documentación cuando:

- Se incorporen nuevas funcionalidades.
- Se modifiquen interfaces o contratos.
- Se altere la estructura del proyecto.
- Se agreguen o modifiquen dependencias.
- Se cambien los procedimientos de instalación o ejecución.

## Consideraciones generales

- Mantener las ramas actualizadas con respecto a su rama de origen.
- Evitar commits que incorporen cambios ajenos a la tarea asignada.
- No subir credenciales, contraseñas, tokens ni información sensible.
- Evitar incorporar dependencias innecesarias.
- Mantener el código organizado y modular.
- Comunicar oportunamente los problemas que impidan completar una tarea.