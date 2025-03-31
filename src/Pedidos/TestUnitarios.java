package Pedidos;

import Pedidos.*;
import Pago.*;
import Inventario.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

public class TestUnitarios {
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

    // Prueba de agregar un producto al inventario
    @Test
    void testAgregarProducto() {
        inventario.agregarProducto("Mouse", 10, 25.50);
        Producto mouse = inventario.obtenerProducto("Mouse");
        assertNotNull(mouse, "El producto Mouse debe existir en el inventario");
        assertEquals(10, mouse.getCantidad(), "La cantidad del producto Mouse debe ser 10");
    }

    // Prueba de reducción de stock
    @Test
    void testReducirStock() {
        inventario.reducirStock("Laptop", 2);
        assertEquals(3, inventario.obtenerProducto("Laptop").getCantidad(), "El stock de Laptop debe reducirse a 3");
    }

    // Prueba de verificar disponibilidad de stock
    @Test
    void testVerificarDisponibilidad() {
        assertTrue(inventario.verificarDisponibilidad("Laptop", 2), "Debe haber suficiente stock de Laptop");
        assertFalse(inventario.verificarDisponibilidad("Laptop", 6), "No debe haber suficiente stock de Laptop para 6 unidades");
    }

    // Prueba de creación de un pedido
    @Test
    void testCrearPedido() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente1", Arrays.asList("Laptop"));
        assertNotNull(pedido, "El pedido no debe ser null");
        assertEquals("Cliente1", pedido.getCliente(), "El cliente debe ser Cliente1");
    }

    // Prueba de obtención de un pedido por su ID
    @Test
    void testObtenerPedidoPorId() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente2", Arrays.asList("Laptop"));
        Pedido pedidoObtenido = gestorPedidos.obtenerPedido(pedido.getId());
        assertNotNull(pedidoObtenido, "El pedido debe existir en el sistema");
        assertEquals(pedido.getId(), pedidoObtenido.getId(), "Los IDs deben coincidir");
    }

    // Prueba de procesamiento de un pago exitoso
    @Test
    void testPagoExitoso() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente3", Arrays.asList("Laptop"));
        Pago pago = procesadorPagos.procesarPago(pedido.getId(), 1200.00);
        assertNotNull(pago, "El pago debe ser registrado");
        assertTrue(pago.getEstado() == EstadoPago.APROBADO || pago.getEstado() == EstadoPago.RECHAZADO, "El estado del pago debe ser APROBADO o RECHAZADO");
    }

    // Prueba de pago rechazado no afecta el inventario
    @Test
    void testPagoRechazadoNoReduceStock() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente4", Arrays.asList("Laptop"));
        Pago pago = new Pago(pedido.getId(), 1200.00);
        pago.setEstado(EstadoPago.RECHAZADO);

        int stockAntes = inventario.obtenerProducto("Laptop").getCantidad();
        if (pago.getEstado() == EstadoPago.APROBADO) {
            inventario.reducirStock("Laptop", 1);
        }
        assertEquals(stockAntes, inventario.obtenerProducto("Laptop").getCantidad(), "El stock no debe cambiar si el pago es rechazado");
    }

    // Prueba de actualización del estado del pedido
    @Test
    void testActualizarEstadoPedido() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente5", Arrays.asList("Laptop"));
        gestorPedidos.actualizarEstadoPedido(pedido.getId(), EstadoPedido.COMPLETADO);
        assertEquals(EstadoPedido.COMPLETADO, pedido.getEstado(), "El estado del pedido debe cambiar a COMPLETADO");
    }

    // Prueba de almacenamiento de pagos en el sistema
    @Test
    void testAlmacenarPagos() {
        Pago pago = procesadorPagos.procesarPago(1, 1200.00);
        Pago pagoObtenido = procesadorPagos.obtenerPago(pago.getId());
        assertNotNull(pagoObtenido, "El pago debe estar registrado en el sistema");
        assertEquals(pago.getId(), pagoObtenido.getId(), "Los IDs de pago deben coincidir");
    }

    // Prueba de intento de compra con stock insuficiente
    @Test
    void testIntentoCompraSinStock() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente7", Arrays.asList("Laptop", "Laptop", "Laptop", "Laptop", "Laptop", "Laptop"));
        boolean disponible = inventario.verificarDisponibilidad("Laptop", 6);
        assertFalse(disponible, "No debe permitir comprar más productos de los disponibles en el stock");
    }

    // Prueba de doble reducción de stock
    @Test
    void testDobleReduccionStock() {
        inventario.reducirStock("Laptop", 2);
        inventario.reducirStock("Laptop", 2);
        assertEquals(1, inventario.obtenerProducto("Laptop").getCantidad(), "El stock debe actualizarse correctamente después de dos reducciones");
    }

    // Prueba de creación de pedido vacío
    @Test
    void testCrearPedidoVacio() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente10", Arrays.asList());
        assertEquals(0, pedido.getProductos().size(), "El pedido no debe contener productos si la lista está vacía");
    }

    // Prueba de cancelación de un pedido
    @Test
    void testCancelarPedido() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente11", Arrays.asList("Laptop"));
        pedido.setEstado(EstadoPedido.CANCELADO);
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado(), "El pedido debe marcarse como cancelado correctamente");
    }

    // Prueba de que un pago aprobado reduce el stock
    @Test
    void testPagoAprobadoReduceStock() {
        Pedido pedido = gestorPedidos.crearPedido("Cliente12", Arrays.asList("Laptop"));
        Pago pago = procesadorPagos.procesarPago(pedido.getId(), 1200.00);

        if (pago.getEstado() == EstadoPago.APROBADO) {
            inventario.reducirStock("Laptop", 1);
        }

        assertTrue(inventario.obtenerProducto("Laptop").getCantidad() <= 4, "El stock debe disminuir si el pago es aprobado");
    }
}
