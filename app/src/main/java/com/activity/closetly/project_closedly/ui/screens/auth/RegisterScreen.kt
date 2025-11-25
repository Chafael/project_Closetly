package com.activity.closetly.project_closedly.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.activity.closetly.project_closedly.ui.login.components.HeaderImage
import com.activity.closetly.project_closedly.ui.login.components.TextFieldWithLabel
import com.activity.closetly.project_closedly.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    // Inyectamos el ViewModel automáticamente con Hilt
    registerViewModel: RegisterViewModel = hiltViewModel(),
    // Funciones de navegación que vienen desde el NavGraph
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val uiState = registerViewModel.uiState
    val scrollState = rememberScrollState()

    // Observamos si el registro fue exitoso para navegar
    LaunchedEffect(uiState.isRegisterSuccess) {
        if (uiState.isRegisterSuccess) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState) // Scroll por si el teclado tapa campos
    ) {
        // Reutilizamos el header que hizo tu compañera
        HeaderImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear Cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB59A7A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // -- Campos del formulario usando el componente reutilizable --

            // Usuario
            TextFieldWithLabel(
                label = "Nombre de Usuario",
                value = uiState.username,
                onValueChange = registerViewModel::onUsernameChange,
                placeholder = "Tu nombre",
                keyboardType = KeyboardType.Text,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            TextFieldWithLabel(
                label = "Email",
                value = uiState.email,
                onValueChange = registerViewModel::onEmailChange,
                placeholder = "example@email.com",
                keyboardType = KeyboardType.Email,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contraseña
            TextFieldWithLabel(
                label = "Contraseña",
                value = uiState.password,
                onValueChange = registerViewModel::onPasswordChange,
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = uiState.isPasswordVisible,
                onPasswordToggle = registerViewModel::onTogglePasswordVisibility,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmar Contraseña
            TextFieldWithLabel(
                label = "Confirmar Contraseña",
                value = uiState.confirmPassword,
                onValueChange = registerViewModel::onConfirmPasswordChange,
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = uiState.isConfirmPasswordVisible,
                onPasswordToggle = registerViewModel::onToggleConfirmPasswordVisibility,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Términos y condiciones
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.acceptedTerms,
                    onCheckedChange = registerViewModel::onTermsChanged,
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFB59A7A))
                )
                Text(
                    text = "Acepto los términos y condiciones",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Mensaje de error
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                onClick = registerViewModel::onRegisterClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB59A7A)),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Registrarse", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ir al Login
            Row(modifier = Modifier.padding(bottom = 24.dp)) {
                Text("¿Ya tienes cuenta? ", color = Color.Gray)
                Text(
                    "Inicia Sesión",
                    color = Color(0xFFB59A7A),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                        onNavigateToLogin()
                    }
                )
            }
        }
    }
}