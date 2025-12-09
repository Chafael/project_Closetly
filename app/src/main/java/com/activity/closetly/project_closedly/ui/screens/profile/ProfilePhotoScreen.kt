package com.activity.closetly.project_closedly.ui.screens.profile

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.activity.closetly.project_closedly.ui.components.CustomDialog
import com.activity.closetly.project_closedly.ui.components.DialogType
import com.activity.closetly.project_closedly.ui.viewmodel.ProfilePhotoState
import com.activity.closetly.project_closedly.ui.viewmodel.ProfilePhotoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)
private val LightGray = Color(0xFFF5F5F5)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfilePhotoScreen(
    viewModel: ProfilePhotoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPhotoUpdated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedImage(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            viewModel.setSelectedImage(tempImageUri)
        }
    }

    LaunchedEffect(uiState.uploadState) {
        when (uiState.uploadState) {
            is ProfilePhotoState.Success -> {
                showSuccessDialog = true
            }
            is ProfilePhotoState.Error -> {
                showErrorDialog = true
            }
            else -> {}
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Foto de Perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfilePhotoPreview(
                photoUrl = uiState.selectedImageUri?.toString() ?: uiState.currentPhotoUrl,
                userInitial = uiState.userInitial
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Foto Actual",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Toca para cambiar",
                fontSize = 12.sp,
                color = SecondaryGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            PhotoOption(
                icon = Icons.Default.CameraAlt,
                title = "Tomar Foto",
                description = "Usa la cámara para una foto nueva",
                onClick = {
                    if (cameraPermission.status.isGranted) {
                        val photoFile = File(context.cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            photoFile
                        )
                        tempImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermission.launchPermissionRequest()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PhotoOption(
                icon = Icons.Default.Image,
                title = "Elegir de Galería",
                description = "Selecciona una foto existente",
                onClick = {
                    galleryLauncher.launch("image/*")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            TipsSection()

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.uploadPhoto() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightBrown,
                    contentColor = Color.White
                ),
                enabled = uiState.uploadState !is ProfilePhotoState.Loading
            ) {
                if (uiState.uploadState is ProfilePhotoState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Listo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showSuccessDialog) {
        CustomDialog(
            type = DialogType.SUCCESS,
            title = "Confirmación",
            message = "La acción se completó exitosamente",
            dismissButtonText = "Cerrar",
            onDismiss = {
                showSuccessDialog = false
                viewModel.dismissUploadState()
                onPhotoUpdated()
                onNavigateBack()
            }
        )
    }

    if (showErrorDialog) {
        CustomDialog(
            type = DialogType.ERROR,
            title = "Error",
            message = "No se pudo completar la acción",
            dismissButtonText = "Cerrar",
            onDismiss = {
                showErrorDialog = false
                viewModel.dismissUploadState()
            }
        )
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
private fun ProfilePhotoPreview(
    photoUrl: String,
    userInitial: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl.isNotEmpty()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = LightBrown
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userInitial,
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = LightGray
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LightBrown,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = SecondaryGray
            )
        }
    }
}

@Composable
private fun TipsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = LightBrown,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Consejos para la mejor foto",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TipItem("Usa buena iluminación natural")
        Spacer(modifier = Modifier.height(8.dp))
        TipItem("Centra tu rostro en la imagen")
        Spacer(modifier = Modifier.height(8.dp))
        TipItem("Evita fondos muy ocupados")
    }
}

@Composable
private fun TipItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = LightBrown,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = SecondaryGray
        )
    }
}
