# RFP-01 — Seleccionar productos y cantidades

## Descripción

**Como** cliente de la cafetería,  
**quiero** seleccionar los productos que deseo adquirir y sus cantidades,  
**para** poder realizar un pedido.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] Al seleccionar un producto disponible, este aparece en la selección del cliente.
- [ ] Si un producto está marcado como no disponible, el cliente no puede añadirlo a la selección.
- [ ] Al indicar la cantidad de un producto seleccionado, el sistema la muestra junto a ese producto.
- [ ] Al añadir otro producto, se conservan los productos ya seleccionados y sus cantidades.


## Reglas de negocio relacionadas

- RN-NEG-01: Un pedido debe incluir al menos un producto y cada cantidad debe ser un número entero positivo.
- RN-NEG-02: Solo los productos marcados como disponibles pueden incluirse en nuevos pedidos. 

## Trazabilidad

- Origen: RU-02
- Issue: #4
- Pull Request: #7
- Casos de prueba: Pendiente

## Observaciones

Ninguna.