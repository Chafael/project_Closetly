package com.activity.closetly.project_closedly.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.activity.closetly.project_closedly.ui.screens.profile.components.ChangeEmailDialog
import com.activity.closetly.project_closedly.ui.screens.profile.components.ChangePasswordDialog
import com.activity.closetly.project_closedly.ui.screens.profile.components.ProfileHeader
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

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
            if (uiState.showEmailDialog) profileViewModel.dismissEmailDialog()
            if (uiState.showPasswordDialog) profileViewModel.dismissPasswordDialog()
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
            SnackbarHost(hostState = snackbarHostState)
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
            ProfileHeader(
                username = uiState.username,
                memberSince = uiState.memberSince
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Configuración de la Cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { profileViewModel.showEmailDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F5),
                    contentColor = Color.Black
                )
            ) {
                Text("Cambiar Email")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { profileViewModel.showPasswordDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F5),
                    contentColor = Color.Black
                )
            ) {
                Text("Cambiar Contraseña")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { profileViewModel.logout(onLogout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEBEE),
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
