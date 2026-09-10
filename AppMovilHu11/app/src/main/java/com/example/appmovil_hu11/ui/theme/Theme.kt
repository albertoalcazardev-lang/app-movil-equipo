package com.example.appmovil_hu11.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NovaConfianza,
    secondary = NovaApoyo,
    tertiary = NovaAccion,
    background = NovaTexto,
    surface = NovaTexto
)

private val LightColorScheme = lightColorScheme(
    primary = NovaConfianza,
    secondary = NovaApoyo,
    tertiary = NovaAccion,
    background = NovaFondo,
    surface = NovaFondo,
    onPrimary = NovaFondo,
    onSecondary = NovaTexto,
    onTertiary = NovaFondo,
    onBackground = NovaTexto,
    onSurface = NovaTexto
)

@Composable
fun AppMovilHu11Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivamos dynamicColor por defecto para mantener la paleta NOVA
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}