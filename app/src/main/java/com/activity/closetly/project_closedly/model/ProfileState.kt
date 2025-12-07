package com.activity.closetly.project_closedly.model

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val memberSince: Long = 0L,

    val currentEmail: String = "",
    val newEmail: String = "",

    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",

    val isPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val showEmailDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)