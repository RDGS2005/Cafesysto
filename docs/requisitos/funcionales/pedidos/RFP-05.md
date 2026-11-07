# RFP-05 — Consultar los pedidos y registrar sus avances

## Descripción

**Como** personal de la cafetería,  
**quiero** consultar los pedidos y registrar los avances hasta la entrega,
**para** organizar la preparación y entregar cada pedido a quien corresponde.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El personal puede consultar los pedidos e identificar cuáles están Pendientes.
- [ ] El personal puede tomar un pedido Pendiente y colocarlo a En preparación.
- [ ] El personal puede tomar un pedido En preparación y colocarlo en Listo para retirar.
- [ ] El personal puede tomar un pedido Listo para retirar y colocarlo en Entregado solo si el código corresponde al pedido que se está entregando.
- [ ] Si el personal intenta saltar una etapa o volver a un estado anterior, el pedido conserva su estado actual.

## Reglas de negocio relacionadas

- RN-NEG-06: Un pedido confirmado inicia en estado Pendiente y puede pasar a En preparación, Listo para retirar y Entregado, en ese orden. 
- RN-NEG-07: Un pedido solo puede marcarse como Entregado si previamente estaba Listo para retirar y el cliente 
presenta el código correspondiente. 

## Trazabilidad

- Requisito relacionado: RU-07
- Issue: #4
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

Ninguna.