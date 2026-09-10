package com.example.autenticacion.red;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * IMPORTANTE PARA PRINCIPIANTES - Patrón "Singleton":
 * Crear una conexión Retrofit es "costoso" (configura tiempos de espera,
 * interceptores, etc.), así que NO queremos crear una nueva cada vez que
 * alguien la necesite. En su lugar, la creamos UNA sola vez y la
 * reutilizamos siempre. Por eso el constructor es privado y solo se
 * accede mediante el método estático obtenerServicio().
 *
 * Todo el equipo (las 12 historias de usuario) debe usar esta MISMA
 * clase para consumir la API, así garantizamos que todos apuntamos a
 * la misma URL base y con la misma configuración.
 */

public final class ClienteRetrofit {
    private static final String URL_BASE = "https://fakestoreapi.com/";

    // "volatile" + comprobación doble evita crear el objeto dos veces
    // si dos pantallas lo piden casi al mismo tiempo.
    private static volatile ApiService instancia;

    private ClienteRetrofit() {
        // Constructor privado: nadie puede escribir "new ClienteRetrofit()"
    }

    public static ApiService obtenerServicio() {
        if (instancia == null) {
            synchronized (ClienteRetrofit.class) {
                if (instancia == null) {
                    instancia = construirRetrofit().create(ApiService.class);
                }
            }
        }
        return instancia;
    }

    private static Retrofit construirRetrofit() {
        // El interceptor de logging imprime en Logcat cada petición y
        // respuesta HTTP. Es MUY útil mientras programamos y depuramos
        // errores (verás exactamente qué manda y qué responde la API).
        HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
        interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient clienteHttp = new OkHttpClient.Builder()
                .addInterceptor(interceptorLog)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(clienteHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
