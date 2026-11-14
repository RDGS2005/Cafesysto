# DN — Diagrama de Navegación

## Diagrama

```mermaid

stateDiagram-v2
    [*] --> InicioSesion

    InicioSesion --> PanelAdministrador: Autenticación de Administrador exitosa

    InicioSesion --> PanelPersonal: Autenticación de Personal exitosa

    InicioSesion --> PanelCliente: Autenticación de Cliente exitosa

    InicioSesion --> Registro : Registrarse

    PanelPersonal --> GestionPedidos

    PanelPersonal --> GestionProductos

    PanelCliente --> Menu

    PanelCliente --> DetallePedido

    Menu --> DetallePedido

    PanelAdministrador --> RegistrarPersonal

    PanelAdministrador --> ActualizacionCredenciales

```

## Versión

v1.0

## Trazabilidad

- Issue: #14
- Pull Request: Pendiente

## Observaciones

Ninguna.