package com.activity.closetly.project_closedly.ui.screens.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.activity.closetly.project_closedly.ui.viewmodel.UploadGarmentViewModel

private val PrimaryText = Color(0xFF705840)
private val SecondaryText = Color(0xFF6B7280)
private val ButtonBorder = Color(0xFFA28460)
private val CheckGreen = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadGarmentScreen(
    uploadViewModel: UploadGarmentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onUploadSuccess: () -> Unit = {}
) {
    val uiState = uploadViewModel.uiState
    val scrollState = rememberScrollState()

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
                title = {
                    Text(
                        "Subir prenda",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Volver",
                            tint = PrimaryText
                        )
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
                onGalleryClick = { galleryLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TipsSection()

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
                    containerColor = ButtonBorder
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
                    Text(
                        "Guardar prenda",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ImagePickerSection(
    imageUri: Uri?,
    onGalleryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = ButtonBorder
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sube una foto de tu prenda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Puedes tomar una foto o seleccionar desde tu galería",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* TODO: Implementar cámara */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonBorder
                    )
                ) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar Foto", fontSize = 14.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ButtonBorder
                    )
                ) {
                    Icon(Icons.Default.Image, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun TipsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Consejos para mejores fotos:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(12.dp))

        TipItem("Usa buena iluminación natural")
        Spacer(modifier = Modifier.height(8.dp))
        TipItem("Coloca la prenda sobre fondo neutro")
        Spacer(modifier = Modifier.height(8.dp))
        TipItem("Asegúrate que esté bien estirada")
    }
}

@Composable
private fun TipItem(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = CheckGreen,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = SecondaryText
        )
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
    // CATEGORÍAS SINCRONIZADAS CON WARDROBE
    val allCategories = listOf(
        "Camisetas",
        "Pantalones",
        "Zapatos",
        "Accesorios",
        "Bolsos",
        "Ropa interior",
        "Trajes de baño",
        "Deportiva",
        "Vestidos",
        "Chaquetas"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Detalles de la prenda",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = PrimaryText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomTextField(
            label = "Nombre de la prenda",
            value = uiState.name,
            onValueChange = onNameChange,
            placeholder = "Ej: Camisa blanca",
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "Categoría",
            value = uiState.category,
            options = allCategories,
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

        CustomTextField(
            label = "Color",
            value = uiState.color,
            onValueChange = onColorChange,
            placeholder = "Ej: Blanco",
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Marca (opcional)",
            value = uiState.brand,
            onValueChange = onBrandChange,
            placeholder = "Ej: Zara",
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

@Composable
private fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = PrimaryText
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    placeholder,
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedBorderColor = ButtonBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedTextColor = PrimaryText,
                focusedTextColor = PrimaryText
            ),
            singleLine = true,
            enabled = enabled
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = PrimaryText
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
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedBorderColor = ButtonBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedTextColor = PrimaryText,
                    focusedTextColor = PrimaryText
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
                        text = { Text(option, fontSize = 16.sp, color = PrimaryText) },
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