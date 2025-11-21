package com.activity.closetly.project_closetly.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.activity.closetly.project_closetly.ui.login.components.HeaderImage
import com.activity.closetly.project_closetly.ui.login.components.TextFieldWithLabel
import com.activity.closetly.project_closetly.ui.theme.Project_ClosetlyTheme
import com.activity.closetly.project_closetly.viewmodel.LoginViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    val uiState = loginViewModel.uiState

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

            TextFieldWithLabel(
                label = "Email",
                value = uiState.email,
                onValueChange = loginViewModel::onEmailChange,
                placeholder = "example@email.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldWithLabel(
                label = "Contraseña",
                value = uiState.contrasena,
                onValueChange = loginViewModel::onContrasenaChange,
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = uiState.contrasenaVisible,
                onPasswordToggle = loginViewModel::onToggleContrasenaVisibility
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = loginViewModel::onLoginClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB59A7A))
            ) {
                Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("o", color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Text("¿No tienes cuenta? ", color = Color.Gray)
                Text(
                    "Regístrate aquí",
                    color = Color(0xFFB59A7A),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { /* TODO: Navegar a la pantalla de registro */ }
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
