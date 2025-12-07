package com.activity.closetly.project_closedly.ui.screens.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.activity.closetly.project_closedly.ui.login.components.TextFieldWithLabel
import com.activity.closetly.project_closedly.ui.viewmodel.UploadGarmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadGarmentScreen(
    uploadViewModel: UploadGarmentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onUploadSuccess: () -> Unit = {}
) {
    val uiState = uploadViewModel.uiState
    val scrollState = rememberScrollState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // La URI ya está en el estado
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadViewModel.onImageSelected(it) }
    }

    LaunchedEffect(uiState.isUploadSuccess) {
        if (uiState.isUploadSuccess) {
            onUploadSuccess()
            uploadViewModel.resetUploadSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subir prenda", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(Color.White)
        ) {
            ImagePickerSection(
                imageUri = uiState.imageUri,
                onCameraClick = { uri ->
                    uploadViewModel.onImageSelected(uri)
                    cameraLauncher.launch(uri)
                },
                onGalleryClick = { galleryLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FormSection(
                uiState = uiState,
                onNameChange = uploadViewModel::onNameChange,
                onCategoryChange = uploadViewModel::onCategoryChange,
                onSubcategoryChange = uploadViewModel::onSubcategoryChange,
                onColorChange = uploadViewModel::onColorChange,
                onBrandChange = uploadViewModel::onBrandChange,
                onSeasonChange = uploadViewModel::onSeasonChange
            )

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = uploadViewModel::onUploadClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB59A7A)
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar prenda", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ImagePickerSection(
    imageUri: Uri?,
    onCameraClick: (Uri) -> Unit,
    onGalleryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Prenda seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Imagen seleccionada", fontSize = 14.sp, color = Color.Gray)
            } else {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFFB59A7A).copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sube una foto de tu prenda",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Puedes tomar una foto o seleccionar desde tu galería",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFB59A7A)
                    )
                ) {
                    Icon(Icons.Default.Image, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería")
                }
            }
        }
    }
}

@Composable
private fun FormSection(
    uiState: com.activity.closetly.project_closedly.model.UploadGarmentState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSubcategoryChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onSeasonChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Detalles de la prenda",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextFieldWithLabel(
            label = "Nombre de la prenda",
            value = uiState.name,
            onValueChange = onNameChange,
            placeholder = "Ej: Camisa blanca",
            keyboardType = KeyboardType.Text,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "Categoría",
            value = uiState.category,
            options = listOf("Camisetas", "Pantalones", "Vestidos", "Chaquetas"),
            onValueChange = onCategoryChange,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "Subcategoría",
            value = uiState.subcategory,
            options = listOf("Casual", "Formal", "Deportivo"),
            onValueChange = onSubcategoryChange,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithLabel(
            label = "Color",
            value = uiState.color,
            onValueChange = onColorChange,
            placeholder = "Ej: Blanco",
            keyboardType = KeyboardType.Text,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithLabel(
            label = "Marca (opcional)",
            value = uiState.brand,
            onValueChange = onBrandChange,
            placeholder = "Ej: Zara",
            keyboardType = KeyboardType.Text,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "Temporada",
            value = uiState.season,
            options = listOf("Todo el año", "Verano", "Invierno", "Primavera", "Otoño"),
            onValueChange = onSeasonChange,
            enabled = !uiState.isLoading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFFB59A7A),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                enabled = enabled
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}