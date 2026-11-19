package ec.edu.cafeteria.pedidos.dto;

import java.util.List;

public record SeleccionPedidoRequest(List<SeleccionProductoRequest> items) {
}
