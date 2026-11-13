# Requisitos

Versión: v1.0
Autor: Equipo 6

El presente documento sirve como un índice de los requisitos del sistema informático para la gestión de pedidos de la cafetería universitaria. Su propósito es exponer las necesidades del negocio, las funcionalidades del sistema y las condiciones que debe cumplir para facilitar la consulta del menú, el registro de pedidos anticipados y el seguimiento de estos hasta su entrega.

## Tabla de Contenidos

- [Requisitos](#requisitos)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Contexto](#contexto)
  - [Glosario](#glosario)
  - [Requisitos de Negocio](#requisitos-de-negocio)
  - [Perfiles de Usuario](#perfiles-de-usuario)
    - [Clientes](#clientes)
      - [Origen](#origen)
      - [Funciones](#funciones)
    - [Personal](#personal)
      - [Origen](#origen-1)
      - [Funciones](#funciones-1)
    - [Administrador](#administrador)
      - [Origen](#origen-2)
      - [Funciones](#funciones-2)
  - [Requisitos de Usuario](#requisitos-de-usuario)
  - [Reglas de Negocio](#reglas-de-negocio)
  - [Procesos de Negocio](#procesos-de-negocio)
    - [PN-01 — Registro de un Pedido](#pn-01--registro-de-un-pedido)
    - [PN-02 — Preparación y Entrega de un Pedido](#pn-02--preparación-y-entrega-de-un-pedido)
  - [Alcance y Restricciones](#alcance-y-restricciones)
  - [Requisitos Funcionales](#requisitos-funcionales)
    - [Módulo de Usuarios](#módulo-de-usuarios)
    - [Módulo de Productos](#módulo-de-productos)
    - [Módulo de Pedidos](#módulo-de-pedidos)
  - [Requisitos No Funcionales](#requisitos-no-funcionales)
  - [Identificación y Versionamiento de Requisitos](#identificación-y-versionamiento-de-requisitos)
  - [Matriz de Trazabilidad](#matriz-de-trazabilidad)

## Contexto

La cafetería de una institución de educación superior atiende a estudiantes y docentes durante los recesos de clase. Actualmente, los clientes deben acercarse al mostrador para conocer el menú y solicitar sus alimentos. En horas de mayor demanda se forman filas y el personal tiene dificultades para distinguir los pedidos pendientes, los que están en preparación y los listos para retirar. La cafetería desea una solución informática para consultar el menú, registrar pedidos anticipados y facilitar su retiro mediante un código.

## Glosario

* **Cliente:** Estudiante o docente que consulta el menú, registra pedidos y conoce cuándo puede retirarlos. Se identifica mediante un código y datos (nombre, apellido, cédula, celular y correo institucional).
* **Personal:** Trabajador de la cafetería que mantiene el menú y procesa los pedidos recibidos, registrando los cambios de estado y las entregas.
* **Menú:** Conjunto de productos que la cafetería ofrece a los clientes.
* **Producto:** Artículo ofrecido en el menú, identificado mediante código, nombre, precio y disponibilidad. Pueden ofrecerse alimentos y bebidas.
* **Producto disponible:** Producto habilitado para incorporarse a nuevos pedidos.
* **Pedido:** Solicitud confirmada por un cliente, con un código de retiro, estado, fecha de registro e importe total.
* **Estado de pedido:** Etapa de atención del pedido según las reglas de la cafetería. Puede ser Pendiente, En preparación, Listo para retirar o Entregado.
* **Pedido confirmado:** Solicitud registrada con un código de retiro y estado Pendiente.
* **Código de retiro:** Identificador único que permite localizar y entregar un pedido.
* **Detalle del pedido:** Producto incluido en un pedido, su cantidad y el precio considerado en la operación.
* **Administrador:** Persona encargada de gestionar el sistema informático, registra al personal.

## Requisitos de Negocio

* RN-01 — Reducir el tiempo empleado en registrar los pedidos durante períodos de alta demanda.
* RN-02 — Facilitar que los clientes conozcan los productos ofrecidos y sus precios antes de solicitar un pedido.
* RN-03 — Mejorar el seguimiento de los pedidos desde su recepción hasta la entrega. 
* RN-04 — Reducir confusiones al entregar pedidos a los clientes.

## Perfiles de Usuario

### Clientes

Estudiantes o docentes de la universidad.

#### Origen

Especificación inicial de necesidades del cliente.

#### Funciones

* Consultar el menú.
* Registrar pedidos.
* Conocer cuándo puede retirar sus pedidos. 

### Personal

Trabajadores de la cafetería universitaria.

#### Origen

Especificación inicial de necesidades del cliente.

#### Funciones

* Mantener el menú .
* Procesar los pedidos recibidos.
* Registrar los cambios de estado y las entregas de los pedidos.

### Administrador

Persona encargada de gestionar el sistema informático.

#### Origen

Equipo 6.

#### Funciones

* Registrar personal.

## Requisitos de Usuario

* RU-01 — El cliente necesita consultar el menú de la cafetería, con los productos disponibles y sus precios.
* RU-02 — El cliente necesita seleccionar uno o más productos y las cantidades que desea pedir.
* RU-03 — El cliente necesita conocer el importe total antes de confirmar su pedido.
* RU-04 — El cliente necesita confirmar un pedido y obtener un código que permita identificarlo al momento del retiro.
* RU-05 — El cliente necesita consultar el estado de su pedido.
* RU-06 — El personal necesita registrar o actualizar los productos del menú, sus precios y su disponibilidad.
* RU-07 — El personal necesita consultar los pedidos y registrar sus avances hasta la entrega.

## Reglas de Negocio

* RN-NEG-01 — Un pedido debe incluir al menos un producto y cada cantidad debe ser un número entero positivo.
* RN-NEG-02 — Solo los productos marcados como disponibles pueden incluirse en nuevos pedidos.
* RN-NEG-03 — El importe total de un pedido corresponde a la suma del precio vigente de cada producto multiplicado por su cantidad.
* RN-NEG-04 — El precio registrado para un producto en un pedido no cambia si posteriormente se modifica el precio del menú.
* RN-NEG-05 — Cada pedido confirmado debe recibir un código de retiro único.
* RN-NEG-06 — Un pedido confirmado inicia en estado Pendiente y puede pasar a En preparación, Listo para retirar y Entregado, en ese orden.
* RN-NEG-07 — Un pedido solo puede marcarse como Entregado si previamente estaba Listo para retirar y el cliente presenta el código correspondiente.

## Procesos de Negocio

### PN-01 — Registro de un Pedido

Propósito: Registrar los productos solicitados por un cliente y generar la información necesaria para su seguimiento y retiro.

1. El cliente consulta el menú y sus precios.
2. Selecciona productos y cantidades.
3. La cafetería verifica que los productos estén disponibles y calcula el importe.
4. El cliente revisa la selección y confirma el pedido.
5. Se registra el pedido con estado Pendiente y se genera un código único de retiro.
6. El cliente recibe el resumen y el código del pedido.
7. Si la selección no cumple las reglas, se informa el motivo y el pedido no se confirma.

### PN-02 — Preparación y Entrega de un Pedido

1. El personal consulta los pedidos Pendientes.
2. Selecciona un pedido para iniciar su preparación y registra el cambio de estado.
3. Cuando termina la preparación, marca el pedido como Listo para retirar.
4. El cliente presenta su código de retiro.
5. El personal verifica el código y registra el pedido como Entregado.

## Alcance y Restricciones

* La información de productos y pedidos debe persistir entre ejecuciones.
* El Release 1.0 no incluye pago electrónico, cobro en línea ni integración con servicios financieros. El pago podrá realizarse presencialmente fuera de la aplicación.
* No se requieren notificaciones por correo, mensajería ni integración con impresoras o lectores de códigos. El código puede mostrarse en pantalla.
* No se requieren promociones, reembolsos, pedidos programados ni gestión de inventario de ingredientes.
* El sistema debe permitir demostrar los procesos y las reglas de negocio indicadas en el documento.

## Requisitos Funcionales

### Módulo de Usuarios

El propósito de este módulo es gestionar a los usuarios del sistema, tanto clientes como personal, permitiéndoles su registro e ingreso al sistema para realizar sus funciones.

* [RFU-01 v1.1 — Registrar usuario Cliente](./funcionales/usuarios/RFU-01.md)
* [RFU-02 v2.0 — Ingreso al sistema con usuario Cliente](./funcionales/usuarios/RFU-02.md)
* [RFU-04 v2.0 — Ingreso al sistema con usuario Personal](./funcionales/usuarios/RFU-04.md)
* [RFU-05 v1.0 — Registrar Personal](./funcionales/usuarios/RFU-05.md)
* [RFU-06 v1.0 — Actualización de credenciales de Administrador](./funcionales/usuarios/RFU-06.md)

### Módulo de Productos

El propósito de este módulo es gestionar los productos del menú, permitir el registro y consulta de productos, además de la gestión de su disponibilidad y sus precios.

* [RFM-01 v1.1 — Consultar menú de la cafetería](./funcionales/productos/RFM-01.md)
* [RFM-02 v1.0 — Registrar productos en el menú](./funcionales/productos/RFM-02.md)
* [RFM-03-1 v1.1 — Actualizar el precio de los productos del menú](./funcionales/productos/RFM-03-1.md)
* [RFM-03-2 v1.1 — Actualizar la disponibilidad de los productos del menú](./funcionales/productos/RFM-03-2.md)

### Módulo de Pedidos

El propósito de este módulo es gestionar los pedidos, permitir el registro de pedidos y el seguimiento de los mismos incluyendo el importe total y el estado.

* [RFP-01 v1.0 — Seleccionar productos y cantidades](./funcionales/pedidos/RFP-01.md)
* [RFP-02 v1.1 — Conocer el importe total antes de confirmar el pedido](./funcionales/pedidos/RFP-02.md)
* [RFP-03 v1.1 — Confirmar el pedido y recibir el código de retiro](./funcionales/pedidos/RFP-03.md)
* [RFP-04 v1.0 — Consultar el estado del pedido](./funcionales/pedidos/RFP-04.md)
* [RFP-05-1 v1.1 — Consultar los pedidos](./funcionales/pedidos/RFP-05-1.md)
* [RFP-05-2 v1.1 — Registrar avances de pedidos](./funcionales/pedidos/RFP-05-2.md)

## Requisitos No Funcionales

* [RNF-01 v1.0 — Tiempo de respuesta del menú](./nofuncionales/RNF-01.md)
* [RNF-02 v1.0 — Tiempo de respuesta de los pedidos](./nofuncionales/RNF-02.md)
* [RNF-03 v1.0 — Acceso desde computadoras de escritorio](./nofuncionales/RNF-03.md)
* [RNF-04 v1.0 — Acceso desde dispositivos móviles](./nofuncionales/RNF-04.md)
* [RNF-05 v1.0 — Modularidad](./nofuncionales/RNF-05.md)
* [RNF-06 v1.0 — Cobertura de pruebas](./nofuncionales/RNF-06.md)

## Identificación y Versionamiento de Requisitos

1. Los requisitos referentes al módulo de usuarios tienen el código RFU-\#\#, los referentes al módulo de productos tienen el código RFM-\#\#, los referentes al módulo de pedidos tienen el código RFP-\#\# y los no funcionales tienen el código RNF-\#\#.
2. Si un requisito propuesto se elimina, su identificador no puede ser utilizado por nuevos requisitos.
3. Si un requisito propuesto RX-## se divide en n requisitos, estos toman los identificadores RX-\#\#-1, RX-\#\#-2, ..., RX-\#\#-n.
4. Todo nuevo requisito empieza con versión v1.0.
5. Si un requisito con versión vx.y sufre una modificación leve de contenido, su versión cambia a vx.[y + 1], por ejemplo de v1.0 a v1.1.
6. Si un requisito con versión vx.y sufre una modificación completa de contenido, su versión cambia a v[x + 1].y, por ejemplo de v1.0 a v2.0.

## Matriz de Trazabilidad

| Requisito | Versión | Módulo | Origen | \#Issue | \#Pull Request | Caso de Prueba |
| --- | --- | --- | --- | --- | --- | --- |
| RFU-01 | v1.1 | Módulo de Usuarios | Equipo 6 | 1 | 9 | Pendiente |
| RFU-02 | v2.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | Pendiente |
| RFU-04 | v2.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | Pendiente |
| RFU-05 | v1.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | Pendiente |
| RFU-06 | v1.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | Pendiente |
| RFM-01 | v1.1 | Módulo de Productos | RU-01 | 2 | 8 | Pendiente |
| RFM-02 | v1.0 | Módulo de Productos | RU-06 | 2 | 8 | Pendiente |
| RFM-03-1 | v1.1 | Módulo de Productos | RU-06 | 2 | 8 | Pendiente |
| RFM-03-2 | v1.1 | Módulo de Productos | RU-06 | 2 | 8 | Pendiente |
| RFP-01 | v1.0 | Módulo de Pedidos | RU-02 | 4 | 7 | Pendiente |
| RFP-02 | v1.1 | Módulo de Pedidos | RU-03 | 4 | 7 | Pendiente |
| RFP-03 | v1.1 | Módulo de Pedidos | RU-04 | 4 | 7 | Pendiente |
| RFP-04 | v1.0 | Módulo de Pedidos | RU-05 | 4 | 7 | Pendiente |
| RFP-05-1 | v1.1 | Módulo de Pedidos | RU-07 | 4 | 7 | Pendiente |
| RFP-05-2 | v1.1 | Módulo de Pedidos | RU-07 | 4 | 7 | Pendiente |
| RNF-01 | v1.0 | Módulo de Productos | Equipo 6 | 3 | 6 | Pendiente |
| RNF-02 | v1.0 | Módulo de Pedidos | Equipo 6 | 3 | 6 | Pendiente |
| RNF-03 | v1.0 | Todos | Equipo 6 | 3 | 6 | Pendiente |
| RNF-04 | v1.0 | Todos | Equipo 6 | 3 | 6 | Pendiente |
| RNF-05 | v1.0 | Todos | Equipo 6 | 3 | 6 | Pendiente |
| RNF-06 | v1.0 | Todos | Equipo 6 | 3 | 6 | Pendiente |