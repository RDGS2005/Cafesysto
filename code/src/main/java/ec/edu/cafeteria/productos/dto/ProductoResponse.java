package ec.edu.cafeteria.productos.dto;

import ec.edu.cafeteria.productos.entidad.EstadoProducto;

import java.math.BigDecimal;

public record ProductoResponse(String codigo, String nombre, BigDecimal precio, EstadoProducto estado) {
}
