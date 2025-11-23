package com.activity.closetly.project_closedly.model

data class LoginState(
    val email: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)