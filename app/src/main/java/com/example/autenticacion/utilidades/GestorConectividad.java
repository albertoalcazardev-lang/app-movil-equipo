package com.example.autenticacion.utilidades;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

/**
 * US01 - Escenario 3: "Manejo de conectividad de red".
 *
 * Esta clase responde una sola pregunta: ¿el celular tiene internet
 * ahora mismo (WiFi o datos móviles)? La usamos ANTES de llamar a la
 * API para poder detener la ejecución sin ni siquiera intentar la
 * petición, tal como pide la historia:
 *
 *   "La aplicación detiene la ejecución antes de consumir la API y
 *    alerta al usuario sobre la falta de conexión."
 */

public final class GestorConectividad {
    private GestorConectividad() {
    }

    public static boolean hayConexionInternet(Context contexto) {
        ConnectivityManager administrador =
                (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (administrador == null) {
            return false;
        }

        NetworkCapabilities capacidades =
                administrador.getNetworkCapabilities(administrador.getActiveNetwork());

        if (capacidades == null) {
            return false;
        }

        // Preguntamos si la red activa tiene salida real a internet,
        // ya sea por WiFi, datos móviles o ethernet.
        return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && (capacidades.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capacidades.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capacidades.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }
}
