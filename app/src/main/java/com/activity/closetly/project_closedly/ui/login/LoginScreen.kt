package com.activity.closetly.project_closedly.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.activity.closetly.project_closedly.ui.login.components.HeaderImage
import com.activity.closetly.project_closedly.ui.login.components.TextFieldWithLabel
import com.activity.closetly.project_closedly.ui.theme.Project_ClosetlyTheme
import com.activity.closetly.project_closedly.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val uiState = loginViewModel.uiState

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
            loginViewModel.resetLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Campo de Email
            TextFieldWithLabel(
                label = "Email",
                value = uiState.email,
                onValueChange = loginViewModel::onEmailChange,
                placeholder = "example@email.com",
                keyboardType = KeyboardType.Email,
                enabled = !uiState.isLoading,
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            TextFieldWithLabel(
                label = "Contraseña",
                value = uiState.contrasena,
                onValueChange = loginViewModel::onContrasenaChange,
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = uiState.contrasenaVisible,
                onPasswordToggle = loginViewModel::onToggleContrasenaVisibility,
                enabled = !uiState.isLoading,
                isError = uiState.errorMessage != null
            )

            // mensaje de error
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de login
            Button(
                onClick = loginViewModel::onLoginClicked,
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
                    Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("o", color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            // Enlace a registro
            Row {
                Text("¿No tienes cuenta? ", color = Color.Gray)
                Text(
                    "Regístrate aquí",
                    color = Color(0xFFB59A7A),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                        onNavigateToRegister()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun LoginScreenPreview() {
    Project_ClosetlyTheme {
        LoginScreen()
    }
}