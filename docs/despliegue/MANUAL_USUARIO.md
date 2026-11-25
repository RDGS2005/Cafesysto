# Manual de Usuario

**Versión:** v0.0
**Autor:** Equipo 6

- [Manual de Usuario](#manual-de-usuario)
  - [1. Cliente](#1-cliente)
    - [1.1. ¿Cómo registrarse?](#11-cómo-registrarse)
    - [1.2. ¿Cómo iniciar sesión?](#12-cómo-iniciar-sesión)
    - [1.3. ¿Cómo consultar el menú?](#13-cómo-consultar-el-menú)
    - [1.4. ¿Cómo agregar productos (comida, bebida) a mi pedido?](#14-cómo-agregar-productos-comida-bebida-a-mi-pedido)
    - [1.5. ¿Cómo confirmar mi pedido?](#15-cómo-confirmar-mi-pedido)
    - [1.6. ¿Cómo consultar el estado de mi pedido?](#16-cómo-consultar-el-estado-de-mi-pedido)
    - [1.7. ¿Cómo retirar mi pedido?](#17-cómo-retirar-mi-pedido)
  - [2. Personal](#2-personal)
    - [2.1. ¿Cómo iniciar sesión?](#21-cómo-iniciar-sesión)
    - [2.2. ¿Cómo consultar los productos?](#22-cómo-consultar-los-productos)
    - [2.3. ¿Cómo registrar un nuevo producto (comida, bebida)?](#23-cómo-registrar-un-nuevo-producto-comida-bebida)
    - [2.4. ¿Cómo cambiar el precio de un producto?](#24-cómo-cambiar-el-precio-de-un-producto)
    - [2.5. ¿Cómo cambiar la disponibilidad de un producto?](#25-cómo-cambiar-la-disponibilidad-de-un-producto)
    - [2.6. ¿Cómo consultar los pedidos de los clientes?](#26-cómo-consultar-los-pedidos-de-los-clientes)
    - [2.7. ¿Cómo avanzar un pedido?](#27-cómo-avanzar-un-pedido)
    - [2.8. ¿Cómo entregar un pedido?](#28-cómo-entregar-un-pedido)
  - [3. Administrador](#3-administrador)
    - [3.1. ¿Cómo iniciar sesión?](#31-cómo-iniciar-sesión)
    - [3.2. ¿Cómo cambiar la clave?](#32-cómo-cambiar-la-clave)
    - [3.3. ¿Cómo registrar a un miembro del personal?](#33-cómo-registrar-a-un-miembro-del-personal)

## 1. Cliente

### 1.1. ¿Cómo registrarse?

1. Ingrese a Cafesysto desde su navegador web y pulse el botón **Registro estudiantes**.
2. Complete todos los campos obligatorios: **Nombre**, **Apellido**, **Cédula** (10 números, sin guiones), **Celular** (10 números, empieza con `09`), **Correo institucional** y **Contraseña** (mínimo 8 caracteres).
3. Si desea revisar lo escrito en la clave, marque la casilla **Mostrar contraseña**.
4. Pulse **Registrarme** y verifique el mensaje de confirmación `Registro exitoso. Ya puede iniciar sesión`.

![Formulario de registro de estudiantes](./img/usuarios/1.1-registro-cliente.png)

### 1.2. ¿Cómo iniciar sesión?

1. En la pantalla inicial de Cafesysto, escriba su **Correo** institucional y su **Contraseña** (puede marcar **Mostrar contraseña** si necesita comprobarla).
2. Verifique que en **Tipo de usuario** esté seleccionada la opción **Estudiante**.
3. Pulse **Iniciar sesión** para ingresar a su panel principal con acceso a **Menú** y **Mis pedidos**.

![Inicio de sesión de estudiante](./img/usuarios/1.2-login-cliente.png)

### 1.3. ¿Cómo consultar el menú?

1. Inicie sesión con su cuenta de Cliente.
2. En la barra de navegación superior, pulse **Menú**.
3. Revise los productos disponibles junto con su precio vigente y el selector de **Cantidad**.
4. Los productos que el personal marque como no disponibles no se muestran en esta vista.

![Consulta del menú por el cliente](./img/productos/1.3-consultar-menu.png)

### 1.4. ¿Cómo agregar productos (comida, bebida) a mi pedido?

1. Ingrese a la pestaña **Menú** desde la barra de navegación superior del panel de Cliente.
2. En la tarjeta del producto que desea ordenar, indique la **Cantidad** (número entero entre `1` y `99`).
3. Pulse **Agregar al pedido**.
4. Repita el proceso para añadir otros productos; el contador junto a **Mis pedidos** mostrará el total de unidades acumuladas.

![Selección y adición de productos al pedido](./img/pedidos/1.4-agregar-productos.png)

### 1.5. ¿Cómo confirmar mi pedido?

1. Pulse **Mis pedidos** en la barra superior.
2. Revise el listado de productos, cantidades, subtotales y el **Importe total** (puede pulsar **Retirar** si desea quitar un artículo antes de confirmar).
3. Pulse **Confirmar pedido**.
4. El sistema registrará el pedido en estado **Pendiente**, vaciará la selección en curso y mostrará su **código único de retiro** (`PED-XXXXXX`). Conserve este código para presentarlo en mostrador.

![Confirmación del pedido y código de retiro](./img/pedidos/1.5-confirmar-pedido.png)

### 1.6. ¿Cómo consultar el estado de mi pedido?

1. Abra la pestaña **Mis pedidos** en su sesión de Cliente.
2. En el listado inferior de pedidos confirmados, ubique la tarjeta de su pedido por su código de retiro.
3. Revise la etiqueta de estado (**Pendiente**, **En preparación**, **Listo para retirar** o **Entregado**), que se actualiza automáticamente conforme avanza la atención.

![Consulta del estado del pedido por el cliente](./img/pedidos/1.6-consultar-estado.png)

### 1.7. ¿Cómo retirar mi pedido?

*Nota: El pago y el retiro físico del pedido se realizan fuera de la aplicación, directamente en el mostrador de la cafetería.*

1. Verifique en **Mis pedidos** que el estado de su pedido aparezca como **Listo para retirar**.
2. Acérquese al mostrador de la cafetería y realice el pago presencial por el importe total indicado en su pedido.
3. Indique o muestre al personal su **código de retiro** (`PED-XXXXXX`).
4. Una vez validado el código, el personal le entregará sus productos y registrará el pedido como **Entregado** en el sistema.

## 2. Personal

### 2.1. ¿Cómo iniciar sesión?

1. Abra la pantalla inicial de Cafesysto.
2. Ingrese el **Correo** y la **Contraseña** proporcionados por el Administrador.
3. En el campo **Tipo de usuario**, seleccione **Personal**.
4. Pulse **Iniciar sesión** para acceder a las secciones **Gestionar productos** y **Atender pedidos**.

![Inicio de sesión de personal](./img/usuarios/2.1-login-personal.png)

### 2.2. ¿Cómo consultar los productos?

1. Inicie sesión con su cuenta de **Personal** y pulse **Gestionar productos** en la barra superior.
2. Desplácese a la sección **Productos** para ver el listado completo con código, nombre, precio actual y estado (**Disponible** o **No disponible**).

![Lista de productos en el panel de personal](./img/productos/2.2-consultar-productos.png)

### 2.3. ¿Cómo registrar un nuevo producto (comida, bebida)?

1. En **Gestionar productos**, ubique el formulario **Nuevo producto**.
2. Escriba el **Nombre** del artículo (sin repetir uno existente) y su **Precio (USD)** mayor a cero.
3. Deje marcada la casilla **Disponible** para mostrarlo de inmediato en el menú, o desmárquela si aún no estará a la venta.
4. Pulse **Agregar producto** y verifique el mensaje `Producto registrado exitosamente`.

![Registro de un nuevo producto](./img/productos/2.3-registrar-producto.png)

### 2.4. ¿Cómo cambiar el precio de un producto?

1. En la lista **Productos** de la sección **Gestionar productos**, busque el producto que desea editar.
2. Pulse el botón **Cambiar precio**.
3. Escriba el nuevo valor numérico mayor a cero y pulse **Guardar** (o **Cancelar** para mantener el precio anterior).
4. El nuevo precio se actualizará de inmediato en el menú de los clientes sin alterar los pedidos ya confirmados.

![Edición del precio de un producto](./img/productos/2.4-cambiar-precio.png)

### 2.5. ¿Cómo cambiar la disponibilidad de un producto?

1. En **Gestionar productos**, ubique el producto cuyo estado desea modificar.
2. Pulse **Marcar no disponible** para ocultarlo del menú de los clientes, o **Marcar disponible** para habilitarlo nuevamente.
3. Confirme el cambio en la etiqueta del producto y en el mensaje superior.

![Cambio de disponibilidad de un producto](./img/productos/2.5-cambiar-disponibilidad.png)

### 2.6. ¿Cómo consultar los pedidos de los clientes?

1. Inicie sesión con su cuenta de **Personal** y pulse **Atender pedidos** en la barra superior.
2. Revise el tablero organizado en tres columnas: **Pendiente**, **En preparación** y **Listo para retirar**.
3. Cada tarjeta muestra el código de retiro, el detalle de productos y el importe total. Los pedidos entregados salen automáticamente del tablero.

![Consulta de pedidos en el tablero de personal](./img/pedidos/2.6-consultar-pedidos-personal.png)

### 2.7. ¿Cómo avanzar un pedido?

1. En **Atender pedidos**, ubique la tarjeta del pedido que desea actualizar.
2. Para pasar de **Pendiente** a **En preparación**, pulse **Pasar a En preparación** (también puede arrastrar y soltar la tarjeta hacia la columna siguiente).
3. Cuando el pedido esté listo, pulse **Pasar a Listo para retirar** (o arrastre la tarjeta a la columna **Listo para retirar**).
4. Cada pedido solo avanza un paso a la vez y no se puede devolver a una columna anterior.

![Avance de estado de un pedido en el tablero](./img/pedidos/2.7-avanzar-pedido.png)

### 2.8. ¿Cómo entregar un pedido?

1. Confirme que el pedido se encuentre en la columna **Listo para retirar**.
2. Verifique el pago presencial del cliente y solicite su código de retiro (`PED-XXXXXX`).
3. En el campo **Código del pedido** de la parte superior, ingrese el código y pulse **Entregar**.
4. El sistema validará el código, mostrará el resumen en **Pedido entregado** y retirará la tarjeta del tablero.

![Entrega de un pedido mediante código de retiro](./img/pedidos/2.8-entregar-pedido.png)

## 3. Administrador

### 3.1. ¿Cómo iniciar sesión?

1. Abra la pantalla inicial de Cafesysto.
2. Ingrese el **Correo** y la **Contraseña** de Administrador.
3. En **Tipo de usuario**, seleccione **Administrador** y pulse **Iniciar sesión**.
4. Si ingresa por primera vez con la contraseña predeterminada, el sistema abrirá directamente la vista **Cambiar contraseña** y mantendrá deshabilitadas las demás opciones hasta completar el cambio.

![Inicio de sesión de administrador](./img/usuarios/3.1-login-admin.png)

### 3.2. ¿Cómo cambiar la clave?

1. En la barra superior del panel de Administrador, pulse **Cambiar contraseña** (en el primer acceso esta pantalla se abre de forma automática).
2. Escriba su **Contraseña actual**.
3. Ingrese la **Nueva contraseña** (mínimo 8 caracteres y distinta a la actual) y repítala exactamente en **Confirmar nueva contraseña**.
4. Pulse **Cambiar contraseña**. Al completarse el cambio se habilitará el resto del menú y deberá usar la nueva clave en futuros ingresos.

![Cambio de contraseña del administrador](./img/usuarios/3.2-cambiar-clave-admin.png)

### 3.3. ¿Cómo registrar a un miembro del personal?

1. En el panel de Administrador (con la contraseña inicial ya actualizada), pulse **Registrar personal** en la barra superior.
2. Llene los datos del empleado: **Nombre**, **Apellido**, **Cédula** (10 números, sin guiones), **Celular** (10 números, empieza con `09`), **Correo** (cualquier correo válido) y la **Contraseña** inicial (mínimo 8 caracteres).
3. Pulse **Registrar** y confirme el mensaje `Registro exitoso de empleado`.

![Registro de personal desde el panel de administrador](./img/usuarios/3.3-registrar-personal.png)
