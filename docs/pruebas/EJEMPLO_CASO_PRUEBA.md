# CPF-001 — Generación de factura

## Descripción

Se espera comprobar que el sistema genere correctamente una factura cuando se registra una venta con datos válidos, mostrando la información correspondiente a la transacción.

## Trazabilidad

**Historia de Usuario:** RFF-01
**Issue:** #1
**Pull Request:** #5

**Criterio(s) de aceptación relacionado(s):**
- Por completar: copiar el criterio de aceptación que verifica este caso de prueba.

## Precondiciones

- El usuario ha iniciado sesión en el sistema.
- Existe una venta registrada o se dispone de los datos necesarios para realizarla.
- Los datos del cliente y de los productos son válidos.

## Datos de prueba

- Cliente: Cliente de prueba.
- Producto: Producto de prueba.
- Cantidad: 2 unidades.
- Precio unitario: 5,00.
- Subtotal esperado: 10,00.
- Impuestos y total: según las reglas de cálculo configuradas en el sistema.

## Procedimiento de prueba

1. Iniciar la aplicación.
2. Acceder al módulo de ventas o facturación.
3. Registrar o seleccionar una venta con los datos de prueba.
4. Solicitar la generación de la factura.
5. Consultar la factura generada.
6. Verificar que la factura muestre los datos del cliente, los productos, las cantidades, los precios y los valores totales correspondientes.

## Resultados esperados

El sistema genera la factura sin errores y muestra correctamente la información de la venta, incluidos los datos del cliente, el detalle de los productos, las cantidades, los precios, los impuestos aplicables y el total. Los valores deben corresponder con los datos registrados y las reglas de cálculo del sistema.

## EJECUCIONES DEL CASO DE PRUEBA

### Ejecución 1

**Fecha:** AAAA-MM-DD  
**Responsable:** Nombre del responsable

#### Resultados obtenidos

Describir el resultado observado al ejecutar el procedimiento de prueba.

#### Estado

- [ ] Aprobado
- [ ] Reprobado

#### Capturas o evidencias
<!-- Adjuntar capturas o indicar la ruta/enlace de los archivos de evidencia. -->

---

### Ejecución 2

**Fecha:** AAAA-MM-DD  
**Responsable:** Nombre del responsable

#### Resultados obtenidos

Describir el resultado observado al ejecutar el procedimiento de prueba.

#### Estado

- [ ] Aprobado
- [ ] Reprobado

#### Capturas o evidencias

<!-- Adjuntar capturas o indicar la ruta/enlace de los archivos de evidencia. -->

---

## Observaciones

- Marcar **Aprobado** si los resultados obtenidos coinciden con los resultados esperados; de lo contrario, marcar **Reprobado**.
- Registrar una nueva ejecución cada vez que se vuelva a probar el caso, sin sobrescribir los resultados anteriores.
- Los datos y valores de este ejemplo son ilustrativos. Deben adaptarse a las reglas del sistema que se esté probando.