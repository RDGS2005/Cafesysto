# RFU-05 — Registrar Personal 

## Descripción

**Como** Administrador del sistema,  
**quiero** registrar al Personal,  
**para** controlar quién puede operar el sistema.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación


- [ ] El sistema solicita nombre, apellido, cédula, celular y correo del Personal. 
- [ ] Todos los campos son obligatorios; si falta alguno, el registro no se completa y se indica el campo faltante.
- [ ] El sistema valida el formato de la cédula, del celular y del correo institucional.
- [ ] No se permite registrar una cédula o un correo que ya exista en el sistema.
- [ ] Los datos del personal se almacenan de forma persistente y siguen disponibles entre ejecuciones.
- [ ] El sistema muestra un mensaje de confirmación al finalizar el registro exitoso.
- [ ] Si los datos no cumplen las validaciones, el sistema informa el motivo y no crea el usuario.

## Reglas de negocio relacionadas

N/A (decisión técnica).

## Trazabilidad

- Origen: Equipo 6
- Issue: #1
- Pull Request: #9
- Casos de prueba: Pendiente 

## Observaciones

Ninguna.