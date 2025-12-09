package com.activity.closetly.project_closedly.ui.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.activity.closetly.project_closedly.ui.components.CustomDialog
import com.activity.closetly.project_closedly.ui.components.DialogType
import com.activity.closetly.project_closedly.ui.viewmodel.RateGarmentViewModel

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)
private val StarYellow = Color(0xFFFFC107)

@Composable
fun RateGarmentScreen(
    garmentId: String,
    rateGarmentViewModel: RateGarmentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToWardrobe: () -> Unit = {},
    onNavigateToOutfits: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val garment by rateGarmentViewModel.garment.collectAsState()
    val selectedRating by rateGarmentViewModel.selectedRating.collectAsState()
    val isSaving by rateGarmentViewModel.isSaving.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(garmentId) {
        rateGarmentViewModel.loadGarment(garmentId)
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            RateGarmentTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 1,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToWardrobe()
                        1 -> onNavigateToOutfits()
                        3 -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundGray)
        ) {
            garment?.let { currentGarment ->
                // Imagen de la prenda
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    AsyncImage(
                        model = currentGarment.imageUrl,
                        contentDescription = currentGarment.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Información de la prenda
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = currentGarment.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBrown
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentGarment.category,
                        fontSize = 16.sp,
                        color = SecondaryGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¿Cómo calificarías esta prenda?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBrown
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rating stars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < selectedRating) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = "Estrella ${index + 1}",
                                tint = if (index < selectedRating) StarYellow else SecondaryGray.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp)
                                    .clickable {
                                        rateGarmentViewModel.setRating(index + 1)
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (selectedRating) {
                            0 -> "Sin calificación"
                            1 -> "No me gusta"
                            2 -> "Regular"
                            3 -> "Me gusta"
                            4 -> "Me gusta mucho"
                            5 -> "¡Me encanta!"
                            else -> ""
                        },
                        fontSize = 14.sp,
                        color = SecondaryGray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón guardar
                Button(
                    onClick = {
                        rateGarmentViewModel.saveRating {
                            showSuccessDialog = true
                        }
                    },
                    enabled = selectedRating > 0 && !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBrown,
                        disabledContainerColor = SecondaryGray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Guardar Calificación",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        CustomDialog(
            type = DialogType.SUCCESS,
            title = "¡Calificación guardada!",
            message = "Tu calificación se ha guardado exitosamente",
            dismissButtonText = "Cerrar",
            onDismiss = {
                showSuccessDialog = false
                onNavigateBack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RateGarmentTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Calificar Prenda",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = PrimaryBrown
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        listOf(
            Triple(0, Icons.Default.Home, "Armario"),
            Triple(1, Icons.Default.Checkroom, "Outfits"),
            Triple(2, Icons.Default.Add, "Crear"),
            Triple(3, Icons.Default.Person, "Perfil")
        ).forEach { (index, icon, label) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(icon, label) },
                label = { Text(label, fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LightBrown,
                    selectedTextColor = LightBrown,
                    unselectedIconColor = SecondaryGray,
                    unselectedTextColor = SecondaryGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}