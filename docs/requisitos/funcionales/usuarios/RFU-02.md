# RFU-02 — Identificar al cliente para acceder a sus pedidos

## Descripción

**Como** cliente registrado de la cafetería,
**quiero** que el sistema reconozca mi identidad al acceder a mis pedidos,
**para** registrar y consultar los pedidos asociados a mi código de cliente.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] Cuando la identificación es válida, el sistema reconoce el código y el nombre del cliente registrado.
- [ ] Si no se puede identificar al cliente, el sistema informa que no fue posible completar el acceso y no muestra pedidos personales.
- [ ] Al confirmar un pedido, el módulo de usuarios proporciona al módulo de pedidos el código del cliente identificado para establecer la asociación.
- [ ] La consulta de pedidos personales utiliza el código del cliente identificado y no muestra pedidos asociados a otros clientes.
- [ ] Si el cliente identificado todavía no tiene pedidos, se informa esta situación sin mostrarla como un error.
- [ ] El código de cliente no sustituye al código de retiro requerido para entregar un pedido.

## Reglas de negocio relacionadas

- Especificación del cliente, sección 7: los datos y el código del cliente permiten consultar sus pedidos.
- RN-NEG-05: cada pedido confirmado recibe un código de retiro único.
- RN-NEG-07: la entrega exige que el pedido esté Listo para retirar y que se presente el código correspondiente.
- Decisión de análisis propuesta: la consulta personal se limita a los pedidos del cliente identificado.

## Trazabilidad

- Requisito relacionado: RU-04, RU-05 y sección 7 (Cliente).
- Issue: #1
- Pull Request: pendiente
- Casos de prueba: Pendiente

## Observaciones

Depende del registro de cliente descrito en RFU-01. Esta historia define la identificación y la asociación del cliente; la confirmación, consulta de estados y entrega se detallan en el módulo de pedidos. El mecanismo para verificar la identidad está pendiente de acuerdo antes de implementar: conocer un código de cliente no se considera por sí solo prueba de identidad. La especificación no exige contraseñas, recuperación de acceso ni autenticación con servicios externos.
