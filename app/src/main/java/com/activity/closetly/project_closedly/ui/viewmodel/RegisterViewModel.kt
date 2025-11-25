package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import com.activity.closetly.project_closedly.model.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository //
) : ViewModel() {

    // Usamos el estado que ya definieron en model/RegisterState.kt
    var uiState by mutableStateOf(RegisterState())
        private set

    // --- Funciones para actualizar cada campo ---

    fun onUsernameChange(newValue: String) {
        uiState = uiState.copy(username = newValue, errorMessage = null)
    }

    fun onEmailChange(newValue: String) {
        uiState = uiState.copy(email = newValue, errorMessage = null)
    }

    fun onPasswordChange(newValue: String) {
        uiState = uiState.copy(password = newValue, errorMessage = null)
    }

    fun onConfirmPasswordChange(newValue: String) {
        uiState = uiState.copy(confirmPassword = newValue, errorMessage = null)
    }

    fun onTermsChanged(accepted: Boolean) {
        uiState = uiState.copy(acceptedTerms = accepted, errorMessage = null)
    }

    fun onTogglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    fun onToggleConfirmPasswordVisibility() {
        uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible)
    }

    // --- Lógica del botón de registro ---

    fun onRegisterClicked() {
        // 1. Validaciones
        if (uiState.username.isBlank() || uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Todos los campos son obligatorios")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            uiState = uiState.copy(errorMessage = "Formato de email inválido")
            return
        }

        if (uiState.password != uiState.confirmPassword) {
            uiState = uiState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        if (uiState.password.length < 6) {
            uiState = uiState.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (!uiState.acceptedTerms) {
            uiState = uiState.copy(errorMessage = "Debes aceptar los términos y condiciones")
            return
        }

        // 2. Llamada al repositorio
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val result = authRepository.register(
                email = uiState.email,
                password = uiState.password,
                username = uiState.username
            )

            when (result) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isRegisterSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {
                    // Ya manejado con isLoading = true
                }
            }
        }
    }
}