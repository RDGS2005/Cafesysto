# DCF — Diagrama de Clases Módulo de Facturación

## Diagrama

```mermaid

classDiagram

namespace Facturación {

    class FacturaController {
        -facturaService: FacturaService
        +crearFactura(datos): Factura
        +consultarFactura(id): Factura
        +listarFacturas(): List~Factura~
        +anularFactura(id): void
    }

    class FacturaService {
        -facturaRepository: FacturaRepository
        +generarFactura(cliente, detalles): Factura
        +obtenerFactura(id): Factura
        +listarFacturas(): List~Factura~
        +anularFactura(id): void
        -calcularSubtotal(detalles): Decimal
        -calcularImpuestos(subtotal): Decimal
        -calcularTotal(subtotal, impuestos): Decimal
    }

    class FacturaRepository {
        <<interface>>
        +guardar(factura): Factura
        +buscarPorId(id): Factura
        +buscarTodas(): List~Factura~
        +actualizar(factura): void
    }

    class Factura {
        -id: Long
        -numero: String
        -fechaEmision: Date
        -subtotal: Decimal
        -impuestos: Decimal
        -total: Decimal
        -estado: EstadoFactura
        +calcularTotal(): Decimal
        +agregarDetalle(detalle): void
        +eliminarDetalle(detalle): void
        +anular(): void
    }

    class DetalleFactura {
        -id: Long
        -cantidad: Integer
        -precioUnitario: Decimal
        -descuento: Decimal
        -subtotal: Decimal
        +calcularSubtotal(): Decimal
    }

    class EstadoFactura {
        <<enumeration>>
        EMITIDA
        ANULADA
    }
}

class Cliente {
    -id: Long
    -nombre: String
    -identificacion: String
    -direccion: String
    +obtenerDatosFacturacion(): String
}

class Producto {
    -id: Long
    -nombre: String
    -precio: Decimal
    +obtenerPrecio(): Decimal
}

class Pago {
    -id: Long
    -monto: Decimal
    -fecha: Date
    -metodo: String
    +registrarPago(): void
}

FacturaController --> FacturaService : utiliza
FacturaService --> FacturaRepository : utiliza
FacturaService ..> Cliente : consulta
FacturaService ..> Producto : consulta

FacturaRepository ..> Factura : persiste

Factura "1" *-- "1..*" DetalleFactura : contiene
Factura "1" --> "1" Cliente : pertenece a
DetalleFactura "*" --> "1" Producto : referencia
Factura "1" --> "0..*" Pago : registra
Factura --> EstadoFactura : estado

```

## Versión

v1.0

## Trazabilidad

- Issue: #1
- Pull Request: #2

## Observaciones

Espacio para aclaraciones o decisiones de análisis.