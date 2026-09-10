package com.example.autenticacion.utilidades;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.autenticacion.modelos.RolUsuario;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * ======================================================================
 *  CLASE CLAVE PARA LAS DOS HISTORIAS QUE TE TOCARON (US01 y US02)
 * ======================================================================
 *
 * US01 dice: "El token debe persistirse usando las herramientas de
 * almacenamiento seguro nativas del dispositivo."
 *
 * US02 dice: "Tienen que borrar el token y el rol directamente del
 * almacenamiento del dispositivo (SharedPreferences en Android...),
 * no solo limpiar las variables temporales del código."
 *
 * Por eso NO usamos SharedPreferences normal (que guarda texto plano,
 * legible por cualquiera que tenga el celular "rooteado"). Usamos
 * EncryptedSharedPreferences, una librería oficial de Android Jetpack
 * que cifra automáticamente tanto las claves como los valores antes
 * de guardarlos en el disco. Para quien lee el código, se usa EXACTAMENTE
 * igual que un SharedPreferences normal (.getString, .putString, etc.).
 *
 * Este archivo es el ÚNICO lugar de todo el proyecto que debe leer o
 * escribir la sesión. Si otra historia de usuario (por ejemplo US11,
 * que necesita saber el rol para decidir qué mostrar) necesita esos
 * datos, debe llamar a los métodos de esta clase, nunca acceder al
 * SharedPreferences directamente.
 */
public final class GestorSesion {
    private static final String ARCHIVO_PREFERENCIAS = "sesion_segura_novatienda";

    private static final String CLAVE_TOKEN = "clave_token";
    private static final String CLAVE_ID_USUARIO = "clave_id_usuario";
    private static final String CLAVE_NOMBRE_USUARIO = "clave_nombre_usuario";
    private static final String CLAVE_NOMBRE_COMPLETO = "clave_nombre_completo";
    private static final String CLAVE_ROL = "clave_rol";

    private static final String ETIQUETA_LOG = "GestorSesion";

    private final SharedPreferences preferenciasCifradas;

    // ---------- Patrón Singleton (una sola instancia compartida) ----------
    private static volatile GestorSesion instancia;

    public static GestorSesion obtenerInstancia(Context contexto) {
        if (instancia == null) {
            synchronized (GestorSesion.class) {
                if (instancia == null) {
                    instancia = new GestorSesion(contexto.getApplicationContext());
                }
            }
        }
        return instancia;
    }

    private GestorSesion(Context contextoApp) {
        this.preferenciasCifradas = crearAlmacenamientoCifrado(contextoApp);
    }

    /**
     * Construye el SharedPreferences cifrado. Si por alguna razón el
     * cifrado falla (muy raro, solo en dispositivos con problemas de
     * Keystore), caemos de vuelta a un SharedPreferences normal para
     * que la app no se cierre inesperadamente, y lo anotamos en Logcat.
     */
    private SharedPreferences crearAlmacenamientoCifrado(Context contextoApp) {
        try {
            MasterKey llaveMaestra = new MasterKey.Builder(contextoApp)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    contextoApp,
                    ARCHIVO_PREFERENCIAS,
                    llaveMaestra,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException excepcion) {
            Log.e(ETIQUETA_LOG, "No se pudo crear almacenamiento cifrado, se usa uno normal", excepcion);
            return contextoApp.getSharedPreferences(ARCHIVO_PREFERENCIAS, Context.MODE_PRIVATE);
        }
    }

    // ======================================================================
    // US01 - Guardar la sesión después de un login exitoso (Escenario 1)
    // ======================================================================
    public void guardarSesion(String token, int idUsuario, String nombreUsuario,
                              String nombreCompleto, RolUsuario rol) {
        preferenciasCifradas.edit()
                .putString(CLAVE_TOKEN, token)
                .putInt(CLAVE_ID_USUARIO, idUsuario)
                .putString(CLAVE_NOMBRE_USUARIO, nombreUsuario)
                .putString(CLAVE_NOMBRE_COMPLETO, nombreCompleto)
                .putString(CLAVE_ROL, rol.name())
                .apply(); // apply() guarda en segundo plano, sin trabar la pantalla
    }

    public boolean haySesionActiva() {
        return preferenciasCifradas.getString(CLAVE_TOKEN, null) != null;
    }

    public String obtenerToken() {
        return preferenciasCifradas.getString(CLAVE_TOKEN, null);
    }

    public int obtenerIdUsuario() {
        return preferenciasCifradas.getInt(CLAVE_ID_USUARIO, -1);
    }

    public String obtenerNombreUsuario() {
        return preferenciasCifradas.getString(CLAVE_NOMBRE_USUARIO, "");
    }

    public String obtenerNombreCompleto() {
        return preferenciasCifradas.getString(CLAVE_NOMBRE_COMPLETO, "");
    }

    public RolUsuario obtenerRol() {
        String rolGuardado = preferenciasCifradas.getString(CLAVE_ROL, RolUsuario.CLIENTE.name());
        return RolUsuario.valueOf(rolGuardado);
    }

    // ======================================================================
    // US02 - Cierre de sesión y limpieza de credenciales
    // ======================================================================

    /**
     * Escenario 1 (Cierre de sesión exitoso) + Escenario 3 (Limpieza
     * profunda de memoria):
     *   - Borra el token, el id, el username, el nombre y el rol del
     *     almacenamiento cifrado del dispositivo (no solo variables).
     *   - Además vacía el carrito local (GestorCarrito), para que el
     *     siguiente usuario que use el celular no vea productos ajenos.
     */
    public void cerrarSesion() {
        preferenciasCifradas.edit().clear().apply();
        GestorCarrito.obtenerInstancia().vaciarCarrito();
    }
}
