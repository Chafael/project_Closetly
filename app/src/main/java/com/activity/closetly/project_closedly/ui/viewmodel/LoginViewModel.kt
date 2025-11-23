package com.activity.closetly.project_closedly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// ... (data class LoginUiState no cambia)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userService: UserService // Hilt inyectará esto
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    // ... (onEmailChange, onContrasenaChange, etc. no cambian)

    fun onLoginClicked() {
        // Aquí iría la lógica real de login
        println("Login -> Email: ${uiState.email}, Contraseña: ${uiState.contrasena}")
        // viewModelScope.launch {
        //     val user = userService.findUserByEmail(uiState.email)
        //     ... lógica de validación
        // }
    }
}
