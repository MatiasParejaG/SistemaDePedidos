package Pago;

public class Pago {
    private static int contador = 1;
    private int id;
    private int idPedido;
    private double monto;
    private EstadoPago estado;

    public Pago(int idPedido, double monto) {
        this.id = contador++;
        this.idPedido = idPedido;
        this.monto = monto;
        this.estado = EstadoPago.PENDIENTE;
    }

    public int getId() {
        return id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public double getMonto() {
        return monto;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pago ID: " + id + ", Pedido ID: " + idPedido + ", Monto: " + monto + ", Estado: " + estado;
    }
}