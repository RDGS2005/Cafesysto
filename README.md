# Cafesysto

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-green?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow?logo=javascript)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven)

Sistema web para la gestión de pedidos de una cafetería universitaria. Permite consultar el menú, registrar pedidos anticipados, generar códigos únicos de retiro y realizar el seguimiento de los pedidos desde su recepción hasta su entrega, facilitando la atención a clientes y la gestión del personal de cafetería.

## Entregables

* [Código fuente](./code/)
* [Documentación de Requisitos](./docs/requisitos/)
* [Documentación de Diseño](./docs/modelado/)
* [Documentación de Ambiente](./DEVELOP.md)
* [Documentación de Flujo de Trabajo](./CONTRIBUTING.md)
* [Documentación Técnica](./docs/implementacion/)
* [Reporte de Análisis Estático de Código](./docs/pruebas/sonarqube/REPORTE_SONARQUBE.md)
* [Documentación de Casos de Prueba](./docs/pruebas/)
* [Manual de Instalación](./docs/despliegue/MANUAL_INSTALACION.md)
* [Manual de Usuario](./docs/despliegue/MANUAL_USUARIO.md)

## Estructura del Proyecto

```text
Cafesysto/
│
├── code/                                       # Aplicación y pruebas
│   ├── .mvn/                                   # Propiedades de Maven
│   ├── src/                                    # Código fuente y de pruebas automatizadas
│   │   ├── main/                               # Código fuente
│   │   │   ├── java                            # Código fuente del backend Springboot Java
│   │   │   └── resources                       # Código fuente del frontend y recursos
|   |   |       ├── sql                         # Scripts de migración de base de datos
|   |   |       ├── static                      # Código fuente del frontend HTML+CSS+JS
|   |   |       └── application.yml             # Configuración de la aplicación
|   |   └── test/                               # Código de pruebas automatizadas
|   |       ├── java/                           # Código de pruebas automatizadas del backend Springboot Java
|   |       └── js/                             # Código de pruebas automatizadas del frontend Javascript
|   ├── .dockerignore                           # Configuración de Docker
|   ├── .env-example                            # Ejemplo de archivo .env para variables de entorno
|   ├── Dockerfile                              # Archivo para crear el contenedor Docker
|   ├── docker-compose.yml                      # Configuración de Docker Compose
|   ├── mvnw                                    # Comando de Maven Wrapper
|   ├── mvnw.cmd                                # Comando de Maven Wrapper para Windows
|   ├── package-lock.json                       # Versiones y árbol de dependencias de NodeJS
|   ├── package.json                            # Dependencias de NodeJS
    |   ├── pom.xml                             # Dependencias y Plugins de Maven
|   └── vitest.config.js                        # Configuración de pruebas de Javascript con Vitest
├── docs/                                       # Documentación del proyecto
|   ├── despliegue/                             # Documentación de instalación y operación
|   |   ├── img/                                # Recursos de imagen para los manuales
|   |   ├── MANUAL_USUARIO.md                   # Manual de usuario
|   |   └── MANUAL_INSTALACIÓN.md               # Manual de instalación
|   ├── implementación/                         # Documentación técnica de código
|   |   └── javadoc/                            # Documentación técnica Javadoc
|   ├── modelado/                               # Documentación de diseño
|   |   ├── pedidos/                            # Documentación de diseño del módulo de pedidos
|   |   ├── productos/                          # Documentación de diseño del módulo de productos
|   |   ├── ui/                                 # Documentación de diseño de la interfaz de usuario
|   |   ├── usuarios/                           # Documentación de diseño del módulo de usuarios
|   |   ├── DIAGRAMA_CLASES.md                  # Diagrama de clases general
|   |   └── EJEMPLO_DIAGRAMA_CLASES.md          # Plantilla para diagramas de clases
|   ├── pruebas/                                # Documentación de pruebas
|   |   ├── nofuncionales/                      # Casos de prueba para requisitos no funcionales
|   |   ├── pedidos/                            # Casos de prueba del módulo de pedidos
|   |   ├── productos/                          # Casos de prueba del módulo de productos
|   |   ├── sonarqube/                          # Análisis estático de Sonarqube
|   |   └── usuarios/                           # Casos de prueba del módulo de usuarios
|   ├── requisitos/                             # Documentación de requisitos
|   |   ├── funcionales/                        # Requisitos funcionales
|   |   |   ├── pedidos/                        # Historias de usuario del módulo de pedidos
|   |   |   ├── productos/                      # Historias de usuario del módulo de productos
|   |   |   └── usuarios/                       # Historias de usuario del módulo de usuarios
|   |   ├── nofuncionales/                      # Requisitos no funcionales
|   |   ├── EJEMPLO_HISTORIA_DE_USUARIO.md      # Plantilla para historias de usuario
|   |   ├── EJEMPLO_REQUISITO_NO_FUNCIONAL.md   # Plantilla para requisitos no funcionales
|   |   └── REQUISITOS.md                       # Plantilla para diagramas de clases
|   └── MATRIZ_TRAZABILIDAD.md                  # Matriz de trazabilidad
├── .gitattributes                              # Configuración de git nativa
├── .gitignore                                  # Archivos y carpetas no incluidos (binarios, configuración de IDE, módulos de NodeJS, variables de entorno)
├── CONTRIBUTING.md                             # Documentación de flujo de trabajo
├── DEVELOP.md                                  # Documentación de ambiente
├── PULL_REQUEST_TEMPLATE.md                    # Plantilla para Pull Requests
└── README.md                                   # Documentación principal del proyecto
```