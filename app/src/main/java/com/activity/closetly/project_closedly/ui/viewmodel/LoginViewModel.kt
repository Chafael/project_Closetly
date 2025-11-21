package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

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
        println("Login -> Email: ${uiState.email}, Contraseña: ${uiState.contrasena}")
    }
}