# RFU-03 — Registrar cuenta Personal

## Descripción

**Como** integrante del personal de cafetería,
**quiero** registrar mi cuenta con mis datos laborales,
**para** identificarme en el sistema.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema solicita nombre, apellido, cédula, celular y correo institucional del trabajador.
- [ ] Todos los campos son obligatorios; si falta alguno, el registro no se completa y se indica el campo faltante.
- [ ] El sistema valida el formato de la cédula, del celular y del correo institucional.
- [ ] No se permite registrar una cédula o un correo institucional que ya existan en el sistema; se informa el motivo.
- [ ] Al completar el registro, el sistema genera automáticamente un código de personal único.
- [ ] La cuenta registrada queda identificada con el rol Personal, distinto del rol Cliente.
- [ ] Los datos del personal se almacenan de forma persistente y siguen disponibles entre ejecuciones.
- [ ] El sistema muestra un mensaje de confirmación al finalizar el registro exitoso.
- [ ] Si los datos no cumplen las validaciones, el sistema informa el motivo y no crea la cuenta.

## Reglas de negocio relacionadas
 
- RN-NEG-06 y RN-NEG-07: los cambios de estado respetan la secuencia prevista y la entrega requiere verificar el código de retiro.
- Las operaciones de gestión se reservan al personal autorizado y no al perfil de cliente.
- El personal mantiene el menú y procesa los pedidos, registrando cambios de estado y entregas.

## Trazabilidad

- Requisito relacionado: RU-06, RU-07.
- Issue: #1
- Pull Request: pendiente
- Casos de prueba: Pendiente

## Observaciones

Esta historia establece la separación de permisos entre clientes y personal. Los procedimientos para registrar productos y procesar pedidos corresponden a sus respectivos módulos. 
No se incorpora un rol de administrador porque la especificación no los define.