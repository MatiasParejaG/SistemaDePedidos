package Pago;

import java.util.HashMap;
import java.util.Map;

public class ProcesadorPago {
    private Map<Integer, Pago> pagos;

    public ProcesadorPago() {
        this.pagos = new HashMap<>();
    }

    public Pago procesarPago(int idPedido, double monto) {
        Pago pago = new Pago(idPedido, monto);
        boolean aprobado = Math.random() > 0.2;
        pago.setEstado(aprobado ? EstadoPago.APROBADO : EstadoPago.RECHAZADO);
        pagos.put(pago.getId(), pago);
        return pago;
    }

    public Pago obtenerPago(int id) {
        return pagos.get(id);
    }
}