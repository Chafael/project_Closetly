package com.activity.closetly.project_closedly.ui.screens.profile

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.activity.closetly.project_closedly.ui.theme.brown
import com.activity.closetly.project_closedly.ui.theme.lightbrown
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel
import com.activity.closetly.project_closedly.utils.ComposeFileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePictureScreen(
    profileViewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri?.let { profileViewModel.onImageSelected(it) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.onImageSelected(it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val newImageUri = ComposeFileProvider.getImageUri(context)
            imageUri = newImageUri
            cameraLauncher.launch(newImageUri)
        }
    }

    val selectedImageUri by profileViewModel.selectedImageUri.collectAsState()
    val painter = rememberAsyncImagePainter(
        model = selectedImageUri ?: "https://via.placeholder.com/150"
    )

    val darkBrownColor = brown
    val backArrowColor = Color(0xFF424242)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Foto de perfil",
                        color = darkBrownColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = backArrowColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Foto Actual", color = darkBrownColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painter,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OptionItem(
                    icon = Icons.Default.CameraAlt,
                    text = "Tomar foto",
                    color = darkBrownColor,
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
                OptionItem(
                    icon = Icons.Default.PhotoLibrary,
                    text = "Elegir de la galería",
                    color = darkBrownColor,
                    onClick = {
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }
}

@Composable
fun OptionItem(icon: ImageVector, text: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, color = color)
    }
}