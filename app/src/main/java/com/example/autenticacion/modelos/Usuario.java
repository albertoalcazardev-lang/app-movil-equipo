package com.example.autenticacion.modelos;
import com.google.gson.annotations.SerializedName;

/**
 * Representa a un usuario tal como lo devuelve FakeStoreAPI en el
 * endpoint GET https://fakestoreapi.com/users
 *
 * IMPORTANTE PARA PRINCIPIANTES:
 * Esta clase es un "POJO" (Plain Old Java Object): solo tiene atributos
 * y sus getters/setters. NO tiene lógica. Su único trabajo es guardar
 * los datos que llegan de internet en variables de Java.
 *
 * La librería Gson (que usa Retrofit por debajo) lee el JSON que
 * devuelve la API y llena automáticamente estos atributos, siempre
 * y cuando el nombre coincida (o le digamos el nombre real con
 * @SerializedName, como hacemos con "name").
 */

public class Usuario {
    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String correo;

    @SerializedName("username")
    private String nombreUsuario;

    @SerializedName("password")
    private String contrasena;

    @SerializedName("phone")
    private String telefono;

    // FakeStoreAPI envía el nombre completo como un objeto anidado:
    // "name": { "firstname": "...", "lastname": "..." }
    @SerializedName("name")
    private Nombre nombre;

    /**
     * Clase interna que representa el objeto anidado "name" del JSON.
     * La ponemos "dentro" de Usuario porque solo tiene sentido junto a él.
     */
    public static class Nombre {
        @SerializedName("firstname")
        private String primerNombre;

        @SerializedName("lastname")
        private String apellido;

        public String getPrimerNombre() {
            return primerNombre == null ? "" : primerNombre;
        }

        public String getApellido() {
            return apellido == null ? "" : apellido;
        }
    }

    // ==================== Getters ====================
    // (No hace falta setters: nosotros no vamos a modificar estos datos
    // manualmente, solo los vamos a LEER después de que Gson los llene)

    public int getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    /**
     * Devuelve el nombre completo listo para mostrar en pantalla,
     * por ejemplo: "Lucía Torres". Si la API no trae nombre (puede
     * pasar con algunos usuarios de prueba), devolvemos el username.
     */
    public String obtenerNombreCompleto() {
        if (nombre == null) {
            return nombreUsuario;
        }
        String completo = (nombre.getPrimerNombre() + " " + nombre.getApellido()).trim();
        return completo.isEmpty() ? nombreUsuario : completo;
    }

    /**
     * Devuelve las iniciales del nombre completo para mostrarlas en el
     * avatar circular de la pantalla "Mi cuenta" (ej. "Lucía Torres" -> "LT")
     */
    public String obtenerIniciales() {
        String completo = obtenerNombreCompleto();
        String[] partes = completo.trim().split("\\s+");
        StringBuilder iniciales = new StringBuilder();
        for (int i = 0; i < partes.length && i < 2; i++) {
            if (!partes[i].isEmpty()) {
                iniciales.append(Character.toUpperCase(partes[i].charAt(0)));
            }
        }
        return iniciales.length() == 0 ? "?" : iniciales.toString();
    }
}
