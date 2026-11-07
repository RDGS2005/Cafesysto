# RFU-01 — Registrar los datos del cliente

## Descripción

**Como** cliente de la cafetería,
**quiero** registrar mis datos y obtener un código de cliente,
**para** identificarme en el sistema y vincular mis pedidos a mi registro.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema permite registrar nombre, apellido, cédula, celular y correo institucional del cliente.
- [ ] Si falta alguno de los datos requeridos, el sistema indica cuál debe completarse y no confirma el registro.
- [ ] Al completar correctamente el registro, el sistema asigna un código de cliente único y muestra una confirmación con dicho código.
- [ ] El código permite distinguir al cliente de los demás clientes registrados.
- [ ] Los datos registrados y el código del cliente se conservan entre ejecuciones para mantener la relación con sus pedidos.

## Reglas de negocio relacionadas

- Especificación del cliente, sección 7: el cliente se identifica mediante un código y sus datos de nombre, apellido, cédula, celular y correo institucional.
- RN-NEG-05: el código de retiro identifica un pedido confirmado; es distinto conceptualmente del código que identifica al cliente.
- Decisión de análisis propuesta: los datos indicados en la sección 7 son obligatorios y el código de cliente es único.
- Decisión de análisis propuesta: conservar el registro del cliente permite mantener su relación con los pedidos persistentes exigidos por la sección 8.

## Trazabilidad

- Requisito relacionado: sección 7 (Cliente) y sección 8 (persistencia); soporte a RU-04 y RU-05.
- Issue: #1
- Pull Request: pendiente
- Casos de prueba: Pendiente

## Observaciones

Historia derivada del modelo de dominio; la especificación no incluye un RU específico para el registro de clientes. La obligatoriedad de los campos, la unicidad del código y la persistencia del cliente son decisiones propuestas para revisión. Quedan por acordar las validaciones de cédula y celular, los dominios institucionales admitidos y el tratamiento de registros duplicados. No se presupone una integración externa para validar esos datos.
