-- =====================================================================
-- Datos de prueba (DESARROLLO / PRUEBAS). No usar en producción.
-- Pobla: personal, clientes, productos y pedidos en distintos estados.
-- El administrador NO se crea aquí: lo crea AdministradorInicializador
-- al arrancar, con las variables CAFESYSTO_ADMIN_USUARIO / _CLAVE.
-- Las claves están guardadas como hash BCrypt (las mismas que usa la app).
-- =====================================================================

-- ---------------------------------------------------------------------
-- Personal (clave de prueba: Personal2026)
-- ---------------------------------------------------------------------
INSERT INTO usuario (rol, clave_hash, nombre, apellido, cedula, celular, correo) VALUES
    ('PERSONAL', '$2a$10$IjLDXGhfUYdnP1y70NUUVOpM6GcvVvjKRQGLjnGZ7QLgMNC.0j8Ga', 'María', 'Andrade', '0912345678', '0991112233', 'maria.andrade@cafeteria.uni.edu.ec'),
    ('PERSONAL', '$2a$10$IjLDXGhfUYdnP1y70NUUVOpM6GcvVvjKRQGLjnGZ7QLgMNC.0j8Ga', 'Carlos', 'Mendoza', '0923456789', '0992223344', 'carlos.mendoza@cafeteria.uni.edu.ec');

-- ---------------------------------------------------------------------
-- Clientes (clave de prueba: Cliente2026)
-- ---------------------------------------------------------------------
INSERT INTO usuario (rol, clave_hash, nombre, apellido, cedula, celular, correo, codigo_cliente) VALUES
    ('CLIENTE', '$2a$10$xpaXUj/GM39bmGIW4fLwNuYuI41vtPzapC1ZKiSa2VjPjU9KdWgPa', 'Ana', 'Pérez', '1712345670', '0981234567', 'ana.perez@uni.edu.ec', 'CLI-100001'),
    ('CLIENTE', '$2a$10$xpaXUj/GM39bmGIW4fLwNuYuI41vtPzapC1ZKiSa2VjPjU9KdWgPa', 'Luis', 'Torres', '1723456781', '0982345678', 'luis.torres@uni.edu.ec', 'CLI-100002'),
    ('CLIENTE', '$2a$10$xpaXUj/GM39bmGIW4fLwNuYuI41vtPzapC1ZKiSa2VjPjU9KdWgPa', 'Sofía', 'Vargas', '1734567892', '0983456789', 'sofia.vargas@uni.edu.ec', 'CLI-100003'),
    ('CLIENTE', '$2a$10$xpaXUj/GM39bmGIW4fLwNuYuI41vtPzapC1ZKiSa2VjPjU9KdWgPa', 'Diego', 'Salazar', '1745678903', '0984567890', 'diego.salazar@uni.edu.ec', 'CLI-100004');

-- ---------------------------------------------------------------------
-- Productos
-- ---------------------------------------------------------------------
INSERT INTO producto (codigo, nombre, precio, estado) VALUES
    ('PRD-AMER22', 'Café americano',   1.50, 'DISPONIBLE'),
    ('PRD-CAPU33', 'Capuchino',        2.25, 'DISPONIBLE'),
    ('PRD-LATT44', 'Café latte',       2.50, 'DISPONIBLE'),
    ('PRD-MCHA55', 'Mocachino',        2.75, 'DISPONIBLE'),
    ('PRD-TEAA66', 'Té helado',        1.25, 'DISPONIBLE'),
    ('PRD-JUGN77', 'Jugo natural',     2.00, 'DISPONIBLE'),
    ('PRD-CROS88', 'Croissant',        1.75, 'DISPONIBLE'),
    ('PRD-SAND99', 'Sándwich de pollo',3.50, 'DISPONIBLE'),
    ('PRD-BRWN2K', 'Brownie',          2.00, 'DISPONIBLE'),
    ('PRD-EMPA3M', 'Empanada de queso',1.50, 'NO_DISPONIBLE');

-- ---------------------------------------------------------------------
-- Pedidos (uno por estado). El total lo calcula el trigger a partir de
-- los detalles; el pedido ya procesado indica el personal que lo atendió.
-- ---------------------------------------------------------------------
INSERT INTO pedido (codigo_retiro, fecha_registro, estado, cliente_id, personal_id, personal_rol)
SELECT v.codigo, now() - v.antiguedad, v.estado::estado_pedido, c.id, p.id,
       CASE WHEN p.id IS NULL THEN NULL ELSE 'PERSONAL'::rol_usuario END
  FROM (VALUES
    ('PED-A2B3C4', interval '2 days',    'ENTREGADO',          'ana.perez@uni.edu.ec',      'maria.andrade@cafeteria.uni.edu.ec'),
    ('PED-D4E5F6', interval '10 minutes','PENDIENTE',          'ana.perez@uni.edu.ec',      NULL),
    ('PED-G7H8J9', interval '25 minutes','EN_PREPARACION',     'luis.torres@uni.edu.ec',    'carlos.mendoza@cafeteria.uni.edu.ec'),
    ('PED-K2L3M4', interval '40 minutes','LISTO_PARA_RETIRAR', 'sofia.vargas@uni.edu.ec',   'maria.andrade@cafeteria.uni.edu.ec'),
    ('PED-N5P6Q7', interval '5 minutes', 'PENDIENTE',          'diego.salazar@uni.edu.ec',  NULL)
  ) AS v(codigo, antiguedad, estado, correo_cliente, correo_personal)
  JOIN usuario c ON lower(c.correo) = v.correo_cliente
  LEFT JOIN usuario p ON lower(p.correo) = v.correo_personal;

-- ---------------------------------------------------------------------
-- Detalles (el precio unitario se congela con el precio actual del producto)
-- ---------------------------------------------------------------------
INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario)
SELECT p.id, pr.id, v.cantidad, pr.precio
  FROM (VALUES
    ('PED-A2B3C4', 'PRD-AMER22', 2),
    ('PED-A2B3C4', 'PRD-SAND99', 1),
    ('PED-D4E5F6', 'PRD-CAPU33', 1),
    ('PED-D4E5F6', 'PRD-BRWN2K', 1),
    ('PED-G7H8J9', 'PRD-JUGN77', 1),
    ('PED-G7H8J9', 'PRD-BRWN2K', 2),
    ('PED-K2L3M4', 'PRD-TEAA66', 1),
    ('PED-K2L3M4', 'PRD-CROS88', 1),
    ('PED-N5P6Q7', 'PRD-LATT44', 2)
  ) AS v(pedido, producto, cantidad)
  JOIN pedido   p  ON p.codigo_retiro = v.pedido
  JOIN producto pr ON pr.codigo       = v.producto;
