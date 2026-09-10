package com.example.autenticacion.red;

import com.example.autenticacion.modelos.RespuestaLogin;
import com.example.autenticacion.modelos.SolicitudLogin;
import com.example.autenticacion.modelos.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * IMPORTANTE PARA PRINCIPIANTES:
 * Esta es una INTERFAZ, no una clase. En Java, una interfaz solo
 * declara "qué métodos existen", pero no dice "cómo funcionan por
 * dentro". Retrofit lee esta interfaz y genera automáticamente el
 * código real que habla con internet (HTTP). Nosotros solo describimos
 * la URL y el tipo de dato que esperamos recibir.
 *
 * Todas las historias de usuario del equipo (US01 a US12) deben agregar
 * aquí el método que necesiten, para que exista UN SOLO archivo de
 * endpoints compartido por todo el proyecto.
 */

public interface ApiService {
    /**
     * US01 - Escenario 1 y 2: iniciar sesión.
     * Equivale a: POST https://fakestoreapi.com/auth/login
     */
    @POST("auth/login")
    Call<RespuestaLogin> iniciarSesion(@Body SolicitudLogin solicitud);

    /**
     * US01 - Necesitamos la lista de usuarios para encontrar el ID
     * de quien inició sesión (FakeStoreAPI no regresa el ID en el
     * login, así que lo buscamos por "username" en esta lista).
     * Equivale a: GET https://fakestoreapi.com/users
     */
    @GET("users")
    Call<List<Usuario>> obtenerUsuarios();
}
