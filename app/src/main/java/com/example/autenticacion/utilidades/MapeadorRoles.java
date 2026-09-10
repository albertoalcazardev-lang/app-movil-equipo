package com.example.autenticacion.utilidades;

import com.example.autenticacion.modelos.RolUsuario;

/**
 * Aplica la regla de negocio escrita en las "Notas adicionales" de la
 * Historia de Usuario US01:
 *
 *   "El mapeo de roles debe programarse en el código base, asignando
 *    a los IDs 1 y 2 el rol de Administrador, el ID 3 para Auditor,
 *    y los IDs restantes como Clientes."
 *
 * Esta clase existe para que esa regla esté escrita en UN SOLO LUGAR.
 * Si el profesor cambia la regla mañana, solo tocamos este archivo.
 */

public final class MapeadorRoles {
    private MapeadorRoles() {
        // Clase de solo-utilidad: no se debe instanciar.
    }

    public static RolUsuario mapearPorId(int idUsuario) {
        if (idUsuario == 1 || idUsuario == 2) {
            return RolUsuario.ADMINISTRADOR;
        }
        if (idUsuario == 3) {
            return RolUsuario.AUDITOR;
        }
        return RolUsuario.CLIENTE;
    }
}
