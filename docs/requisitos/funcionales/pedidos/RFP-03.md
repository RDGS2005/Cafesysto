# RFP-03 — Confirmar el pedido y recibir el código de retiro

## Descripción

**Como** cliente de la cafetería,  
**quiero** confirmar un pedido y obtener un código que lo identifique,
**para** realizar el pedido y poderlo retirar cuando esté listo.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] Al revisar el resumen, el pedido todavía no se registra.
- [ ] Al confirmar una selección válida, el pedido se registra en estado Pendiente.
- [ ] El cliente recibe el resumen del pedido y un código de retiro único.
- [ ] Si la selección está vacía, contiene una cantidad inválida o incluye un producto no disponible, se informa el motivo y el pedido no se registra.
- [ ] Si cambia el precio de un producto en el menú después de la confirmación, el pedido conserva el precio que se registró al confirmarlo.
- [ ] Después de reiniciar la aplicación, el pedido confirmado sigue disponible con su detalle, importe, código de retiro, fecha de registro y último estado registrado.

## Reglas de negocio relacionadas

- RN-NEG-01: Un pedido debe incluir al menos un producto y cada cantidad debe ser un número entero positivo. 
- RN-NEG-02: Solo los productos marcados como disponibles pueden incluirse en nuevos pedidos. 
- RN-NEG-04: El precio registrado para un producto en un pedido no cambia si posteriormente se modifica el precio del menú.
- RN-NEG-05: Cada pedido confirmado debe recibir un código de retiro único. 
- RN-NEG-06: Un pedido confirmado inicia en estado Pendiente y puede pasar a En preparación, Listo para retirar y Entregado, en ese orden. 

## Trazabilidad

- Requisito relacionado: RU-04
- Issue: #4
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

Se debe decidir si la aplicación permitirá editar la selección de productos en el pedido una vez el cliente se ha movido a la revisión del importe total, ya que no se menciona como un requisito pero podría aportar valor al usuario. 
