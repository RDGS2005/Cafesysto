# RFU-01 — Registrar usuario Cliente

## Descripción

**Como** Cliente (estudiante o docente),  
**quiero** registrar mi usuario con mis datos personales,  
**para** identificarme en el sistema.

## Prioridad

Alta

## Versión

v1.1

## Criterios de aceptación

- [ ] El sistema solicita nombre, apellido, cédula, celular, correo institucional y contraseña.
- [ ] Todos los campos son obligatorios; si falta alguno, el registro no se completa y se indica el campo faltante.
- [ ] El sistema valida el formato de la cédula, del celular y del correo institucional.
- [ ] No se permite registrar una cédula o un correo institucional que ya existan en el sistema; se informa el motivo.
- [ ] Los datos del Cliente se almacenan de forma persistente y siguen disponibles entre ejecuciones.
- [ ] El sistema muestra un mensaje de confirmación al finalizar el registro exitoso.
- [ ] Si los datos no cumplen las validaciones, el sistema informa el motivo y no crea el usuario.


## Reglas de negocio relacionadas

N/A (decisión técnica).

## Trazabilidad

- Requisito relacionado: N/A (decisión técnica).
- Issue: #1
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

- Dado que el documento "Especificación inicial de necesidades del cliente" no especifica reglas de formato, se asumen como parámetros normativos para el desarrollo que tanto la cédula como el número celular tendrán una longitud exacta de 10 dígitos, y el correo institucional deberá pertenecer al dominio oficial de la institución.