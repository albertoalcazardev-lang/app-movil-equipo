package com.example.autenticacion.modelos;

/**
 * Representa los 3 perfiles (roles) que puede tener un usuario dentro
 * de la app, según la regla de negocio de la Historia de Usuario US01:
 *
 *   - IDs 1 y 2   -> ADMINISTRADOR
 *   - ID 3        -> AUDITOR
 *   - Resto de IDs -> CLIENTE
 *
 * Usamos un "enum" (enumeración) porque el rol solo puede tener uno de
 * estos 3 valores exactos; así evitamos errores de escritura como
 * comparar Strings ("Administrador" vs "administrador").
 */

public enum RolUsuario {
    ADMINISTRADOR,
    CLIENTE,
    AUDITOR
}
