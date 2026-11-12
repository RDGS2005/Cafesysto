# RFU-02 — Ingreso al sistema con usuario Cliente 

## Descripción

**Como** Cliente,  
**quiero** ingresar al sistema con mi usuario registrado,   
**para** acceder de forma segura a mis pedidos y realizar nuevos pedidos con mi identidad.

## Prioridad

Alta

## Versión

v2.0

## Criterios de aceptación

- [ ] El sistema solicita las credenciales del Cliente (correo institucional y contraseña).
- [ ] El sistema consulta si existe un usuario registrado con las credenciales ingresadas.
- [ ] Si las credenciales son correctas, el sistema permite el acceso.
- [ ] Si las credenciales son incorrectas o el usuario no existe, el sistema muestra un mensaje de error genérico sin revelar cuál dato falló.
- [ ] El Cliente validado solo puede consultar sus propios pedidos.

## Reglas de negocio relacionadas

N/A (decisión técnica).

## Trazabilidad

- Requisito relacionado: N/A (decisión técnica)
- Issue: #1
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

- Las contraseñas no deben almacenarse en texto plano.