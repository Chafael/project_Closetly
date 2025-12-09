package com.activity.closetly.project_closedly.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.activity.closetly.project_closedly.model.ProfileState
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)
private val LightGray = Color(0xFFF5F5F5)
private val BorderBrown = Color(0xFFD4C4B0)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.emailUpdateState) {
        if (uiState.emailUpdateState is ProfileState.Success) {
            kotlinx.coroutines.delay(2000)
            viewModel.dismissEmailUpdateState()
        }
    }

    LaunchedEffect(uiState.passwordUpdateState) {
        if (uiState.passwordUpdateState is ProfileState.Success) {
            kotlinx.coroutines.delay(2000)
            viewModel.dismissPasswordUpdateState()
        }
    }

    Scaffold(
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(onNavigateBack = onNavigateBack)
            
            UserProfileHeader(
                username = uiState.userProfile?.username ?: "",
                memberSince = viewModel.getMemberSinceText(),
                userInitial = viewModel.getUserInitial()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Editar Perfil",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Configuración de Cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmailSection(
                currentEmail = uiState.currentEmail,
                newEmail = uiState.newEmail,
                emailPassword = uiState.emailPassword,
                onNewEmailChange = viewModel::updateNewEmail,
                onEmailPasswordChange = viewModel::updateEmailPassword,
                showEmailPassword = uiState.showEmailPassword,
                onToggleEmailPassword = viewModel::toggleEmailPasswordVisibility,
                updateState = uiState.emailUpdateState
            )

            Spacer(modifier = Modifier.height(24.dp))

            PasswordSection(
                currentPassword = uiState.currentPassword,
                newPassword = uiState.newPassword,
                confirmPassword = uiState.confirmPassword,
                showCurrentPassword = uiState.showCurrentPassword,
                showNewPassword = uiState.showNewPassword,
                showConfirmPassword = uiState.showConfirmPassword,
                onCurrentPasswordChange = viewModel::updateCurrentPassword,
                onNewPasswordChange = viewModel::updateNewPassword,
                onConfirmPasswordChange = viewModel::updateConfirmPassword,
                onToggleCurrentPassword = viewModel::toggleCurrentPasswordVisibility,
                onToggleNewPassword = viewModel::toggleNewPasswordVisibility,
                onToggleConfirmPassword = viewModel::toggleConfirmPasswordVisibility,
                hasMinLength = viewModel.hasMinimumLength(uiState.newPassword),
                hasUpperCase = viewModel.hasUpperCase(uiState.newPassword),
                hasNumber = viewModel.hasNumber(uiState.newPassword),
                updateState = uiState.passwordUpdateState
            )

            Spacer(modifier = Modifier.height(24.dp))

            ActionButtons(
                onUpdateEmail = viewModel::updateEmail,
                onUpdatePassword = viewModel::updatePassword,
                emailUpdateState = uiState.emailUpdateState,
                passwordUpdateState = uiState.passwordUpdateState,
                canUpdateEmail = uiState.newEmail.isNotBlank() && uiState.emailPassword.isNotBlank(),
                canUpdatePassword = uiState.currentPassword.isNotBlank() && 
                                   uiState.newPassword.isNotBlank() && 
                                   uiState.confirmPassword.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            SecurityMessage()

            Spacer(modifier = Modifier.height(24.dp))

            LogoutButton(
                onLogout = {
                    viewModel.logout()
                    onLogout()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopBar(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = PrimaryBrown
            )
        }
    }
}

@Composable
private fun UserProfileHeader(
    username: String,
    memberSince: String,
    userInitial: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = LightBrown
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = userInitial,
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
            fontWeight = FontWeight.Bold,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = memberSince,
            fontSize = 14.sp,
            color = SecondaryGray
        )
    }
}

@Composable
private fun EmailSection(
    currentEmail: String,
    newEmail: String,
    emailPassword: String,
    onNewEmailChange: (String) -> Unit,
    onEmailPasswordChange: (String) -> Unit,
    showEmailPassword: Boolean,
    onToggleEmailPassword: () -> Unit,
    updateState: ProfileState
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Correo Electrónico",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu email para iniciar sesión",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Email actual",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = currentEmail,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = LightGray,
                disabledBorderColor = BorderBrown,
                disabledTextColor = LightBrown
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = LightBrown,
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nuevo email",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = newEmail,
            onValueChange = onNewEmailChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("nuevo@email.com", color = Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = LightBrown,
                unfocusedBorderColor = BorderBrown,
                focusedTextColor = LightBrown,
                unfocusedTextColor = LightBrown
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Contraseña actual",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = emailPassword,
            onValueChange = onEmailPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("••••••••", color = Color.LightGray) },
            visualTransformation = if (showEmailPassword) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = LightBrown,
                unfocusedBorderColor = BorderBrown,
                focusedTextColor = LightBrown,
                unfocusedTextColor = LightBrown
            ),
            trailingIcon = {
                IconButton(onClick = onToggleEmailPassword) {
                    Icon(
                        imageVector = if (showEmailPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showEmailPassword) "Ocultar" else "Mostrar",
                        tint = LightBrown
                    )
                }
            }
        )

        if (updateState is ProfileState.Error) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = updateState.message,
                fontSize = 12.sp,
                color = Color.Red
            )
        }

        if (updateState is ProfileState.Success) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = updateState.message,
                fontSize = 12.sp,
                color = Color(0xFF10B981)
            )
        }
    }
}

@Composable
private fun PasswordSection(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    showCurrentPassword: Boolean,
    showNewPassword: Boolean,
    showConfirmPassword: Boolean,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleCurrentPassword: () -> Unit,
    onToggleNewPassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    hasMinLength: Boolean,
    hasUpperCase: Boolean,
    hasNumber: Boolean,
    updateState: ProfileState
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Contraseña",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Cambia tu contraseña de acceso",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Contraseña actual",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = currentPassword,
            onValueChange = onCurrentPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("••••••••", color = Color.LightGray) },
            visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = LightBrown,
                unfocusedBorderColor = BorderBrown,
                focusedTextColor = LightBrown,
                unfocusedTextColor = LightBrown
            ),
            trailingIcon = {
                IconButton(onClick = onToggleCurrentPassword) {
                    Icon(
                        imageVector = if (showCurrentPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showCurrentPassword) "Ocultar" else "Mostrar",
                        tint = LightBrown
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nueva contraseña",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("••••••••", color = Color.LightGray) },
            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = LightBrown,
                unfocusedBorderColor = BorderBrown,
                focusedTextColor = LightBrown,
                unfocusedTextColor = LightBrown
            ),
            trailingIcon = {
                IconButton(onClick = onToggleNewPassword) {
                    Icon(
                        imageVector = if (showNewPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showNewPassword) "Ocultar" else "Mostrar",
                        tint = LightBrown
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Confirmar nueva contraseña",
            fontSize = 12.sp,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text("••••••••", color = Color.LightGray) },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = LightBrown,
                unfocusedBorderColor = BorderBrown,
                focusedTextColor = LightBrown,
                unfocusedTextColor = LightBrown
            ),
            trailingIcon = {
                IconButton(onClick = onToggleConfirmPassword) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showConfirmPassword) "Ocultar" else "Mostrar",
                        tint = LightBrown
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Requisitos de contraseña:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordRequirement(
            text = "Mínimo 8 caracteres",
            isMet = hasMinLength
        )

        Spacer(modifier = Modifier.height(4.dp))

        PasswordRequirement(
            text = "Al menos una letra mayúscula",
            isMet = hasUpperCase
        )

        Spacer(modifier = Modifier.height(4.dp))

        PasswordRequirement(
            text = "Al menos un número",
            isMet = hasNumber
        )

        if (updateState is ProfileState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = updateState.message,
                fontSize = 12.sp,
                color = Color.Red
            )
        }

        if (updateState is ProfileState.Success) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = updateState.message,
                fontSize = 12.sp,
                color = Color(0xFF10B981)
            )
        }
    }
}

@Composable
private fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) Color(0xFF10B981) else SecondaryGray,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isMet) Color(0xFF10B981) else SecondaryGray
        )
    }
}

@Composable
private fun ActionButtons(
    onUpdateEmail: () -> Unit,
    onUpdatePassword: () -> Unit,
    emailUpdateState: ProfileState,
    passwordUpdateState: ProfileState,
    canUpdateEmail: Boolean,
    canUpdatePassword: Boolean
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Button(
            onClick = onUpdateEmail,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBrown,
                contentColor = Color.White
            ),
            enabled = emailUpdateState !is ProfileState.Loading && canUpdateEmail
        ) {
            if (emailUpdateState is ProfileState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Actualizar Email",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onUpdatePassword,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBrown,
                contentColor = Color.White
            ),
            enabled = passwordUpdateState !is ProfileState.Loading && canUpdatePassword
        ) {
            if (passwordUpdateState is ProfileState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cambiar Contraseña",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SecurityMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Seguridad",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryBrown
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Los cambios se aplicarán inmediatamente a tu cuenta.",
            fontSize = 12.sp,
            color = SecondaryGray,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFDC2626),
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Cerrar Sesión",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
