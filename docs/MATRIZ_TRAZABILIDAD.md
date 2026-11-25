# Matriz de Trazabilidad

| Requisito | Estado | Versión | Módulo | Origen | \#Issue | \#Pull Request | Diagrama | Endpoint | Caso(s) de Prueba |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| [RFU-01](./requisitos/funcionales/usuarios/RFU-01.md) | Activo | v1.1 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCU](./modelado/usuarios/DCU.md) | POST /api/usuarios/clientes | Pendiente |
| [RFU-02](./requisitos/funcionales/usuarios/RFU-02.md) | Activo | v2.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCU](./modelado/usuarios/DCU.md) | POST /api/auth/cliente | Pendiente |
| RFU-03 | Eliminado | v1.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | | | |
| [RFU-04](./requisitos/funcionales/usuarios/RFU-04.md) | Activo | v2.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCU](./modelado/usuarios/DCU.md) | POST /api/auth/personal | Pendiente |
| [RFU-05](./requisitos/funcionales/usuarios/RFU-05.md) | Activo | v1.1 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCU](./modelado/usuarios/DCU.md) | POST /api/usuarios/personal | Pendiente |
| [RFU-06](./requisitos/funcionales/usuarios/RFU-01.md) | Activo | v1.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCU](./modelado/usuarios/DCU.md) | POST /api/auth/administrador/clave | Pendiente |
| [RFM-01](./requisitos/funcionales/productos/RFM-01.md) | Activo | v1.1 | Módulo de Productos | RU-01 | 2 | 8 | [DCM](./modelado/productos/DCM.md) | GET /api/productos/menu | Pendiente |
| [RFM-02](./requisitos/funcionales/productos/RFM-02.md) | Activo | v1.0 | Módulo de Productos | RU-06 | 2 | 8 | [DCM](./modelado/productos/DCM.md) | POST /api/productos/ | Pendiente |
| RFM-03 | Dividido | v1.0 | Módulo de Productos | RU-06 | 2 | 8 | | | |
| [RFM-03-1](./requisitos/funcionales/productos/RFM-03-1.md) | Activo | v1.1 | Módulo de Productos | RU-06 | 2 | 8 | [DCM](./modelado/productos/DCM.md) | PATCH /api/productos/{codigo}/precio | Pendiente |
| [RFM-03-2](./requisitos/funcionales/productos/RFM-03-2.md) | Activo | v1.1 | Módulo de Productos | RU-06 | 2 | 8 | [DCM](./modelado/productos/DCM.md) |  PATCH /api/productos/{codigo}/disponibilidad | Pendiente |
| [RFP-01](./requisitos/funcionales/pedidos/RFP-01.md) | Activo | v1.0 | Módulo de Pedidos | RU-02 | 4 | 7 | [DCP](./modelado/pedidos/DCP.md) | POST /api/pedidos/seleccion | Pendiente |
| [RFP-02](./requisitos/funcionales/pedidos/RFP-02.md) | Activo | v1.1 | Módulo de Pedidos | RU-03 | 4 | 7 | [DCP](./modelado/pedidos/DCP.md) | POST /api/pedidos/total | Pendiente |
| [RFP-03](./requisitos/funcionales/pedidos/RFP-03.md) | Activo | v1.1 | Módulo de Pedidos | RU-04 | 4 | 7 | [DCP](./modelado/pedidos/DCP.md) | POST /api/pedidos | Pendiente |
| [RFP-04](./requisitos/funcionales/pedidos/RFP-04.md) | Activo | v1.0 | Módulo de Pedidos | RU-05 | 4 | 7 | [DCP](./modelado/pedidos/DCP.md) | GET /api/pedidos/{codigoRetiro}/estado | Pendiente |
| RFU-05 | Dividido | v1.0 | Módulo de Pedidos | RU-07 | 4 | 7 | | | |
| [RFP-05-1](./requisitos/funcionales/pedidos/RFP-05-1.md) | Activo | v1.1 | Módulo de Pedidos | RU-07 | 4 | 7 | [DCP](./modelado/pedidos/DCP.md) | GET /api/pedidos | Pendiente |
| [RFP-05-2](./requisitos/funcionales/pedidos/RFP-05-2.md) | Activo | v1.1 | Módulo de Pedidos | RU-07 | 4 | 7 | [DCP](./modelado/pedidos/DCP.md) | POST /api/pedidos/{codigoRetiro}/avanzar | Pendiente |
| [RNF-01](./requisitos/nofuncionales/RNF-01.md) | Activo | v1.0 | Módulo de Productos | Equipo 6 | 3 | 6 | N/A | N/A | Pendiente |
| [RNF-02](./requisitos/nofuncionales/RNF-02.md) | Activo | v1.0 | Módulo de Pedidos | Equipo 6 | 3 | 6 | N/A | N/A | Pendiente |
| [RNF-03](./requisitos/nofuncionales/RNF-03.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | Pendiente |
| [RNF-04](./requisitos/nofuncionales/RNF-04.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | Pendiente |
| [RNF-05](./requisitos/nofuncionales/RNF-05.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | Pendiente |
| [RNF-06](./requisitos/nofuncionales/RNF-06.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | Pendiente |