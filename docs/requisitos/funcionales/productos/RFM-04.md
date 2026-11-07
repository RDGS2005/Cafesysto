# RFM-04 — Retirar productos del menú

## Descripción

**Como** personal de la cafetería,  
**quiero** retirar productos del menú,  
**para** corregir el menú ante errores de registro de productos.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema permite seleccionar un producto existente del menú para su retiro.
- [ ] El personal no puede retirar un producto si hay pedidos asociados a él.
- [ ] Si el producto se ha retirado correctamente, el sistema muestra un mensaje de éxito y el producto ya no puede ser consultado desde el menú que ve el cliente.

## Reglas de negocio relacionadas

- Retirar un producto no se puede hacer si es que un pedido está asociado a él, sin importar la fase del pedido.

## Trazabilidad

- Requisito relacionado: N/A (decisión técnica)
- Issue: #2
- Pull Request: pendiente
- Casos de prueba: Pendiente

## Observaciones

El retiro de productos solo debería hacerse cuando el registro fue incorrecto (como cuando hay erratas en el nombre del producto). En cambio, si se quiere reflejar cambios de inventario, hay que referirse a RFM-03-2.