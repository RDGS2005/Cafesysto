package ec.edu.cafeteria.pedidos.dto;

import ec.edu.cafeteria.pedidos.entidad.EstadoPedido;

public record EstadoPedidoResponse(String codigoRetiro, EstadoPedido estado) {
}
