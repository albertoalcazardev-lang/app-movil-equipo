package com.example.autenticacion.modelos;
import com.google.gson.annotations.SerializedName;
/**
 * Representa la respuesta exitosa de POST /auth/login:
 *   { "token": "eyJhbGciOiJI..." }
 *
 * Si las credenciales son correctas, la API responde con código 200
 * y este JSON. Nosotros guardamos ese token para futuras peticiones
 * (por ejemplo, cuando otras historias de usuario necesiten enviar
 * "Authorization: Bearer <token>").
 */
public class RespuestaLogin {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
