package com.activity.closetly.project_closedly // -> CORREGIDO

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// Importaciones corregidas para apuntar al paquete 'project_closedly'
import com.activity.closetly.project_closedly.ui.login.LoginScreen // -> CORREGIDO
import com.activity.closetly.project_closedly.ui.theme.Project_ClosetlyTheme // -> CORREGIDO

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project_ClosetlyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // La única responsabilidad del MainActivity es llamar a la pantalla principal.
                    LoginScreen()
                }
            }
        }
    }
}
