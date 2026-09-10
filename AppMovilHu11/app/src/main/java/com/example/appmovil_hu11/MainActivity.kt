package com.example.appmovil_hu11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

// Importación explícita de UsersScreen
import com.example.appmovil_hu11.ui.screens.UsersScreen
import com.example.appmovil_hu11.ui.theme.AppMovilHu11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMovilHu11Theme {
                UsersScreen(userRole = "Auditor")
            }
        }
    }
}