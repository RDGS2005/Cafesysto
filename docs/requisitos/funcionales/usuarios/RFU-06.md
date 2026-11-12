# RFU-06 — Actualización de credenciales de Administrador

## Descripción

**Como** Administrador del sistema,
**quiero** cambiar mi contraseña,
**para** mantener protegido el acceso a las funciones de administración.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema solicita la contraseña actual; si no coincide, rechaza el cambio, informa el motivo y no modifica nada.
- [ ] La nueva contraseña debe ingresarse dos veces; si ambas no coinciden, el sistema informa el motivo y no realiza el cambio.
- [ ] La nueva contraseña debe ser distinta de la contraseña actual.
- [ ] Una vez realizado el cambio, el Administrador solo puede ingresar con la nueva contraseña; la contraseña por defecto deja de ser válida.
- [ ] Si falla cualquier validación, la contraseña anterior se conserva sin cambios.
- [ ] El cambio persiste entre ejecuciones del sistema.

## Reglas de negocio relacionadas

N/A (decisión técnica).

## Trazabilidad

- Requisito relacionado: N/A (decisión técnica).
- Issue: #1
- Pull Request: Pendiente
- Casos de prueba: Pendiente 

## Observaciones

- El sistema cuenta con credenciales predeterminadas (usuario y contraseña) para el usuario Administrador. 
- Al iniciar sesión por primera vez con las credenciales predeterminadas, el sistema exigirá al Administrador el cambio obligatorio de su contraseña.
- Las credenciales predeterminadas estarán documentadas en el manual de instalación.


