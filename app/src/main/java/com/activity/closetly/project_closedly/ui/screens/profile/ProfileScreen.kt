package com.activity.closetly.project_closedly.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.activity.closetly.project_closedly.ui.screens.profile.components.ProfileHeader
import com.activity.closetly.project_closedly.ui.screens.profile.components.TextFieldWithLabel
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState = profileViewModel.uiState
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.successMessage) {
        if (uiState.successMessage != null) {
            snackbarHostState.showSnackbar(uiState.successMessage)
            profileViewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF424242)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(username = uiState.username, memberSince = uiState.memberSince)
                Text("Editar Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
                Text("Configuración de Cuenta", fontSize = 16.sp, color = Color(0xFFB59A7A), fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Correo Electrónico", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Tu email para iniciar sesión", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            TextFieldWithLabel(label = "Email actual", value = uiState.email, onValueChange = {}, enabled = false, isEditable = true)
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithLabel(label = "Nuevo email", value = uiState.newEmail, onValueChange = profileViewModel::onNewEmailChange, placeholder = "nuevo@email.com", keyboardType = KeyboardType.Email)

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            Text("Contraseña", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Cambia tu contraseña de acceso", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            TextFieldWithLabel(label = "Contraseña actual", value = uiState.currentPassword, onValueChange = profileViewModel::onCurrentPasswordChange, isPassword = true, passwordVisible = uiState.isPasswordVisible, onPasswordToggle = profileViewModel::onTogglePasswordVisibility)
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithLabel(label = "Nueva contraseña", value = uiState.newPassword, onValueChange = profileViewModel::onNewPasswordChange, isPassword = true, passwordVisible = uiState.isNewPasswordVisible, onPasswordToggle = profileViewModel::onToggleNewPasswordVisibility)
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithLabel(label = "Confirmar nueva contraseña", value = uiState.confirmNewPassword, onValueChange = profileViewModel::onConfirmNewPasswordChange, isPassword = true, passwordVisible = uiState.isConfirmPasswordVisible, onPasswordToggle = profileViewModel::onToggleConfirmPasswordVisibility)
            Spacer(modifier = Modifier.height(16.dp))

            PasswordRequirements(password = uiState.newPassword)

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = profileViewModel::onUpdateEmailClicked,
                enabled = uiState.newEmail.isNotBlank() && uiState.currentPassword.isNotBlank() && !uiState.isUpdatingEmail && !uiState.isUpdatingPassword,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB59A7A), contentColor = Color.White, disabledContainerColor = Color(0xFFD3C1B0), disabledContentColor = Color(0xFFF5F5F5))
            ) {
                if (uiState.isUpdatingEmail) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Actualizar Email")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = profileViewModel::onUpdatePasswordClicked,
                enabled = uiState.newPassword.isNotBlank() && uiState.confirmNewPassword.isNotBlank() && uiState.currentPassword.isNotBlank() && !uiState.isUpdatingPassword && !uiState.isUpdatingEmail,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D5D52), contentColor = Color.White, disabledContainerColor = Color(0xFFB4A9A2), disabledContentColor = Color(0xFFF5F5F5))
            ) {
                if (uiState.isUpdatingPassword) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Contraseña")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            SecurityInfo()
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { profileViewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFD32F2F))
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PasswordRequirements(password: String) {
    val hasMinChars = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasNumber = password.any { it.isDigit() }
    Column {
        Text("Requisitos de contraseña:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        RequirementItem(text = "Mínimo 8 caracteres", isMet = hasMinChars)
        RequirementItem(text = "Al menos una letra mayúscula", isMet = hasUppercase)
        RequirementItem(text = "Al menos un número", isMet = hasNumber)
    }
}

@Composable
private fun RequirementItem(text: String, isMet: Boolean) {
    val color = if (isMet) Color(0xFF4CAF50) else Color.Gray
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = color)
    }
}

@Composable
private fun SecurityInfo() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text("Te enviaremos un email de confirmación antes de aplicar cualquier cambio a tu cuenta.", fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
    }
}
