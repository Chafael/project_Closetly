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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.activity.closetly.project_closedly.ui.screens.profile.components.ChangeEmailDialog
import com.activity.closetly.project_closedly.ui.screens.profile.components.ChangePasswordDialog
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
                title = {
                    Text(
                        text = "Perfil",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB59A7A)
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
                .background(Color(0xFFFAFAFA))
                .verticalScroll(scrollState)
        ) {
            ProfileHeader(
                username = uiState.username,
                email = uiState.email,
                memberSince = uiState.memberSince
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(title = "Configuración de Cuenta")

            ProfileOption(
                icon = Icons.Default.Email,
                title = "Correo Electrónico",
                subtitle = "Tu email para iniciar sesión",
                value = uiState.email,
                onClick = profileViewModel::showEmailDialog
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.LightGray.copy(alpha = 0.3f)
            )

            ProfileOption(
                icon = Icons.Default.Lock,
                title = "Contraseña",
                subtitle = "Cambia tu contraseña de acceso",
                value = "••••••••",
                onClick = profileViewModel::showPasswordDialog
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    profileViewModel.logout(onLogout)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (uiState.showEmailDialog) {
        ChangeEmailDialog(
            currentEmail = uiState.currentEmail,
            newEmail = uiState.newEmail,
            currentPassword = uiState.currentPassword,
            isPasswordVisible = uiState.isPasswordVisible,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onNewEmailChange = profileViewModel::onNewEmailChange,
            onCurrentPasswordChange = profileViewModel::onCurrentPasswordChange,
            onTogglePasswordVisibility = profileViewModel::onTogglePasswordVisibility,
            onConfirm = profileViewModel::onUpdateEmailClicked,
            onDismiss = profileViewModel::dismissEmailDialog
        )
    }

    if (uiState.showPasswordDialog) {
        ChangePasswordDialog(
            currentPassword = uiState.currentPassword,
            newPassword = uiState.newPassword,
            confirmNewPassword = uiState.confirmNewPassword,
            isPasswordVisible = uiState.isPasswordVisible,
            isNewPasswordVisible = uiState.isNewPasswordVisible,
            isConfirmPasswordVisible = uiState.isConfirmPasswordVisible,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onCurrentPasswordChange = profileViewModel::onCurrentPasswordChange,
            onNewPasswordChange = profileViewModel::onNewPasswordChange,
            onConfirmNewPasswordChange = profileViewModel::onConfirmNewPasswordChange,
            onTogglePasswordVisibility = profileViewModel::onTogglePasswordVisibility,
            onToggleNewPasswordVisibility = profileViewModel::onToggleNewPasswordVisibility,
            onToggleConfirmPasswordVisibility = profileViewModel::onToggleConfirmPasswordVisibility,
            onConfirm = profileViewModel::onUpdatePasswordClicked,
            onDismiss = profileViewModel::dismissPasswordDialog
        )
    }
}

@Composable
private fun ProfileHeader(
    username: String,
    email: String,
    memberSince: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFFB59A7A)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = username.firstOrNull()?.uppercase() ?: "U",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = email,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        val formattedDate = if (memberSince > 0) {
            "Miembro desde ${dateFormat.format(Date(memberSince))}"
        } else {
            "Miembro reciente"
        }

        Text(
            text = formattedDate,
            fontSize = 13.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        onClick = onClick,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFFB59A7A).copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFFB59A7A),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }

            Text(
                text = "Editar",
                fontSize = 14.sp,
                color = Color(0xFFB59A7A),
                fontWeight = FontWeight.Medium
            )
        }
    }
}