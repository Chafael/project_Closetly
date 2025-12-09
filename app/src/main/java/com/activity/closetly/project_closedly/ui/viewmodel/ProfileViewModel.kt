package com.activity.closetly.project_closedly.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.remote.FirebaseAuthService
import com.activity.closetly.project_closedly.model.ProfileState
import com.activity.closetly.project_closedly.model.ProfileUiState
import com.activity.closetly.project_closedly.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: FirebaseAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = authService.getUserData()) {
                is AuthResult.Success -> {
                    val data = result.data
                    val userProfile = UserProfile(
                        uid = data["uid"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        username = data["username"] as? String ?: "",
                        createdAt = data["createdAt"] as? Long ?: 0L
                    )
                    _uiState.update { 
                        it.copy(
                            userProfile = userProfile,
                            currentEmail = userProfile.email,
                            isLoading = false
                        )
                    }
                    Log.d(TAG, "Perfil cargado: ${userProfile.username}")
                }
                is AuthResult.Error -> {
                    Log.e(TAG, "Error al cargar perfil: ${result.message}")
                    _uiState.update { it.copy(isLoading = false) }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun updateNewEmail(email: String) {
        _uiState.update { it.copy(newEmail = email) }
    }

    fun updateCurrentPassword(password: String) {
        _uiState.update { it.copy(currentPassword = password) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun toggleCurrentPasswordVisibility() {
        _uiState.update { it.copy(showCurrentPassword = !it.showCurrentPassword) }
    }

    fun toggleNewPasswordVisibility() {
        _uiState.update { it.copy(showNewPassword = !it.showNewPassword) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(showConfirmPassword = !it.showConfirmPassword) }
    }

    fun toggleSecurityConfirmation() {
        _uiState.update { it.copy(securityConfirmed = !it.securityConfirmed) }
    }

    fun updateEmail() {
        val state = _uiState.value
        
        if (state.newEmail.isBlank()) {
            _uiState.update { it.copy(emailUpdateState = ProfileState.Error("Ingresa un nuevo email")) }
            return
        }
        
        if (state.currentPassword.isBlank()) {
            _uiState.update { it.copy(emailUpdateState = ProfileState.Error("Ingresa tu contraseña actual")) }
            return
        }

        if (!state.securityConfirmed) {
            _uiState.update { it.copy(emailUpdateState = ProfileState.Error("Confirma la casilla de seguridad")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(emailUpdateState = ProfileState.Loading) }
            
            when (val result = authService.updateUserEmail(state.newEmail, state.currentPassword)) {
                is AuthResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            emailUpdateState = ProfileState.Success("Email actualizado exitosamente"),
                            currentEmail = state.newEmail,
                            newEmail = "",
                            currentPassword = "",
                            securityConfirmed = false
                        )
                    }
                    loadUserProfile()
                }
                is AuthResult.Error -> {
                    _uiState.update { 
                        it.copy(emailUpdateState = ProfileState.Error(result.message))
                    }
                }
                else -> {}
            }
        }
    }

    fun updatePassword() {
        val state = _uiState.value
        
        if (state.currentPassword.isBlank()) {
            _uiState.update { it.copy(passwordUpdateState = ProfileState.Error("Ingresa tu contraseña actual")) }
            return
        }
        
        if (state.newPassword.isBlank()) {
            _uiState.update { it.copy(passwordUpdateState = ProfileState.Error("Ingresa una nueva contraseña")) }
            return
        }
        
        if (state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(passwordUpdateState = ProfileState.Error("Confirma tu nueva contraseña")) }
            return
        }
        
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(passwordUpdateState = ProfileState.Error("Las contraseñas no coinciden")) }
            return
        }

        val validationError = validatePassword(state.newPassword)
        if (validationError != null) {
            _uiState.update { it.copy(passwordUpdateState = ProfileState.Error(validationError)) }
            return
        }

        if (!state.securityConfirmed) {
            _uiState.update { it.copy(passwordUpdateState = ProfileState.Error("Confirma la casilla de seguridad")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(passwordUpdateState = ProfileState.Loading) }
            
            when (val result = authService.updateUserPassword(state.currentPassword, state.newPassword)) {
                is AuthResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            passwordUpdateState = ProfileState.Success("Contraseña actualizada exitosamente"),
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = "",
                            securityConfirmed = false
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update { 
                        it.copy(passwordUpdateState = ProfileState.Error(result.message))
                    }
                }
                else -> {}
            }
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !password.any { it.isUpperCase() } -> "Debe contener al menos una letra mayúscula"
            !password.any { it.isDigit() } -> "Debe contener al menos un número"
            else -> null
        }
    }

    fun hasMinimumLength(password: String): Boolean = password.length >= 8
    fun hasUpperCase(password: String): Boolean = password.any { it.isUpperCase() }
    fun hasNumber(password: String): Boolean = password.any { it.isDigit() }

    fun dismissEmailUpdateState() {
        _uiState.update { it.copy(emailUpdateState = ProfileState.Idle) }
    }

    fun dismissPasswordUpdateState() {
        _uiState.update { it.copy(passwordUpdateState = ProfileState.Idle) }
    }

    fun logout() {
        authService.signOut()
    }

    fun getMemberSinceText(): String {
        val profile = _uiState.value.userProfile ?: return ""
        val date = Date(profile.createdAt)
        val format = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        return "Miembro desde ${format.format(date)}"
    }

    fun getUserInitial(): String {
        return _uiState.value.userProfile?.username?.firstOrNull()?.uppercase() ?: "U"
    }
}
