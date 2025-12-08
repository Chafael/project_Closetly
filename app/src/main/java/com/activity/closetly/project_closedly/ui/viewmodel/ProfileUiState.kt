package com.activity.closetly.project_closedly.ui.viewmodel

data class ProfileUiState(
    val email: String = "",
    val username: String = "",
    val memberSince: Long = 0,
    val newEmail: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isUpdatingEmail: Boolean = false,
    val isUpdatingPassword: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
