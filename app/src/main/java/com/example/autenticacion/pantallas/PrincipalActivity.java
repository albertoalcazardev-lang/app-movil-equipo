package com.example.autenticacion.pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autenticacion.R;
import com.example.autenticacion.modelos.RolUsuario;
import com.example.autenticacion.utilidades.GestorSesion;

/**
 * =======================================================================
 *  HISTORIA DE USUARIO 02: "Cierre de sesión y limpieza de credenciales"
 * =======================================================================
 * NOTA PARA EL EQUIPO: esta Activity representa, por ahora, la pantalla
 * "Mi cuenta" (mockup 14) para poder demostrar el cierre de sesión de
 * forma aislada. Cuando el resto del equipo integre el catálogo
 * (US03-US12) con su propia navegación (pestañas inferiores), el botón
 * "Cerrar sesión" y su lógica (más abajo) se pueden mover tal cual a la
 * pantalla "Mi cuenta" definitiva; no depende de nada más que de
 * GestorSesion.
 *
 * Cubre los 3 escenarios de aceptación de US02:
 *   Escenario 1: Cierre de sesión exitoso.
 *   Escenario 2: Bloqueo de retroceso a vistas protegidas (parte de
 *                esto se resuelve aquí con los FLAGS del Intent, y la
 *                otra parte en LoginActivity.onBackPressed()).
 *   Escenario 3: Limpieza profunda de memoria (token + rol + carrito).
 */

public class PrincipalActivity extends AppCompatActivity {
    private GestorSesion gestorSesion;

    private TextView textoIniciales;
    private TextView textoNombreCompleto;
    private TextView textoUsuarioArroba;
    private TextView chipRol;

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        setContentView(R.layout.activity_principal);

        gestorSesion = GestorSesion.obtenerInstancia(this);

        // Seguridad extra: si alguien llega a esta pantalla sin sesión
        // (por ejemplo, abriendo la app desde "Recientes" después de
        // que el sistema la mató), lo regresamos al Login.
        if (!gestorSesion.haySesionActiva()) {
            irALogin();
            return;
        }

        vincularVistas();
        mostrarDatosDeSesion();
        configurarBotonCerrarSesion();
    }

    private void vincularVistas() {
        textoIniciales = findViewById(R.id.texto_iniciales);
        textoNombreCompleto = findViewById(R.id.texto_nombre_completo);
        textoUsuarioArroba = findViewById(R.id.texto_usuario_arroba);
        chipRol = findViewById(R.id.chip_rol);

        // Las 3 filas "etiqueta: valor" (Identificador, Perfil, Sesión)
        // provienen del layout reutilizable fila_dato.xml, incluido
        // 3 veces con <include>. Buscamos sus TextViews internos.
        configurarFilaDato(R.id.fila_id, getString(R.string.etiqueta_id_usuario),
                "#" + gestorSesion.obtenerIdUsuario());
        configurarFilaDato(R.id.fila_rol, getString(R.string.etiqueta_perfil_acceso),
                textoLegibleDelRol(gestorSesion.obtenerRol()));
        configurarFilaDato(R.id.fila_sesion, getString(R.string.etiqueta_sesion),
                getString(R.string.valor_sesion_activa));

        findViewById(R.id.boton_cerrar_sesion).setOnClickListener(v -> cerrarSesionYRedirigir());
    }

    private void configurarFilaDato(int idContenedorInclude, String etiqueta, String valor) {
        android.view.View filaIncluida = findViewById(idContenedorInclude);
        TextView textoEtiqueta = filaIncluida.findViewById(R.id.etiqueta_fila_dato);
        TextView textoValor = filaIncluida.findViewById(R.id.valor_fila_dato);
        textoEtiqueta.setText(etiqueta);
        textoValor.setText(valor);
    }

    private void mostrarDatosDeSesion() {
        String nombreCompleto = gestorSesion.obtenerNombreCompleto();
        textoNombreCompleto.setText(nombreCompleto);
        textoUsuarioArroba.setText("@" + gestorSesion.obtenerNombreUsuario());
        chipRol.setText(textoLegibleDelRol(gestorSesion.obtenerRol()));
        textoIniciales.setText(obtenerInicialesDe(nombreCompleto));
    }

    private void configurarBotonCerrarSesion() {
        // El listener ya se configuró en vincularVistas() para mantener
        // el código agrupado con el resto de findViewById; este método
        // se deja documentado por claridad de flujo de onCreate().
    }

    /**
     * Corazón de la Historia US02: cierra la sesión y navega al Login.
     */
    private void cerrarSesionYRedirigir() {
        // Escenario 1 + Escenario 3: borra token/rol/usuario/carrito
        // del almacenamiento cifrado del dispositivo.
        gestorSesion.cerrarSesion();
        irALogin();
    }

    /**
     * Escenario 2: al volver al Login, se destruyen TODAS las pantallas
     * anteriores de la pila (FLAG_ACTIVITY_CLEAR_TASK). Así, si el
     * usuario presiona "Atrás" estando en Login, ya no existe ninguna
     * pantalla de catálogo/cuenta a la que regresar.
     */
    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String textoLegibleDelRol(RolUsuario rol) {
        switch (rol) {
            case ADMINISTRADOR:
                return getString(R.string.rol_administrador);
            case AUDITOR:
                return getString(R.string.rol_auditor);
            case CLIENTE:
            default:
                return getString(R.string.rol_cliente);
        }
    }

    private String obtenerInicialesDe(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            return "?";
        }
        String[] partes = nombreCompleto.trim().split("\\s+");
        StringBuilder iniciales = new StringBuilder();
        for (int i = 0; i < partes.length && i < 2; i++) {
            iniciales.append(Character.toUpperCase(partes[i].charAt(0)));
        }
        return iniciales.toString();
    }
}
