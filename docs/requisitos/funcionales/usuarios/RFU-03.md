# RFU-03 — Acceder a las funciones del personal de cafetería

## Descripción

**Como** integrante del personal de cafetería,
**quiero** acceder a las funciones correspondientes a mi perfil,
**para** mantener el menú y gestionar los pedidos hasta su entrega.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] Cuando el sistema reconoce a una persona habilitada como personal de cafetería, permite acceder a las funciones de gestión del menú y de pedidos.
- [ ] Un cliente no puede registrar productos ni modificar sus precios o disponibilidad.
- [ ] Un cliente no puede modificar el estado de los pedidos ni registrar su entrega.
- [ ] Si una persona sin autorización intenta ejecutar una operación del personal, el sistema rechaza la operación e informa la restricción sin modificar los datos.
- [ ] La autorización se verifica al ejecutar la operación, incluso si se intenta acceder directamente a ella sin utilizar el menú de navegación.
- [ ] El acceso del personal mantiene las reglas de negocio aplicables a los productos y pedidos; el perfil no permite omitirlas.

## Reglas de negocio relacionadas

- Especificación del cliente, sección 3: el personal mantiene el menú y procesa los pedidos, registrando cambios de estado y entregas.
- RU-06: el personal registra o actualiza productos, precios y disponibilidad.
- RU-07: el personal consulta los pedidos y registra sus avances hasta la entrega.
- RN-NEG-06 y RN-NEG-07: los cambios de estado respetan la secuencia prevista y la entrega requiere verificar el código de retiro.
- Decisión de análisis propuesta: las operaciones de gestión se reservan al personal autorizado y no al perfil de cliente.

## Trazabilidad

- Requisito relacionado: RU-06, RU-07 y sección 3 (Usuarios involucrados).
- Issue: #1
- Pull Request: pendiente
- Casos de prueba: Pendiente

## Observaciones

Esta historia establece la separación de permisos entre clientes y personal. Los procedimientos para registrar productos y procesar pedidos corresponden a sus respectivos módulos. Antes de implementar se debe acordar cómo se identifica al personal, quién habilita su acceso y cómo se verifica su autorización. No se incorpora un rol de administrador ni un proceso de autorregistro del personal, porque la especificación no los define.
