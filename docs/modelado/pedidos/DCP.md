# DCP — Diagrama de Clases Módulo de Pedidos

## Diagrama

```mermaid

classDiagram

namespace Pedidos {

    class PedidoController {
        -pedidoService: PedidoService
        +seleccionarProducto(codigoProducto, cantidad): DetallePedido
        +calcularTotalPrevio(detalles): Decimal
        +confirmarPedido(cedulaCliente, detalles): Pedido
        +consultarEstadoPedido(codigoRetiro): EstadoPedido
        +consultarPedidosCliente(cedulaCliente): List~Pedido~
        +consultarPedidos(): List~Pedido~
        +consultarPedidosPorEstado(estado): List~Pedido~
        +consultarDetallePedido(codigoRetiro): Pedido
        +avanzarEstadoPedido(codigoRetiro, cedulaPersonal): Pedido
        +registrarEntregaPedido(codigoRetiro, codigoPresentado, cedulaPersonal): Pedido
    }

    class PedidoService {
        -pedidoRepository: PedidoRepository
        -productoService: ProductoService
        -usuarioService: UsuarioService
        +crearDetalleSeleccion(codigoProducto, cantidad): DetallePedido
        +calcularImporteTotal(detalles): Decimal
        +confirmarPedido(cedulaCliente, detalles): Pedido
        +consultarEstado(codigoRetiro): EstadoPedido
        +listarPorCliente(cedulaCliente): List~Pedido~
        +listarTodos(): List~Pedido~
        +listarPorEstado(estado): List~Pedido~
        +buscarPorCodigoRetiro(codigoRetiro): Pedido
        +avanzarEstado(codigoRetiro, cedulaPersonal): Pedido
        +entregarPedido(codigoRetiro, codigoPresentado, cedulaPersonal): Pedido
        -validarSeleccion(detalles): boolean
        -validarCantidadPositiva(cantidad): boolean
        -validarTransicionEstado(estadoActual, nuevoEstado): boolean
        -validarCodigoRetiro(pedido, codigoPresentado): boolean
        -generarCodigoRetiro(): String
    }

    class PedidoRepository {
        <<interface>>
        +guardar(pedido): Pedido
        +buscarPorCodigoRetiro(codigoRetiro): Pedido
        +existeCodigoRetiro(codigoRetiro): boolean
        +buscarTodos(): List~Pedido~
        +buscarPorEstado(estado): List~Pedido~
        +buscarPorCliente(cedulaCliente): List~Pedido~
        +actualizar(pedido): void
    }

    class Pedido {
        -codigoRetiro: String
        -fechaRegistro: Date
        -total: Decimal
        -estado: EstadoPedido
        +calcularTotal(): Decimal
        +agregarDetalle(detalle): void
        +eliminarDetalle(detalle): void
        +cambiarEstado(nuevoEstado): void
        +estaListoParaRetirar(): boolean
        +verificarCodigoRetiro(codigoPresentado): boolean
    }

    class DetallePedido {
        -cantidad: Integer
        -precioUnitario: Decimal
        -subtotal: Decimal
        +calcularSubtotal(): Decimal
        +actualizarCantidad(nuevaCantidad): void
    }

    class EstadoPedido {
        <<enumeration>>
        PENDIENTE
        EN_PREPARACION
        LISTO_PARA_RETIRAR
        ENTREGADO
    }

}

namespace Productos {

    class ProductoService {
        +buscarPorCodigo(codigo): Producto
        +estaDisponible(codigo): boolean
        +obtenerPrecioVigente(codigo): Decimal
    }

    class Producto {
        -codigo: String
        -nombre: String
        -precio: Decimal
        +estaDisponible(): boolean
    }

}

namespace Usuarios {

    class UsuarioService {
        +buscarClientePorCedula(cedula): Cliente
        +validarPersonalActivo(cedula): boolean
    }

    class Cliente {
        -cedula: String
        -nombre: String
        -apellido: String
    }

    class Personal {
        -cedula: String
        -nombre: String
        -apellido: String
    }

}

    PedidoController --> PedidoService : utiliza
    PedidoService --> PedidoRepository : utiliza
    PedidoService ..> ProductoService : consulta
    PedidoService ..> UsuarioService : consulta
    PedidoRepository ..> Pedido : persiste

    Pedido "1" *-- "1..*" DetallePedido : contiene
    Pedido "*" --> "1" Cliente : pertenece a
    Pedido "*" --> "0..1" Personal : procesado por
    Pedido --> EstadoPedido : estado
    DetallePedido "*" --> "1" Producto : referencia

```

## Versión

v1.0

## Trazabilidad

- Issue: #13
- Pull Request: Pendiente

## Observaciones

- Arquitectura MVC:
    - Modelo: Pedido, DetallePedido, EstadoPedido, PedidoService, PedidoRepository.
    - Controlador: PedidoController.
    - Vista: no aparece hasta el prototipo de la interfaz.
- Pedidos depende de Productos y Usuarios, y no al revés (RNF-05).
- Se usan los métodos de ProductoService definidos en DCM.md (buscarPorCodigo, estaDisponible y obtenerPrecioVigente) para validar los productos y calcular el importe (RN-NEG-02, RN-NEG-03, RFP-01, RFP-02).
- El pedido solo se guarda cuando el cliente lo confirma. Se registra en PENDIENTE y con un código de retiro único (RFP-03, RN-NEG-05, RN-NEG-06).
- DetallePedido guarda su propio precioUnitario, así que un cambio de precio en el menú no afecta a los pedidos ya confirmados (RN-NEG-04).
- El cliente consulta el estado de sus pedidos (RFP-04) y el personal consulta todos los pedidos con su código de retiro (RFP-05-1).
- El estado solo avanza en orden: PENDIENTE, EN_PREPARACION, LISTO_PARA_RETIRAR y ENTREGADO, sin saltar ni retroceder (RFP-05-2, RN-NEG-06).
- Para marcar un pedido como ENTREGADO tiene que estar LISTO_PARA_RETIRAR y el cliente debe presentar su código de retiro (RFP-05-2, RN-NEG-07).
- El namespace Usuarios es provisional porque DCU.md todavía no está hecho. Los métodos de UsuarioService deben coincidir con los que se definan en ese módulo.
