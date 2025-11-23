package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import com.activity.closetly.project_closedly.model.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginState())
        private set

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, errorMessage = null)
    }

    fun onContrasenaChange(contrasena: String) {
        uiState = uiState.copy(contrasena = contrasena, errorMessage = null)
    }

    fun onToggleContrasenaVisibility() {
        uiState = uiState.copy(contrasenaVisible = !uiState.contrasenaVisible)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun onLoginClicked() {
        // Validaciones básicas
        if (uiState.email.isBlank()) {
            uiState = uiState.copy(errorMessage = "Por favor ingresa tu email")
            return
        }
        if (uiState.contrasena.isBlank()) {
            uiState = uiState.copy(errorMessage = "Por favor ingresa tu contraseña")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            uiState = uiState.copy(errorMessage = "Email inválido")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            when (val result = authRepository.login(uiState.email, uiState.contrasena)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoginSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {
                }
            }
        }
    }

    fun resetLoginSuccess() {
        uiState = uiState.copy(isLoginSuccess = false)
    }
}