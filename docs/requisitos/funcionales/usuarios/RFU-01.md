# RFU-01 — Registrar cuenta Cliente

## Descripción

**Como** cliente de la cafetería (estudiante o docente),  
**quiero** registrar mi cuenta con mis datos personales,  
**para** identificarme en el sistema.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema solicita nombre, apellido, cédula, celular y correo institucional.
- [ ] Todos los campos son obligatorios; si falta alguno, el registro no se completa y se indica el campo faltante.
- [ ] El sistema valida el formato de la cédula, del celular y del correo institucional.
- [ ] No se permite registrar una cédula o un correo institucional que ya existan en el sistema; se informa el motivo.
- [ ] Al completar el registro, el sistema genera automáticamente un código de cliente único.
- [ ] Los datos del cliente se almacenan de forma persistente y siguen disponibles entre ejecuciones.
- [ ] El sistema muestra un mensaje de confirmación al finalizar el registro exitoso.
- [ ] Si los datos no cumplen las validaciones, el sistema informa el motivo y no crea la cuenta.

## Reglas de negocio relacionadas

- RN-NEG-05 y RN-NEG-07: el código de retiro identifica un pedido confirmado; es distinto del código que identifica al cliente.
- El cliente se identifica mediante un código y sus datos (nombre, apellido, cédula, celular y correo institucional).
- La información debe persistir entre ejecuciones.
- Cada cliente debe poder identificarse de forma única en el sistema.

## Trazabilidad

- Requisito relacionado: RU-04 (código de identificación)
- Issue: #1
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

- El documento del cliente no define reglas de formato para cédula, celular ni correo institucional. Supuesto de análisis: cédula de 10 dígitos, celular de 10 dígitos y correo con el dominio de la institución. 
