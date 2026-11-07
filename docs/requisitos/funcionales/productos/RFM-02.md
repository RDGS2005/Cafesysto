# RFM-02 — Registrar productos en el menú

## Descripción

**Como** personal de la cafetería,  
**quiero** registrar productos en el menú, sus precios y su disponibilidad,  
**para** que los clientes puedan verlos y agregarlos a sus pedidos.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema permite añadir el nombre, el precio y el estado de un producto.
- [ ] El precio de un producto deber ser un número mayor a 0.
- [ ] Un producto solo puede tener estado "Disponible" o "No disponible".
- [ ] Un producto nace con estado "Disponible" a menos que el usuario indique lo contrario.
- [ ] El sistema no permite registrar dos productos con el mismo nombre.
- [ ] Si el producto se ha registrado correctamente, el sistema muestra un mensaje de éxito y el producto aparece en el menú.

## Reglas de negocio relacionadas

- RN-NEG-02: El sistema debe exponer los productos y sus precios.
- Solo el personal de la cafetería puede registrar productos en el menú.
- Todo producto nuevo debe tener un precio asignado para poder ser guardado.
- Cada producto tiene un código único que no se puede modificar.

## Trazabilidad

- Requisito relacionado: RU-06
- Issue: #2
- Pull Request: pendiente
- Casos de prueba: Pendiente

## Observaciones