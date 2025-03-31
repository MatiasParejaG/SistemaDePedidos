package Pedidos;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private static int contador = 1;
    private int id;
    private String cliente;
    private List<String> productos;
    private EstadoPedido estado;

    public Pedido(String cliente, List<String> productos) {
        this.id = contador++;
        this.cliente = cliente;
        this.productos = new ArrayList<>(productos);
        this.estado = EstadoPedido.PENDIENTE;
    }

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public List<String> getProductos() {
        return productos;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pedido ID: " + id + ", Cliente: " + cliente + ", Productos: " + productos + ", Estado: " + estado;
    }
}
