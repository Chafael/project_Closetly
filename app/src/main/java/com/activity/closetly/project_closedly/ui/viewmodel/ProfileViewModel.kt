package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        authRepository.currentUser?.let { user ->
            viewModelScope.launch {
                when (val result = authRepository.getUserData(user.uid)) {
                    is AuthResult.Success -> {
                        val data = result.data
                        uiState = uiState.copy(
                            email = data["email"] as? String ?: "",
                            username = data["username"] as? String ?: "",
                            memberSince = data["createdAt"] as? Long ?: 0
                        )
                    }
                    is AuthResult.Error -> {
                        uiState = uiState.copy(errorMessage = result.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun onUpdateEmailClicked() {
        viewModelScope.launch {
            uiState = uiState.copy(isUpdatingEmail = true, errorMessage = null)
            val result = authRepository.updateEmail(uiState.currentPassword, uiState.newEmail)
            if (result is AuthResult.Error) {
                uiState = uiState.copy(errorMessage = result.message)
            } else {
                uiState = uiState.copy(
                    successMessage = "¡Revisa tu nuevo correo! Haz clic en el enlace para finalizar el cambio.",
                    newEmail = "",
                    currentPassword = ""
                )
            }
            uiState = uiState.copy(isUpdatingEmail = false)
        }
    }

    fun onUpdatePasswordClicked() {
        if (uiState.newPassword != uiState.confirmNewPassword) {
            uiState = uiState.copy(errorMessage = "Las nuevas contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isUpdatingPassword = true, errorMessage = null)
            val result = authRepository.updatePassword(uiState.currentPassword, uiState.newPassword)
            if (result is AuthResult.Error) {
                uiState = uiState.copy(errorMessage = result.message)
            } else {
                uiState = uiState.copy(
                    successMessage = "Contraseña actualizada con éxito",
                    currentPassword = "",
                    newPassword = "",
                    confirmNewPassword = ""
                )
            }
            uiState = uiState.copy(isUpdatingPassword = false)
        }
    }

    fun onNewEmailChange(value: String) { uiState = uiState.copy(newEmail = value) }
    fun onCurrentPasswordChange(value: String) { uiState = uiState.copy(currentPassword = value) }
    fun onNewPasswordChange(value: String) { uiState = uiState.copy(newPassword = value) }
    fun onConfirmNewPasswordChange(value: String) { uiState = uiState.copy(confirmNewPassword = value) }
    fun onTogglePasswordVisibility() { uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible) }
    fun onToggleNewPasswordVisibility() { uiState = uiState.copy(isNewPasswordVisible = !uiState.isNewPasswordVisible) }
    fun onToggleConfirmPasswordVisibility() { uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible) }
    fun clearSuccessMessage() { uiState = uiState.copy(successMessage = null) }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }
}
