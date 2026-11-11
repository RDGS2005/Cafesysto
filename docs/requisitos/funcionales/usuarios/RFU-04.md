# RFU-04 — Ingreso al sistema con usuario Personal 

## Descripción

**Como** personal de la cafetería,  
**quiero** que el sistema consulte y valide mis datos en la base de datos al ingresar,  
**para** acceder únicamente a las funciones de gestión del menú y de procesamiento de pedidos.

## Prioridad

Alta

## Versión

v1.0

## Criterios de aceptación

- [ ] El sistema solicita las credenciales del personal.
- [ ] El sistema consulta en la base de datos si existe un usuario con rol Personal y las credenciales ingresadas.
- [ ] Si las credenciales son correctas, el sistema permite el acceso a las funciones del Personal.
- [ ] Si las credenciales son incorrectas o el usuario no existe, el sistema muestra un mensaje de error genérico sin revelar cuál dato falló.
- [ ] Un usuario con rol Cliente no puede acceder a las funciones del Personal, aunque sus credenciales sean válidas.
- [ ] Un usuario no validado no puede modificar el menú ni cambiar el estado de los pedidos.
- [ ] La validación funciona con los datos persistidos de ejecuciones anteriores.
- [ ] Durante el registro (RFU-03), el sistema consulta la base de datos para detectar cédulas o correos duplicados.

## Reglas de negocio relacionadas


- RN-NEG-07: el Personal verifica el código de retiro antes de marcar un pedido como Entregado.
- Solo el personal registra o actualiza productos, precios y disponibilidad.
- Solo el personal consulta pedidos y registra sus avances hasta la entrega.

## Trazabilidad

- Requisito relacionado: RU-06, RU-07, RFU-03
- Issue: #1
- Pull Request: Pendiente
- Casos de prueba: Pendiente

## Observaciones

- Se recomienda compartir la lógica de validación entre RFU-02 y RFU-04, diferenciando únicamente por rol.