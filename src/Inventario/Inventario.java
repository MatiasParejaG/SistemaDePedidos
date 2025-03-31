package Inventario;

import java.util.HashMap;
import java.util.Map;

public class Inventario {
    private Map<String, Producto> productos;

    public Inventario() {
        this.productos = new HashMap<>();
    }

    public void agregarProducto(String nombre, int cantidad, double precio) {
        productos.put(nombre, new Producto(nombre, cantidad, precio));
    }

    public boolean verificarDisponibilidad(String nombre, int cantidad) {
        Producto producto = productos.get(nombre);
        return producto != null && producto.getCantidad() >= cantidad;
    }

    public boolean reducirStock(String nombre, int cantidad) {
        if (verificarDisponibilidad(nombre, cantidad)) {
            Producto producto = productos.get(nombre);
            producto.setCantidad(producto.getCantidad() - cantidad);
            return true;
        }
        return false;
    }

    public Producto obtenerProducto(String nombre) {
        return productos.get(nombre);
    }

    public void listarInventario() {
        productos.values().forEach(System.out::println);
    }
}
