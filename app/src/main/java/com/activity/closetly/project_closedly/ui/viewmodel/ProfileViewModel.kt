package com.activity.closetly.project_closedly.ui.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import com.activity.closetly.project_closedly.model.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileState())
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                val email = currentUser.email ?: ""
                uiState = uiState.copy(
                    email = email,
                    currentEmail = email
                )

                when (val result = authRepository.getUserData(currentUser.uid)) {
                    is AuthResult.Success -> {
                        val data = result.data
                        val username = data["username"] as? String ?: "Usuario"
                        val createdAt = data["createdAt"] as? Long ?: 0L

                        uiState = uiState.copy(
                            username = username,
                            memberSince = createdAt
                        )
                    }
                    is AuthResult.Error -> {
                        uiState = uiState.copy(
                            errorMessage = "Error al cargar datos del perfil"
                        )
                    }
                    is AuthResult.Loading -> {}
                }
            }
        }
    }

    fun onNewEmailChange(value: String) {
        uiState = uiState.copy(newEmail = value, errorMessage = null)
    }

    fun onCurrentPasswordChange(value: String) {
        uiState = uiState.copy(currentPassword = value, errorMessage = null)
    }

    fun onTogglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    fun showEmailDialog() {
        uiState = uiState.copy(
            showEmailDialog = true,
            newEmail = "",
            currentPassword = "",
            errorMessage = null
        )
    }

    fun dismissEmailDialog() {
        uiState = uiState.copy(
            showEmailDialog = false,
            newEmail = "",
            currentPassword = "",
            errorMessage = null
        )
    }

    fun onUpdateEmailClicked() {
        if (uiState.newEmail.isBlank()) {
            uiState = uiState.copy(errorMessage = "Ingresa el nuevo email")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uiState.newEmail).matches()) {
            uiState = uiState.copy(errorMessage = "Formato de email inválido")
            return
        }

        if (uiState.newEmail == uiState.currentEmail) {
            uiState = uiState.copy(errorMessage = "El nuevo email es igual al actual")
            return
        }

        if (uiState.currentPassword.isBlank()) {
            uiState = uiState.copy(errorMessage = "Ingresa tu contraseña actual para confirmar el cambio")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            when (val result = authRepository.updateEmail(
                currentPassword = uiState.currentPassword,
                newEmail = uiState.newEmail
            )) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        email = uiState.newEmail,
                        currentEmail = uiState.newEmail,
                        newEmail = "",
                        currentPassword = "",
                        successMessage = "Email actualizado exitosamente"
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {}
            }
        }
    }

    fun onNewPasswordChange(value: String) {
        uiState = uiState.copy(newPassword = value, errorMessage = null)
    }

    fun onConfirmNewPasswordChange(value: String) {
        uiState = uiState.copy(confirmNewPassword = value, errorMessage = null)
    }

    fun onToggleNewPasswordVisibility() {
        uiState = uiState.copy(isNewPasswordVisible = !uiState.isNewPasswordVisible)
    }

    fun onToggleConfirmPasswordVisibility() {
        uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible)
    }

    fun showPasswordDialog() {
        uiState = uiState.copy(
            showPasswordDialog = true,
            currentPassword = "",
            newPassword = "",
            confirmNewPassword = "",
            errorMessage = null
        )
    }

    fun dismissPasswordDialog() {
        uiState = uiState.copy(
            showPasswordDialog = false,
            currentPassword = "",
            newPassword = "",
            confirmNewPassword = "",
            errorMessage = null
        )
    }

    fun onUpdatePasswordClicked() {
        if (uiState.currentPassword.isBlank()) {
            uiState = uiState.copy(errorMessage = "Ingresa tu contraseña actual")
            return
        }

        if (uiState.newPassword.isBlank()) {
            uiState = uiState.copy(errorMessage = "Ingresa la nueva contraseña")
            return
        }

        if (uiState.newPassword.length < 8) {
            uiState = uiState.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres")
            return
        }

        if (!uiState.newPassword.any { it.isUpperCase() }) {
            uiState = uiState.copy(errorMessage = "La contraseña debe tener al menos una mayúscula")
            return
        }

        if (!uiState.newPassword.any { it.isDigit() }) {
            uiState = uiState.copy(errorMessage = "La contraseña debe tener al menos un número")
            return
        }

        if (uiState.newPassword != uiState.confirmNewPassword) {
            uiState = uiState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        if (uiState.currentPassword == uiState.newPassword) {
            uiState = uiState.copy(errorMessage = "La nueva contraseña debe ser diferente")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            when (val result = authRepository.updatePassword(
                currentPassword = uiState.currentPassword,
                newPassword = uiState.newPassword
            )) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        currentPassword = "",
                        newPassword = "",
                        confirmNewPassword = "",
                        successMessage = "Contraseña actualizada exitosamente"
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {}
            }
        }
    }

    fun clearSuccessMessage() {
        uiState = uiState.copy(successMessage = null)
    }

    fun clearErrorMessage() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
        }
    }
}