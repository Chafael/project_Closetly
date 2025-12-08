package com.activity.closetly.project_closedly.ui.viewmodel

import android.net.Uri
import androidx.compose.animation.core.copy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.ui.screens.profile.ProfileUIState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(ProfileUIState())
        private set

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    init {
        val currentUser = Firebase.auth.currentUser
        _selectedImageUri.value = currentUser?.photoUrl
        uiState = uiState.copy(
            username = currentUser?.displayName ?: "Usuario",
            email = currentUser?.email ?: ""
        )
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        // Aquí se agregará la lógica para subir la imagen a Firebase
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            Firebase.auth.signOut()
            onSuccess()
        }
    }

    fun onNewEmailChange(newEmail: String) {
        uiState = uiState.copy(newEmail = newEmail)
    }

    fun onEmailAuthPasswordChange(password: String) {
        uiState = uiState.copy(emailAuthPassword = password)
    }

    fun onPasswordAuthPasswordChange(password: String) {
        uiState = uiState.copy(passwordAuthPassword = password)
    }

    fun onNewPasswordChange(password: String) {
        uiState = uiState.copy(newPassword = password)
    }

    fun onConfirmNewPasswordChange(password: String) {
        uiState = uiState.copy(confirmNewPassword = password)
    }

    fun onTogglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    fun onToggleNewPasswordVisibility() {
        uiState = uiState.copy(isNewPasswordVisible = !uiState.isNewPasswordVisible)
    }

    fun onToggleConfirmPasswordVisibility() {
        uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible)
    }

    fun onUpdateEmailClicked() {
    }

    fun onUpdatePasswordClicked() {
    }

    fun clearSuccessMessage() {
        uiState = uiState.copy(successMessage = null)
    }

    fun clearErrorMessage() {
        uiState = uiState.copy(errorMessage = null)
    }
}
