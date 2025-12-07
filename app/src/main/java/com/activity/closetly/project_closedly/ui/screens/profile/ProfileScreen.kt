package com.activity.closetly.project_closedly.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState = profileViewModel.uiState

    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            kotlinx.coroutines.delay(3000)
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        snackbarHost = {
            uiState.successMessage?.let { message ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Text(message, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            ProfileHeader(
                username = uiState.username,
                memberSince = uiState.memberSince
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Editar Perfil",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Configuración de Cuenta",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmailSection(
                currentEmail = uiState.email,
                newEmail = uiState.newEmail,
                onNewEmailChange = profileViewModel::onNewEmailChange,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            PasswordSection(
                currentPassword = uiState.currentPassword,
                newPassword = uiState.newPassword,
                confirmNewPassword = uiState.confirmNewPassword,
                isPasswordVisible = uiState.isPasswordVisible,
                isNewPasswordVisible = uiState.isNewPasswordVisible,
                isConfirmPasswordVisible = uiState.isConfirmPasswordVisible,
                onCurrentPasswordChange = profileViewModel::onCurrentPasswordChange,
                onNewPasswordChange = profileViewModel::onNewPasswordChange,
                onConfirmNewPasswordChange = profileViewModel::onConfirmNewPasswordChange,
                onTogglePasswordVisibility = profileViewModel::onTogglePasswordVisibility,
                onToggleNewPasswordVisibility = profileViewModel::onToggleNewPasswordVisibility,
                onToggleConfirmPasswordVisibility = profileViewModel::onToggleConfirmPasswordVisibility,
                isLoading = uiState.isLoading
            )

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = Color(0xFFD32F2F),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = profileViewModel::onUpdateEmailClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB59A7A)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading && uiState.newEmail.isNotBlank()
            ) {
                if (uiState.isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(
                            id = android.R.drawable.ic_dialog_email
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Actualizar Email", fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = profileViewModel::onUpdatePasswordClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB59A7A)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading &&
                        uiState.currentPassword.isNotBlank() &&
                        uiState.newPassword.isNotBlank() &&
                        uiState.confirmNewPassword.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(
                            id = android.R.drawable.ic_lock_lock
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Contraseña", fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SecurityMessage()

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    profileViewModel.logout(onLogout)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
@Composable
private fun ProfileHeader(
    username: String,
    memberSince: Long
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = Color(0xFFC4A484) // Color café claro
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = username.firstOrNull()?.uppercase() ?: "A",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = username,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF212121)
        )

        Spacer(modifier = Modifier.height(4.dp))

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        val formattedDate = if (memberSince > 0) {
            "Miembro desde ${dateFormat.format(Date(memberSince))}"
        } else {
            "Miembro desde enero 2024"
        }

        Text(
            text = formattedDate,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun EmailSection(
    currentEmail: String,
    newEmail: String,
    onNewEmailChange: (String) -> Unit,
    isLoading: Boolean
) {
    Column {
        Text(
            text = "Correo Electrónico",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tu email para iniciar sesión",
            fontSize = 12.sp,
            color = Color.Gray.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Email actual",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = currentEmail,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color(0xFFE0E0E0),
                disabledTextColor = Color(0xFF424242),
                disabledContainerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nuevo email",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newEmail,
            onValueChange = onNewEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("nuevo@email.com", color = Color.Gray) },
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFB59A7A),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(
                        id = android.R.drawable.ic_menu_edit
                    ),
                    contentDescription = "Editar",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}

@Composable
private fun PasswordSection(
    currentPassword: String,
    newPassword: String,
    confirmNewPassword: String,
    isPasswordVisible: Boolean,
    isNewPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmNewPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    isLoading: Boolean
) {
    Column {

        Text(
            text = "Contraseña",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Cambia tu contraseña de acceso",
            fontSize = 12.sp,
            color = Color.Gray.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))

        PasswordField(
            label = "Contraseña actual",
            value = currentPassword,
            onValueChange = onCurrentPasswordChange,
            isVisible = isPasswordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordField(
            label = "Nueva contraseña",
            value = newPassword,
            onValueChange = onNewPasswordChange,
            isVisible = isNewPasswordVisible,
            onToggleVisibility = onToggleNewPasswordVisibility,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordField(
            label = "Confirmar nueva contraseña",
            value = confirmNewPassword,
            onValueChange = onConfirmNewPasswordChange,
            isVisible = isConfirmPasswordVisible,
            onToggleVisibility = onToggleConfirmPasswordVisibility,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Requisitos de contraseña:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))

        PasswordRequirement(
            text = "Mínimo 8 caracteres",
            isMet = newPassword.length >= 8
        )
        PasswordRequirement(
            text = "Al menos una letra mayúscula",
            isMet = newPassword.any { it.isUpperCase() }
        )
        PasswordRequirement(
            text = "Al menos un número",
            isMet = newPassword.any { it.isDigit() }
        )
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isLoading: Boolean
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("••••••••", color = Color.Gray) },
            visualTransformation = if (isVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFB59A7A),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = "Toggle password visibility",
                        tint = Color.Gray
                    )
                }
            }
        )
    }
}

@Composable
private fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isMet,
            onCheckedChange = null,
            enabled = false,
            colors = CheckboxDefaults.colors(
                disabledCheckedColor = Color(0xFF4CAF50),
                disabledUncheckedColor = Color.Gray,
                checkmarkColor = Color.White
            ),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isMet) Color(0xFF424242) else Color.Gray
        )
    }
}

@Composable
private fun SecurityMessage() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = null,
            enabled = false,
            colors = CheckboxDefaults.colors(
                disabledCheckedColor = Color.Gray,
                disabledUncheckedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = "Seguridad",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242)
            )
            Text(
                text = "Te enviaremos un email de confirmación antes de aplicar cualquier cambio a tu cuenta.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )
        }
    }
}