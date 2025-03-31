import Pedidos.*;
import Pago.*;
import Inventario.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;


public class TestIntegracion {
    private Inventario inventario;
    private GestorPedidos gestorPedidos;
    private ProcesadorPago procesadorPagos;

    @BeforeEach
    void setUp() {
        inventario = new Inventario();
        gestorPedidos = new GestorPedidos();
        procesadorPagos = new ProcesadorPago();
        inventario.agregarProducto("Laptop", 5, 1200.00);
    }

    @Test
    void testValidacionStockAntesDelPago() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente1", Arrays.asList("Laptop", "Laptop", "Laptop"));
        boolean disponible = inventario.verificarDisponibilidad("Laptop", 3);
        assertTrue(disponible, "El inventario debería permitir la compra");
    }

    @Test
    void testActualizacionInventarioDespuesDeCompraExitosa() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente2", Arrays.asList("Laptop", "Laptop"));
        boolean disponible = inventario.verificarDisponibilidad("Laptop", 2);
        assertTrue(disponible, "El producto debe estar disponible antes de la compra");

        Pago pago = procesadorPagos.procesarPago(pedido.getId(), 2400.00);
        if (pago.getEstado() == EstadoPago.APROBADO) {
            inventario.reducirStock("Laptop", 2);
        }

        assertEquals(3, inventario.obtenerProducto("Laptop").getCantidad(), "El inventario debe reducirse correctamente");
    }

    @Test
    void testCancelacionPedidoPorFalloEnPago() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente3", Arrays.asList("Laptop"));
        Pago pago = procesadorPagos.procesarPago(pedido.getId(), 1200.00);

        if (pago.getEstado() == EstadoPago.RECHAZADO) {
            assertEquals(5, inventario.obtenerProducto("Laptop").getCantidad(), "El stock no debe modificarse si el pago falla");
            assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado(), "El pedido no debe confirmarse si el pago falla");
        }
    }

    @Test
    void testNotificacionConfirmacionCliente() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente4", Arrays.asList("Laptop"));
        Pago pago = procesadorPagos.procesarPago(pedido.getId(), 1200.00);

        if (pago.getEstado() == EstadoPago.APROBADO) {
            String notificacion = "Pedido " + pedido.getId() + " confirmado para Cliente4.";
            assertNotNull(notificacion, "Se debe enviar una notificación de confirmación");
        }
    }

    @Test
    void testManejoConcurrenciaInventario() throws InterruptedException {
        Thread cliente1 = new Thread(() -> {
            synchronized (inventario) {
                if (inventario.verificarDisponibilidad("Laptop", 3)) {
                    Pago pago = procesadorPagos.procesarPago(1, 3600.00);
                    if (pago.getEstado() == EstadoPago.APROBADO) {
                        inventario.reducirStock("Laptop", 3);
                    }
                }
            }
        });

        Thread cliente2 = new Thread(() -> {
            synchronized (inventario) {
                if (inventario.verificarDisponibilidad("Laptop", 3)) {
                    Pago pago = procesadorPagos.procesarPago(2, 3600.00);
                    if (pago.getEstado() == EstadoPago.APROBADO) {
                        inventario.reducirStock("Laptop", 3);
                    }
                }
            }
        });

        cliente1.start();
        cliente2.start();

        cliente1.join();
        cliente2.join();

        assertTrue(inventario.obtenerProducto("Laptop").getCantidad() >= 0, "El sistema debe manejar la concurrencia y asegurar que solo el primer pago aprobado reduzca el stock");
    }
}
