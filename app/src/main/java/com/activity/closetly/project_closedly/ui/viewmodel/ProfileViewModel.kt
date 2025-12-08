package com.activity.closetly.project_closedly.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        authRepository.currentUser?.let { user ->
            viewModelScope.launch {
                val result = authRepository.getUserData(user.uid)
                if (result is AuthResult.Success) {
                    val data = result.data
                    uiState = uiState.copy(
                        email = data["email"] as? String ?: "",
                        username = data["username"] as? String ?: "",
                        memberSince = data["createdAt"] as? Long ?: 0
                    )
                }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun onUpdateEmailClicked() {
        viewModelScope.launch {
            uiState = uiState.copy(isUpdatingEmail = true, errorMessage = null)
            val result = authRepository.updateEmail(uiState.emailAuthPassword, uiState.newEmail)
            if (result is AuthResult.Error) {
                uiState = uiState.copy(errorMessage = result.message)
            } else {
                uiState = uiState.copy(
                    successMessage = "Email actualizado con éxito",
                    newEmail = "",
                    emailAuthPassword = ""
                )
                loadUserData()
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
            val result = authRepository.updatePassword(uiState.passwordAuthPassword, uiState.newPassword)
            if (result is AuthResult.Error) {
                uiState = uiState.copy(errorMessage = result.message)
            } else {
                uiState = uiState.copy(
                    successMessage = "Contraseña actualizada con éxito",
                    passwordAuthPassword = "",
                    newPassword = "",
                    confirmNewPassword = ""
                )
            }
            uiState = uiState.copy(isUpdatingPassword = false)
        }
    }

    fun onNewEmailChange(value: String) { uiState = uiState.copy(newEmail = value) }
    fun onEmailAuthPasswordChange(value: String) { uiState = uiState.copy(emailAuthPassword = value) }

    fun onPasswordAuthPasswordChange(value: String) { uiState = uiState.copy(passwordAuthPassword = value) }
    fun onNewPasswordChange(value: String) { uiState = uiState.copy(newPassword = value) }
    fun onConfirmNewPasswordChange(value: String) { uiState = uiState.copy(confirmNewPassword = value) }

    fun onTogglePasswordVisibility() { uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible) }
    fun onToggleNewPasswordVisibility() { uiState = uiState.copy(isNewPasswordVisible = !uiState.isNewPasswordVisible) }
    fun onToggleConfirmPasswordVisibility() { uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible) }

    fun clearSuccessMessage() { uiState = uiState.copy(successMessage = null) }
    fun clearErrorMessage() { uiState = uiState.copy(errorMessage = null) }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }
}