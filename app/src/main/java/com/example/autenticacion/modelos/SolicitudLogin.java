package com.example.autenticacion.modelos;
import com.google.gson.annotations.SerializedName;

/**
 * Representa el "cuerpo" (body) que ENVIAMOS a la API cuando el
 * usuario presiona "Iniciar sesión".
 *
 * FakeStoreAPI espera exactamente este JSON en POST /auth/login:
 *   {
 *     "username": "mor_2314",
 *     "password": "83r5^_"
 *   }
 *
 * Retrofit + Gson convierten automáticamente este objeto Java en ese
 * JSON, así que nosotros solo trabajamos con un objeto normal de Java.
 */

public class SolicitudLogin {
    @SerializedName("username")
    private final String nombreUsuario;

    @SerializedName("password")
    private final String contrasena;

    public SolicitudLogin(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }
}
