# Cafesysto

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-green?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow?logo=javascript)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven)

Sistema web para la gestión de pedidos de una cafetería universitaria. Permite consultar el menú, registrar pedidos anticipados, generar códigos únicos de retiro y realizar el seguimiento de los pedidos desde su recepción hasta su entrega, facilitando la atención a clientes y la gestión del personal de cafetería.

## Entregables

* [Código fuente](.)

## Estructura del Proyecto

```text
Cafesysto/
├── .mvn/                                   # Propiedades de Maven
├── src/                                    # Código fuente y de pruebas automatizadas
│   ├── main/                               # Código fuente
│   │   ├── java                            # Código fuente del backend Springboot Java
│   │   └── resources                       # Código fuente del frontend y recursos
|   |       ├── sql                         # Scripts de migración de base de datos
|   |       ├── static                      # Código fuente del frontend HTML+CSS+JS
|   |       └── application.yml             # Configuración de la aplicación
|   └── test/                               # Código de pruebas automatizadas
|       ├── java/                           # Código de pruebas automatizadas del backend Springboot Java
|       └── js/                             # Código de pruebas automatizadas del frontend Javascript
├── .dockerignore                           # Configuración de Docker
├── .env-example                            # Ejemplo de archivo .env para variables de entorno
├── .gitattributes                          # Configuración de git nativa
├── .gitignore                              # Archivos y carpetas no incluidos (binarios, configuración de IDE, módulos de NodeJS, variables de entorno)
├── Dockerfile                              # Archivo para crear el contenedor Docker
├── docker-compose.yml                      # Configuración de Docker Compose
├── mvnw                                    # Comando de Maven Wrapper
├── mvnw.cmd                                # Comando de Maven Wrapper para Windows
├── package-lock.json                       # Versiones y árbol de dependencias de NodeJS
├── package.json                            # Dependencias de NodeJS
├── pom.xml                                 # Dependencias y Plugins de Maven
├── vitest.config.js                        # Configuración de pruebas de Javascript con Vitest
└── README.md                               # Documentación principal del proyecto
```