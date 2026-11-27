# DN — Diagrama de Navegación

## Versión

v1.1

## Trazabilidad

- Issue: #14
- Pull Request: #21

## Diagrama

```mermaid

stateDiagram-v2
    [*] --> InicioSesion

    InicioSesion --> PanelAdministrador: Autenticación de Administrador exitosa

    InicioSesion --> PanelPersonal: Autenticación de Personal exitosa

    InicioSesion --> PanelCliente: Autenticación de Cliente exitosa

    InicioSesion --> RegistroCliente : Registrarse

    PanelPersonal --> GestionPedidos

    PanelPersonal --> GestionProductos

    PanelCliente --> Menu

    PanelCliente --> Pedido

    Menu --> Pedido

    PanelAdministrador --> RegistroPersonal

    PanelAdministrador --> ActualizacionCredenciales

```

## Observaciones

- Ninguna.