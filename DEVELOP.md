# Ambiente de Desarrollo

Versión: v1.2
Autor: Equipo 6

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
    - [Frontend y pruebas JavaScript](#frontend-y-pruebas-javascript)
  - [Comandos de compilación, pruebas y ejecución](#comandos-de-compilación-pruebas-y-ejecución)
    - [Compilar el backend](#compilar-el-backend)
    - [Ejecutar las pruebas](#ejecutar-las-pruebas)
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

## Configuración del entorno

1. Instalar los requisitos indicados y clonar el repositorio.
2. Crear la base de datos PostgreSQL `cafesysto_db`.
3. Copiar `.env-example` como `.env` en la raíz del repositorio.
4. Configurar en `.env` al menos las variables `DB_USER` y `DB_PASSWORD`; también puede personalizarse `DB_URL` y las variables administrativas.
5. Instalar las dependencias Java con Maven Wrapper y las dependencias JavaScript con npm:

Ejecutar estos comandos desde la **raíz del repositorio** (la carpeta que contiene `DEVELOP.md`):

```powershell
.\code\mvnw.cmd -f .\code\pom.xml dependency:resolve
npm.cmd --prefix .\code ci
```

Los scripts SQL de `code/src/main/resources/sql` son gestionados por Flyway al iniciar la aplicación. No es necesario ejecutarlos manualmente.

> No subir el archivo `.env` ni incluir credenciales reales en el repositorio.

## Dependencias del proyecto

### Backend Java

El backend usa Spring Boot 3.5.7 y Java 21. Las dependencias principales están declaradas en `code/pom.xml`:

| Dependencia | Uso |
|---|---|
| `spring-boot-starter-web` | API REST y servidor web |
| `spring-boot-starter-data-jpa` | Acceso a datos con JPA/Hibernate |
| `spring-boot-starter-validation` | Validación de datos de entrada |
| `spring-security-crypto` | Hash BCrypt de contraseñas |
| `postgresql` | Controlador JDBC de PostgreSQL |
| `flyway-core` y `flyway-database-postgresql` | Migraciones del esquema de base de datos |
| `lombok` | Reducción de código repetitivo en Java |
| `dotenv-java` | Carga de variables desde `.env` |
| `spring-boot-starter-test` | Pruebas unitarias y de integración |

Maven Wrapper proporciona Maven 3.10.0; no requiere una instalación global de Maven.

### Frontend y pruebas JavaScript

El contenido estático se encuentra en `code/src/main/resources/static`. Las pruebas JavaScript usan Vitest 2.1.0 y jsdom 25.0.0. Las herramientas de desarrollo están declaradas en `code/package.json`:

| Dependencia | Uso |
|---|---|
| `vitest` | Ejecución de pruebas |
| `jsdom` | Entorno DOM simulado |
| `@vitest/coverage-v8` | Reportes de cobertura |

## Comandos de compilación, pruebas y ejecución

Ejecutar todos los siguientes comandos en PowerShell desde la **raíz del repositorio**. No es necesario cambiar a la carpeta `code`; las rutas de los comandos ya la especifican.

### Compilar el backend

```powershell
.\code\mvnw.cmd -f .\code\pom.xml clean package
```

Para compilar sin ejecutar las pruebas:

```powershell
.\code\mvnw.cmd -f .\code\pom.xml clean package -DskipTests
```

### Ejecutar las pruebas

Todas las pruebas Java:

```powershell
.\code\mvnw.cmd -f .\code\pom.xml test
```

Todas las pruebas JavaScript:

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