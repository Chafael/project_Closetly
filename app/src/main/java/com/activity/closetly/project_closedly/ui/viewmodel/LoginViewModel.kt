package com.activity.closetly.project_closetly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Clase de estado para mantener los datos de la UI. Se puede quedar aquí o mover a la carpeta 'ui/login'.
data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
)

// ViewModel para gestionar el estado y la lógica del login
class LoginViewModel : ViewModel() {
    // Estado de la UI, observable por la vista
    var uiState by mutableStateOf(LoginUiState())
        private set

    // Funciones para actualizar el estado desde la UI
    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onContrasenaChange(contrasena: String) {
        uiState = uiState.copy(contrasena = contrasena)
    }

    fun onToggleContrasenaVisibility() {
        uiState = uiState.copy(contrasenaVisible = !uiState.contrasenaVisible)
    }

    fun onLoginClicked() {
        // Aquí iría la lógica para iniciar sesión.
        // Por ejemplo, validar los campos y llamar a un repositorio/API.
        println("Login-Intento -> Email: ${uiState.email}, Contraseña: ${uiState.contrasena}")
    }
}
