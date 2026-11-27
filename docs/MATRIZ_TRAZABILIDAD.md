# Matriz de Trazabilidad

| Requisito | Estado | Versión | Módulo | Origen | \#Issue | \#Pull Request | Diagrama | Endpoint | Caso(s) de Prueba |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| [RFU-01](./requisitos/funcionales/usuarios/RFU-01.md) | Activo | v1.1 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/usuarios/clientes | [CPU-01](./pruebas/funcionales/usuarios/CPU-01.md) |
| [RFU-02](./requisitos/funcionales/usuarios/RFU-02.md) | Activo | v2.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/auth/cliente | [CPU-02](./pruebas/funcionales/usuarios/CPU-02.md) |
| RFU-03 | Eliminado | v1.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | | | |
| [RFU-04](./requisitos/funcionales/usuarios/RFU-04.md) | Activo | v2.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/auth/personal | [CPU-05](./pruebas/funcionales/usuarios/CPU-05.md) |
| [RFU-05](./requisitos/funcionales/usuarios/RFU-05.md) | Activo | v1.1 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/usuarios/personal | [CPU-04](./pruebas/funcionales/usuarios/CPU-04.md) |
| [RFU-06](./requisitos/funcionales/usuarios/RFU-06.md) | Activo | v1.0 | Módulo de Usuarios | Equipo 6 | 1 | 9 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/auth/administrador/clave | [CPU-03](./pruebas/funcionales/usuarios/CPU-03.md) |
| [RFM-01](./requisitos/funcionales/productos/RFM-01.md) | Activo | v1.1 | Módulo de Productos | RU-01 | 2 | 8 | [DCG](./modelado/DIAGRAMA_CLASES.md) | GET /api/productos/menu | [CPM-02](./pruebas/funcionales/productos/CPM-02.md) |
| [RFM-02](./requisitos/funcionales/productos/RFM-02.md) | Activo | v1.0 | Módulo de Productos | RU-06 | 2 | 8 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/productos | [CPM-01](./pruebas/funcionales/productos/CPM-01.md) |
| RFM-03 | Dividido | v1.0 | Módulo de Productos | RU-06 | 2 | 8 | | | |
| [RFM-03-1](./requisitos/funcionales/productos/RFM-03-1.md) | Activo | v1.1 | Módulo de Productos | RU-06 | 2 | 8 | [DCG](./modelado/DIAGRAMA_CLASES.md) | PATCH /api/productos/{codigo}/precio | [CPM-03](./pruebas/funcionales/productos/CPM-03.md) |
| [RFM-03-2](./requisitos/funcionales/productos/RFM-03-2.md) | Activo | v1.1 | Módulo de Productos | RU-06 | 2 | 8 | [DCG](./modelado/DIAGRAMA_CLASES.md) |  PATCH /api/productos/{codigo}/disponibilidad | [CPM-04](./pruebas/funcionales/productos/CPM-04.md) |
| [RFP-01](./requisitos/funcionales/pedidos/RFP-01.md) | Activo | v1.0 | Módulo de Pedidos | RU-02 | 4 | 7 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/pedidos/seleccion | [CPP-01](./pruebas/funcionales/pedidos/CPP-01.md) |
| [RFP-02](./requisitos/funcionales/pedidos/RFP-02.md) | Activo | v1.1 | Módulo de Pedidos | RU-03 | 4 | 7 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/pedidos/total | [CPP-02](./pruebas/funcionales/pedidos/CPP-02.md) |
| [RFP-03](./requisitos/funcionales/pedidos/RFP-03.md) | Activo | v1.1 | Módulo de Pedidos | RU-04 | 4 | 7 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/pedidos | [CPP-03](./pruebas/funcionales/pedidos/CPP-03.md) |
| [RFP-04](./requisitos/funcionales/pedidos/RFP-04.md) | Activo | v1.0 | Módulo de Pedidos | RU-05 | 4 | 7 | [DCG](./modelado/DIAGRAMA_CLASES.md) | GET /api/pedidos/{codigoRetiro}/estado | [CPP-04](./pruebas/funcionales/pedidos/CPP-04.md) |
| RFP-05 | Dividido | v1.0 | Módulo de Pedidos | RU-07 | 4 | 7 | | | |
| [RFP-05-1](./requisitos/funcionales/pedidos/RFP-05-1.md) | Activo | v1.1 | Módulo de Pedidos | RU-07 | 4 | 7 | [DCG](./modelado/DIAGRAMA_CLASES.md) | GET /api/pedidos | [CPP-05](./pruebas/funcionales/pedidos/CPP-05.md) |
| [RFP-05-2](./requisitos/funcionales/pedidos/RFP-05-2.md) | Activo | v1.1 | Módulo de Pedidos | RU-07 | 4 | 7 | [DCG](./modelado/DIAGRAMA_CLASES.md) | POST /api/pedidos/{codigoRetiro}/avanzar | [CPP-06](./pruebas/funcionales/pedidos/CPP-06.md) |
| [RNF-01](./requisitos/nofuncionales/RNF-01.md) | Activo | v1.0 | Módulo de Productos | Equipo 6 | 3 | 6 | N/A | N/A | [CPNF-01](./pruebas/nofuncionales/CPNF-01.md) |
| [RNF-02](./requisitos/nofuncionales/RNF-02.md) | Activo | v1.0 | Módulo de Pedidos | Equipo 6 | 3 | 6 | N/A | N/A | [CPNF-02](./pruebas/nofuncionales/CPNF-02.md) |
| [RNF-03](./requisitos/nofuncionales/RNF-03.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | [CPNF-03](./pruebas/nofuncionales/CPNF-03.md) |
| [RNF-04](./requisitos/nofuncionales/RNF-04.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | [CPNF-03](./pruebas/nofuncionales/CPNF-03.md) |
| [RNF-05](./requisitos/nofuncionales/RNF-05.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | [CPNF-04](./pruebas/nofuncionales/CPNF-04.md) |
| [RNF-06](./requisitos/nofuncionales/RNF-06.md) | Activo | v1.0 | Todos | Equipo 6 | 3 | 6 | N/A | N/A | [CPNF-04](./pruebas/nofuncionales/CPNF-04.md) |