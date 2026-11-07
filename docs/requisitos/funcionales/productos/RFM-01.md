# RFM-01 — Consultar menú de la cafetería

## Descripción

**Como** cliente de la cafetería,  
**quiero** consultar los productos disponibles y sus precios,  
**para** conocer las opciones antes de realizar un pedido.

## Prioridad

Alta

## Versión

v1.1

## Criterios de aceptación

- [ ] El sistema muestra todos los productos marcados como "Disponible" primero.
- [ ] Cada producto muestra su nombre y su precio.
- [ ] Los productos no disponibles se muestran después de los productos disponibles con indicadores claros.
- [ ] El menú muestra información actualizada.

## Reglas de negocio relacionadas

- RN-NEG-02: Solo los productos marcados como disponibles pueden incluirse en nuevos pedidos.
- Los productos no disponibles no pueden agregarse a nuevos pedidos.

## Trazabilidad

- Requisito relacionado: RU-01
- Issue: #2
- Pull Request: pendiente
- Casos de prueba: Pendiente

## Observaciones

Las reglas de negocio incluidas en el análisis se relacionan con el requisito porque el mismo requisito es un tipo de precondición para que las reglas se cumplan. Por ejemplo, para que el cliente pueda incluir un producto disponible en un pedido (RN-NEG-02), es necesario que sepa cuáles productos son, efectivamente, disponibles, y eso lo hace consultando el menú.