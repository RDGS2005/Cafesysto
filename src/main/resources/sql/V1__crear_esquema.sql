-- =====================================================================
-- Cafetería universitaria - Script DDL
-- Motor: PostgreSQL 18
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tipos enumerados (RolUsuario, EstadoProducto, EstadoPedido)
-- El orden de los valores de estado_pedido define el orden de avance.
-- ---------------------------------------------------------------------
CREATE TYPE rol_usuario AS ENUM ('CLIENTE', 'PERSONAL', 'ADMINISTRADOR');

CREATE TYPE estado_producto AS ENUM ('DISPONIBLE', 'NO_DISPONIBLE');

CREATE TYPE estado_pedido AS ENUM (
    'PENDIENTE',
    'EN_PREPARACION',
    'LISTO_PARA_RETIRAR',
    'ENTREGADO'
);

-- ---------------------------------------------------------------------
-- USUARIO
-- Usuario (abstracta) -> Persona (abstracta) -> Cliente / Personal
-- Usuario -> Administrador
-- Se modela con una sola tabla; el rol se deriva del subtipo y las
-- restricciones CHECK garantizan los datos propios de cada subtipo.
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    rol                   rol_usuario  NOT NULL,
    clave_hash            TEXT         NOT NULL,

    -- Datos de Persona (CLIENTE y PERSONAL)
    nombre                VARCHAR(100),
    apellido              VARCHAR(100),
    cedula                CHAR(10),
    celular               CHAR(10),
    correo                VARCHAR(254),

    -- Cliente
    codigo_cliente        VARCHAR(20),

    -- Administrador
    nombre_usuario        VARCHAR(50),
    requiere_cambio_clave BOOLEAN,

    CONSTRAINT uq_usuario_id_rol UNIQUE (id, rol),

    CONSTRAINT ck_usuario_clave_hash CHECK (length(clave_hash) > 0),

    CONSTRAINT ck_usuario_persona CHECK (
        rol = 'ADMINISTRADOR'
        OR (nombre   IS NOT NULL AND length(btrim(nombre))   > 0
        AND apellido IS NOT NULL AND length(btrim(apellido)) > 0
        AND cedula   IS NOT NULL AND cedula  ~ '^[0-9]{10}$'
        AND celular  IS NOT NULL AND celular ~ '^[0-9]{10}$'
        AND correo   IS NOT NULL AND length(btrim(correo)) > 0)
    ),

    CONSTRAINT ck_usuario_cliente CHECK (
        (rol = 'CLIENTE'  AND codigo_cliente IS NOT NULL AND length(btrim(codigo_cliente)) > 0)
        OR (rol <> 'CLIENTE' AND codigo_cliente IS NULL)
    ),

    CONSTRAINT ck_usuario_administrador CHECK (
        (rol = 'ADMINISTRADOR'
            AND nombre_usuario IS NOT NULL AND length(btrim(nombre_usuario)) > 0
            AND requiere_cambio_clave IS NOT NULL
            AND nombre IS NULL AND apellido IS NULL AND cedula IS NULL
            AND celular IS NULL AND correo IS NULL)
        OR (rol <> 'ADMINISTRADOR'
            AND nombre_usuario IS NULL AND requiere_cambio_clave IS NULL)
    )
);

-- Unicidad de cédula, correo (sin distinguir mayúsculas), código de cliente
-- y nombre de usuario del administrador (RFU-01, RFU-05)
CREATE UNIQUE INDEX uq_usuario_cedula          ON usuario (cedula)         WHERE cedula IS NOT NULL;
CREATE UNIQUE INDEX uq_usuario_correo          ON usuario (lower(correo))  WHERE correo IS NOT NULL;
CREATE UNIQUE INDEX uq_usuario_codigo_cliente  ON usuario (codigo_cliente) WHERE codigo_cliente IS NOT NULL;
CREATE UNIQUE INDEX uq_usuario_nombre_usuario  ON usuario (lower(nombre_usuario)) WHERE nombre_usuario IS NOT NULL;

COMMENT ON TABLE  usuario                IS 'Clientes, personal y administrador. El rol se deriva del subtipo.';
COMMENT ON COLUMN usuario.clave_hash     IS 'Hash de la contraseña; nunca se guarda en texto plano.';
COMMENT ON COLUMN usuario.codigo_cliente IS 'Código generado por el sistema; solo para CLIENTE.';

-- ---------------------------------------------------------------------
-- PRODUCTO
-- ---------------------------------------------------------------------
CREATE TABLE producto (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo  VARCHAR(20)     NOT NULL,
    nombre  VARCHAR(100)    NOT NULL,
    precio  NUMERIC(10, 2)  NOT NULL,
    estado  estado_producto NOT NULL DEFAULT 'DISPONIBLE',

    CONSTRAINT uq_producto_codigo UNIQUE (codigo),
    CONSTRAINT ck_producto_nombre CHECK (length(btrim(nombre)) > 0),
    CONSTRAINT ck_producto_precio CHECK (precio > 0)
);

-- Nombre único sin distinguir mayúsculas (validarNombreUnico)
CREATE UNIQUE INDEX uq_producto_nombre ON producto (lower(nombre));

COMMENT ON TABLE producto IS 'Los productos no se borran: se marcan como NO_DISPONIBLE (RFM-03-2).';

-- ---------------------------------------------------------------------
-- PEDIDO
-- cliente_id y personal_id apuntan a usuario; las columnas *_rol con
-- clave foránea compuesta obligan a que el usuario referenciado tenga
-- el rol correcto (CLIENTE / PERSONAL).
-- ---------------------------------------------------------------------
CREATE TABLE pedido (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_retiro   VARCHAR(20)    NOT NULL,
    fecha_registro  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    total           NUMERIC(12, 2) NOT NULL DEFAULT 0,
    estado          estado_pedido  NOT NULL DEFAULT 'PENDIENTE',

    cliente_id      BIGINT      NOT NULL,
    cliente_rol     rol_usuario NOT NULL DEFAULT 'CLIENTE',
    personal_id     BIGINT,
    personal_rol    rol_usuario,

    CONSTRAINT uq_pedido_codigo_retiro UNIQUE (codigo_retiro),
    CONSTRAINT ck_pedido_total CHECK (total >= 0),

    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cliente_id, cliente_rol) REFERENCES usuario (id, rol),
    CONSTRAINT ck_pedido_cliente_rol CHECK (cliente_rol = 'CLIENTE'),

    CONSTRAINT fk_pedido_personal
        FOREIGN KEY (personal_id, personal_rol) REFERENCES usuario (id, rol),
    CONSTRAINT ck_pedido_personal_rol CHECK (
        (personal_id IS NULL AND personal_rol IS NULL)
        OR (personal_id IS NOT NULL AND personal_rol = 'PERSONAL')
    ),

    -- Un pedido que ya salió de PENDIENTE debe tener personal que lo procesa
    CONSTRAINT ck_pedido_personal_estado CHECK (
        estado = 'PENDIENTE' OR personal_id IS NOT NULL
    )
);

CREATE INDEX ix_pedido_cliente ON pedido (cliente_id);
CREATE INDEX ix_pedido_estado  ON pedido (estado);

COMMENT ON COLUMN pedido.codigo_retiro IS 'Código único generado por el sistema al confirmar el pedido (RN-NEG-05).';

-- ---------------------------------------------------------------------
-- DETALLE_PEDIDO
-- precio_unitario conserva el precio del momento de la confirmación,
-- aunque el precio del producto cambie después (RN-NEG-04).
-- ---------------------------------------------------------------------
CREATE TABLE detalle_pedido (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pedido_id        BIGINT         NOT NULL,
    producto_id      BIGINT         NOT NULL,
    cantidad         INTEGER        NOT NULL,
    precio_unitario  NUMERIC(10, 2) NOT NULL,
    subtotal         NUMERIC(12, 2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,

    CONSTRAINT fk_detalle_pedido   FOREIGN KEY (pedido_id)   REFERENCES pedido (id)   ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE RESTRICT,
    CONSTRAINT uq_detalle_pedido_producto UNIQUE (pedido_id, producto_id),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),           -- RN-NEG-01
    CONSTRAINT ck_detalle_precio   CHECK (precio_unitario > 0)
);

CREATE INDEX ix_detalle_producto ON detalle_pedido (producto_id);

-- ---------------------------------------------------------------------
-- TRIGGERS
-- ---------------------------------------------------------------------

-- 1) El total del pedido es la suma de los subtotales de sus detalles (RN-NEG-03)
CREATE FUNCTION fn_recalcular_total_pedido() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_pedido BIGINT;
BEGIN
    v_pedido := COALESCE(NEW.pedido_id, OLD.pedido_id);

    UPDATE pedido
       SET total = COALESCE((SELECT sum(subtotal) FROM detalle_pedido WHERE pedido_id = v_pedido), 0)
     WHERE id = v_pedido;

    RETURN NULL;
END;
$$;

CREATE TRIGGER tg_detalle_recalcular_total
AFTER INSERT OR UPDATE OR DELETE ON detalle_pedido
FOR EACH ROW EXECUTE FUNCTION fn_recalcular_total_pedido();

-- 2) El estado solo avanza un paso a la vez, sin saltos ni retrocesos (RN-NEG-06),
--    y los datos fijos del pedido no se modifican después de registrarlo.
CREATE FUNCTION fn_validar_transicion_estado() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.codigo_retiro IS DISTINCT FROM OLD.codigo_retiro
       OR NEW.cliente_id IS DISTINCT FROM OLD.cliente_id
       OR NEW.fecha_registro IS DISTINCT FROM OLD.fecha_registro THEN
        RAISE EXCEPTION 'El código de retiro, el cliente y la fecha del pedido no se pueden modificar';
    END IF;

    IF NEW.estado IS DISTINCT FROM OLD.estado THEN
        IF NOT (
               (OLD.estado = 'PENDIENTE'          AND NEW.estado = 'EN_PREPARACION')
            OR (OLD.estado = 'EN_PREPARACION'     AND NEW.estado = 'LISTO_PARA_RETIRAR')
            OR (OLD.estado = 'LISTO_PARA_RETIRAR' AND NEW.estado = 'ENTREGADO')
        ) THEN
            RAISE EXCEPTION 'Transición de estado no permitida: % -> %', OLD.estado, NEW.estado;
        END IF;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER tg_pedido_validar_transicion
BEFORE UPDATE ON pedido
FOR EACH ROW EXECUTE FUNCTION fn_validar_transicion_estado();

-- 3) Un pedido confirmado no cambia su contenido: solo se pueden agregar
--    detalles mientras el pedido se está creando (PENDIENTE sin procesar).
CREATE FUNCTION fn_proteger_detalle_confirmado() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_estado estado_pedido;
BEGIN
    SELECT estado INTO v_estado
      FROM pedido
     WHERE id = COALESCE(NEW.pedido_id, OLD.pedido_id);

    IF TG_OP = 'UPDATE' AND (NEW.precio_unitario IS DISTINCT FROM OLD.precio_unitario) THEN
        RAISE EXCEPTION 'El precio registrado en un pedido no puede cambiar (RN-NEG-04)';
    END IF;

    IF v_estado IS DISTINCT FROM 'PENDIENTE' AND v_estado IS NOT NULL THEN
        RAISE EXCEPTION 'No se pueden modificar los detalles de un pedido en estado %', v_estado;
    END IF;

    RETURN COALESCE(NEW, OLD);
END;
$$;

CREATE TRIGGER tg_detalle_proteger
BEFORE UPDATE OR DELETE ON detalle_pedido
FOR EACH ROW EXECUTE FUNCTION fn_proteger_detalle_confirmado();