package com.activity.closetly.project_closedly.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val createdAt: Long = 0L
)

sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data class Success(val message: String) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val currentEmail: String = "",
    val newEmail: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val showCurrentPassword: Boolean = false,
    val showNewPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val emailUpdateState: ProfileState = ProfileState.Idle,
    val passwordUpdateState: ProfileState = ProfileState.Idle,
    val securityConfirmed: Boolean = false
)
