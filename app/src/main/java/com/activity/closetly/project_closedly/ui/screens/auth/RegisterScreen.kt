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
import com.activity.closetly.project_closedly.ui.viewmodel.RegisterViewModelNew

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModelNew = hiltViewModel(),
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val uiState = registerViewModel.uiState
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isRegisterSuccess) {
        if (uiState.isRegisterSuccess) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        HeaderImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextFieldWithLabel(
                label = "Nombre de usuario",
                value = uiState.username,
                onValueChange = registerViewModel::onUsernameChange,
                placeholder = "@usuario123",
                keyboardType = KeyboardType.Text,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldWithLabel(
                label = "Email",
                value = uiState.email,
                onValueChange = registerViewModel::onEmailChange,
                placeholder = "ejemplo@email.com",
                keyboardType = KeyboardType.Email,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.acceptedTerms,
                    onCheckedChange = registerViewModel::onTermsChanged,
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFA28460))
                )
                Text(
                    text = "Acepto los términos y condiciones y la política de privacidad de Closetly",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = registerViewModel::onRegisterClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA28460)),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear cuenta", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "¿Ya tienes cuenta?",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                "Inicia sesión aquí",
                color = Color(0xFFA28460),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable(enabled = !uiState.isLoading) {
                        onNavigateToLogin()
                    }
                    .padding(bottom = 24.dp)
            )
        }
    }
}