package com.example.autenticacion.pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autenticacion.R;
import com.example.autenticacion.modelos.RespuestaLogin;
import com.example.autenticacion.modelos.RolUsuario;
import com.example.autenticacion.modelos.SolicitudLogin;
import com.example.autenticacion.modelos.Usuario;
import com.example.autenticacion.red.ApiService;
import com.example.autenticacion.red.ClienteRetrofit;
import com.example.autenticacion.utilidades.GestorConectividad;
import com.example.autenticacion.utilidades.GestorSesion;
import com.example.autenticacion.utilidades.MapeadorRoles;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * =======================================================================
 *  HISTORIA DE USUARIO 01: "Login y asignación local de perfiles"
 * =======================================================================
 * Esta pantalla es el PUNTO DE ENTRADA de toda la app (es la primera
 * que se ve, así lo declaramos en AndroidManifest.xml con
 * <intent-filter> ... MAIN / LAUNCHER).
 *
 * Cubre los 3 escenarios de aceptación de US01:
 *   Escenario 1: Autenticación exitosa y mapeo de rol.
 *   Escenario 2: Credenciales incorrectas (401).
 *   Escenario 3: Manejo de conectividad de red (sin internet).
 *
 * También cumple parte de US02 (Escenario 2: "Bloqueo de retroceso a
 * vistas protegidas"): como esta pantalla es la raíz de la app, si el
 * usuario presiona "Atrás" aquí, la app se cierra en vez de regresar
 * a una pantalla anterior con datos de sesión.
 */

public class LoginActivity extends AppCompatActivity {
    // Referencias a las vistas del XML. Las llenamos en onCreate()
    // usando findViewById, que "busca" en el layout el elemento que
    // tenga ese android:id.
    private TextInputEditText campoUsuario;
    private TextInputEditText campoContrasena;
    private View bannerEstado;
    private TextView textoBannerEstado;
    private Button botonIniciarSesion;
    private ProgressBar barraProgreso;

    private GestorSesion gestorSesion;
    private ApiService servicioApi;

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        setContentView(R.layout.activity_login);

        // Si YA hay una sesión guardada (por ejemplo, el usuario no
        // cerró sesión la última vez que usó la app), lo mandamos
        // directo a la pantalla principal sin pedirle login otra vez.
        gestorSesion = GestorSesion.obtenerInstancia(this);
        if (gestorSesion.haySesionActiva()) {
            irAPantallaPrincipal();
            return;
        }

        servicioApi = ClienteRetrofit.obtenerServicio();
        vincularVistas();
        configurarBotonLogin();
    }

    /** Conecta cada variable de Java con su elemento del XML. */
    private void vincularVistas() {
        campoUsuario = findViewById(R.id.campo_usuario);
        campoContrasena = findViewById(R.id.campo_contrasena);
        bannerEstado = findViewById(R.id.banner_estado);
        textoBannerEstado = findViewById(R.id.texto_banner_estado);
        botonIniciarSesion = findViewById(R.id.boton_iniciar_sesion);
        barraProgreso = findViewById(R.id.barra_progreso);
    }

    private void configurarBotonLogin() {
        botonIniciarSesion.setOnClickListener(vista -> intentarIniciarSesion());
    }

    /**
     * Método principal: se ejecuta cuando el usuario presiona el botón.
     * Sigue el orden exacto que pide la historia de usuario:
     *   1) Validar que los campos no estén vacíos.
     *   2) Verificar conectividad (Escenario 3) ANTES de llamar la API.
     *   3) Si hay conexión, llamar a POST /auth/login (Escenario 1 y 2).
     */
    private void intentarIniciarSesion() {
        ocultarBannerEstado();

        String usuario = obtenerTexto(campoUsuario);
        String contrasena = obtenerTexto(campoContrasena);

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(contrasena)) {
            mostrarError(getString(R.string.error_campos_vacios));
            return;
        }

        // ---- Escenario 3: Manejo de conectividad de red ----
        if (!GestorConectividad.hayConexionInternet(this)) {
            mostrarError(getString(R.string.error_sin_conexion));
            return; // Se detiene ANTES de llamar la API, tal como pide la historia
        }

        realizarPeticionLogin(usuario, contrasena);
    }

    private void realizarPeticionLogin(String usuario, String contrasena) {
        mostrarCargando(true);

        SolicitudLogin solicitud = new SolicitudLogin(usuario, contrasena);

        servicioApi.iniciarSesion(solicitud).enqueue(new Callback<RespuestaLogin>() {
            @Override
            public void onResponse(Call<RespuestaLogin> llamada, Response<RespuestaLogin> respuesta) {

                if (respuesta.code() == 401 || respuesta.code() == 400) {
                    // ---- Escenario 2: Credenciales incorrectas ----
                    mostrarCargando(false);
                    mostrarError(getString(R.string.error_credenciales));
                    return;
                }

                if (!respuesta.isSuccessful() || respuesta.body() == null) {
                    mostrarCargando(false);
                    mostrarError(getString(R.string.error_generico));
                    return;
                }

                // ---- Escenario 1: Autenticación exitosa ----
                String token = respuesta.body().getToken();
                buscarUsuarioYMapearRol(token, usuario);
            }

            @Override
            public void onFailure(Call<RespuestaLogin> llamada, Throwable error) {
                // Aquí caen errores como "no hay servidor", "tiempo agotado", etc.
                mostrarCargando(false);
                mostrarError(getString(R.string.error_generico));
            }
        });
    }

    /**
     * FakeStoreAPI no devuelve el ID del usuario en el login, así que
     * pedimos la lista completa de usuarios y buscamos el que tenga el
     * mismo "username" que el que se usó para iniciar sesión. Con su
     * ID ya podemos aplicar la regla de mapeo de roles.
     */
    private void buscarUsuarioYMapearRol(String token, String nombreUsuarioIngresado) {
        servicioApi.obtenerUsuarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> llamada, Response<List<Usuario>> respuesta) {
                mostrarCargando(false);

                if (!respuesta.isSuccessful() || respuesta.body() == null) {
                    mostrarError(getString(R.string.error_usuario_no_encontrado));
                    return;
                }

                Usuario usuarioEncontrado = buscarPorNombreUsuario(respuesta.body(), nombreUsuarioIngresado);

                if (usuarioEncontrado == null) {
                    mostrarError(getString(R.string.error_usuario_no_encontrado));
                    return;
                }

                RolUsuario rol = MapeadorRoles.mapearPorId(usuarioEncontrado.getId());

                gestorSesion.guardarSesion(
                        token,
                        usuarioEncontrado.getId(),
                        usuarioEncontrado.getNombreUsuario(),
                        usuarioEncontrado.obtenerNombreCompleto(),
                        rol
                );

                irAPantallaPrincipal();
            }

            @Override
            public void onFailure(Call<List<Usuario>> llamada, Throwable error) {
                mostrarCargando(false);
                mostrarError(getString(R.string.error_usuario_no_encontrado));
            }
        });
    }

    private Usuario buscarPorNombreUsuario(List<Usuario> usuarios, String nombreBuscado) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario() != null
                    && usuario.getNombreUsuario().equalsIgnoreCase(nombreBuscado)) {
                return usuario;
            }
        }
        return null;
    }

    /**
     * Navega a la pantalla principal y BORRA esta pantalla de la pila
     * de navegación (FLAG_ACTIVITY_CLEAR_TASK). Esto es importante para
     * el requisito de US02 de que, tras iniciar sesión, el botón
     * "Atrás" no regrese jamás a un estado sin sesión de forma extraña.
     */
    private void irAPantallaPrincipal() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ==================== Métodos de apoyo para la interfaz ====================

    private String obtenerTexto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }

    private void mostrarError(String mensaje) {
        textoBannerEstado.setText(mensaje);
        bannerEstado.setVisibility(View.VISIBLE);
    }

    private void ocultarBannerEstado() {
        bannerEstado.setVisibility(View.GONE);
    }

    private void mostrarCargando(boolean estaCargando) {
        barraProgreso.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
        botonIniciarSesion.setEnabled(!estaCargando);
    }

    /**
     * US02 - Escenario 2 (parte que le corresponde a esta pantalla):
     * Como LoginActivity es la raíz de la app (no tiene ninguna otra
     * Activity debajo en la pila gracias a CLEAR_TASK), presionar
     * "Atrás" aquí debe CERRAR la aplicación, nunca "regresar" a un
     * catálogo con datos de un usuario ya deslogueado.
     */
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
