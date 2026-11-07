# RFP-02 — Conocer el importe total antes de confirmar el pedido

## Descripción

**Como** cliente de la cafetería,  
**quiero** conocer el importe total antes de confirmar el pedido,  
**para** decidir si el pedido se ajusta a mi presupuesto.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] Una vez comprobada la disponibilidad de los productos seleccionados, el cliente ve el importe total antes de confirmar el pedido.
- [ ] El importe mostrado es la suma del precio vigente de cada producto seleccionado multiplicado por su cantidad.

## Reglas de negocio relacionadas

- RN-NEG-01: Un pedido debe incluir al menos un producto y cada cantidad debe ser un número entero positivo.
- RN-NEG-02: Solo los productos marcados como disponibles pueden incluirse en nuevos pedidos.
- RN-NEG-03: El importe total de un pedido corresponde a la suma del precio vigente de cada producto multiplicado por su cantidad. 

## Trazabilidad

- Requisito relacionado: RU-03
- Issue: #4
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

Ninguna.