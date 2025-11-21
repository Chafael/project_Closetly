package com.example.closetly.data.model

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val acceptedTerms: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
