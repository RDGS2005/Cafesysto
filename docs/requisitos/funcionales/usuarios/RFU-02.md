# RFU-02 — Ingreso al sistema con usuario Cliente 

## Descripción

**Como** cliente registrado de la cafetería,  
**quiero** que el sistema consulte y valide mis datos en la base de datos al ingresar, 
**para** acceder de forma segura a mis pedidos y realizar nuevos pedidos con mi identidad.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema solicita las credenciales del cliente.
- [ ] El sistema consulta en la base de datos si existe un cliente con las credenciales ingresadas.
- [ ] Si las credenciales son correctas, el sistema permite el acceso y asocia la sesión al código del cliente.
- [ ] Si las credenciales son incorrectas o el usuario no existe, el sistema muestra un mensaje de error genérico sin revelar cuál dato falló.
- [ ] Un usuario no validado no puede registrar pedidos ni consultar pedidos.
- [ ] El cliente validado solo puede consultar sus propios pedidos.
- [ ] La validación funciona con los datos persistidos de ejecuciones anteriores.
- [ ] Durante el registro (RFU-01), el sistema consulta la base de datos para detectar cédulas o correos duplicados.

## Reglas de negocio relacionadas

- RN-NEG-05: cada pedido confirmado recibe un código de retiro único.
- RN-NEG-07: la entrega exige que el pedido esté Listo para retirar y que se presente el código correspondiente.
- Los datos del cliente son necesarios para consultar sus pedidos.
- Cada cliente se identifica mediante un código único.
- La información debe persistir entre ejecuciones.

## Trazabilidad

- Requisito relacionado: RU-04, RU-05, RFU-01.
- Issue: #1
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

- El documento no define el mecanismo de ingreso. Se asume correo institucional y contraseña. **Por confirmar con el cliente.**
- Las contraseñas no deben almacenarse en texto plano (decisión de diseño pendiente de documentar).
- Pendiente definir si se limitan los intentos fallidos de ingreso.