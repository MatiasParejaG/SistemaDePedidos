package Pedidos;

import java.util.ArrayList;
import java.util.List;

public class GestorPedidos {
    private List<Pedido> pedidos;

    public GestorPedidos() {
        this.pedidos = new ArrayList<>();
    }

    public Pedido crearPedido(String cliente, List<String> productos) {
        Pedido nuevoPedido = new Pedido(cliente, productos);
        pedidos.add(nuevoPedido);
        return nuevoPedido;
    }

    public Pedido obtenerPedido(int id) {
        return pedidos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public void actualizarEstadoPedido(int id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPedido(id);
        if (pedido != null) {
            pedido.setEstado(nuevoEstado);
        }
    }

    public List<Pedido> listarPedidos() {
        return pedidos;
    }
}