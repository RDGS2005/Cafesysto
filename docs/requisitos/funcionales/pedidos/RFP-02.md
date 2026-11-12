# RFP-02 — Conocer el importe total antes de confirmar el pedido

## Descripción

**Como** cliente de la cafetería,  
**quiero** conocer el importe total antes de confirmar el pedido,  
**para** decidir si el pedido se ajusta a mi presupuesto.

## Prioridad

Alta

## Versión

v1.1

## Criterios de aceptación

- [ ] Al revisar una selección válida, el cliente ve el importe total antes de confirmar el pedido.
- [ ] El importe mostrado es la suma del precio vigente de cada producto seleccionado multiplicado por su cantidad.

## Reglas de negocio relacionadas

- RN-NEG-01: Un pedido debe incluir al menos un producto y cada cantidad debe ser un número entero positivo.
- RN-NEG-03: El importe total de un pedido corresponde a la suma del precio vigente de cada producto multiplicado por su cantidad. 

## Trazabilidad

- Origen: RU-03
- Issue: #4
- Pull Request: #7
- Casos de prueba: Pendiente

## Observaciones

Ninguna.