package com.example.autenticacion.utilidades;
import java.util.ArrayList;
import java.util.List;

/**
 * ATENCIÓN COMPAÑEROS DE EQUIPO (US09 / US10):
 * Esta clase es un "stub" (una versión mínima y temporal). Su único
 * propósito ahora mismo es cumplir el Escenario 3 de la Historia
 * US02: "Todo dato... como... el carrito local, se borra por completo"
 * al cerrar sesión.
 *
 * Cuando implementen el carrito de verdad (US09/US10), reemplacen la
 * lista genérica de abajo por su lista real de productos del carrito,
 * pero CONSERVEN el método vaciarCarrito() con el mismo nombre, porque
 * GestorSesion.cerrarSesion() ya lo está llamando.
 *
 * Es un Singleton (una sola instancia compartida en toda la app) para
 * que "vaciar" realmente afecte al carrito que ve el usuario en pantalla.
 */

public final class GestorCarrito {
    private static GestorCarrito instancia;

    // TODO(US09/US10): sustituir Object por su modelo real "ItemCarrito".
    private final List<Object> productosEnCarrito = new ArrayList<>();

    private GestorCarrito() {
    }

    public static synchronized GestorCarrito obtenerInstancia() {
        if (instancia == null) {
            instancia = new GestorCarrito();
        }
        return instancia;
    }

    public void vaciarCarrito() {
        productosEnCarrito.clear();
    }

    public int contarProductos() {
        return productosEnCarrito.size();
    }
}
