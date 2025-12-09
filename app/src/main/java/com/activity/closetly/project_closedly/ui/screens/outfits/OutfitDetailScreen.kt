package com.activity.closetly.project_closedly.ui.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.ui.components.CustomDialog
import com.activity.closetly.project_closedly.ui.components.DialogType
import com.activity.closetly.project_closedly.ui.viewmodel.OutfitDetailViewModel

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)
private val StarYellow = Color(0xFFFFC107)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailScreen(
    outfitId: String,
    viewModel: OutfitDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToRateGarment: (String) -> Unit = {},
    onOutfitDeleted: () -> Unit = {},
    onNavigateToWardrobe: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val outfit by viewModel.outfit.collectAsState()
    val garments by viewModel.garments.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    // We will use this to track which garment is being rated. If null, no dialog.
    var selectedGarmentForRating by remember { mutableStateOf<GarmentEntity?>(null) }

    LaunchedEffect(outfitId) {
        viewModel.loadOutfit(outfitId)
    }

    LaunchedEffect(Unit) {
        viewModel.deletionSuccess.collect { success ->
            if (success) {
                onOutfitDeleted()
            }
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("Mi Outfit", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBrown) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = PrimaryBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 1,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToWardrobe()
                        3 -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { paddingValues ->
        outfit?.let { currentOutfit ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                GarmentsPreviewSection(
                    garments = garments,
                    onGarmentClick = { garment ->
                        selectedGarmentForRating = garment
                    },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutfitInfoCard(
                    outfitName = currentOutfit.name,
                    occasion = currentOutfit.occasion ?: "Sin ocasión",
                    season = currentOutfit.season ?: "Todo el año",
                    createdAt = currentOutfit.createdAt,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Opciones",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OptionItem(
                    icon = Icons.Default.Delete,
                    iconColor = Color(0xFFDC2626),
                    title = "Eliminar outfit",
                    subtitle = "Esta acción no se puede deshacer",
                    onClick = { showDeleteDialog = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Estadísticas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatisticsCard(
                    rating = currentOutfit.rating,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDeleteDialog) {
        CustomDialog(
            type = DialogType.WARNING,
            title = "Advertencia",
            message = "Esta acción no se puede deshacer",
            dismissButtonText = "Cancelar",
            confirmButtonText = "Confirmar",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteOutfit()
                showDeleteConfirmation = true
            }
        )
    }

    if (showDeleteConfirmation) {
        CustomDialog(
            type = DialogType.SUCCESS,
            title = "Confirmación",
            message = "La acción se completó exitosamente",
            dismissButtonText = "Cerrar",
            onDismiss = {
                showDeleteConfirmation = false
                onNavigateBack()
            }
        )
    }

    selectedGarmentForRating?.let { garment ->
        RateGarmentDialog(
            garment = garment,
            onDismiss = { selectedGarmentForRating = null },
            onSave = { rating ->
                viewModel.saveGarmentRating(garment.id, rating)
                selectedGarmentForRating = null
            }
        )
    }
}

@Composable
private fun RateGarmentDialog(
    garment: GarmentEntity,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var currentRating by remember { mutableStateOf(garment.rating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Calificar ${garment.name}",
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circular image
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = LightBrown.copy(alpha = 0.2f)
                ) {
                   AsyncImage(
                        model = garment.imageUrl,
                        contentDescription = garment.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.BrokenImage)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Stars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < currentRating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "Estrella ${index + 1}",
                            tint = if (index < currentRating) StarYellow else SecondaryGray.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { currentRating = index + 1 }
                                .padding(2.dp)
                        )
                    }
                }
                
                 Spacer(modifier = Modifier.height(8.dp))
                 
                 Text(
                    text = when (currentRating) {
                        0 -> "Toca las estrellas para calificar"
                        else -> "${currentRating} estrella${if (currentRating > 1) "s" else ""}"
                    },
                    fontSize = 12.sp,
                    color = SecondaryGray
                 )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(currentRating) },
                colors = ButtonDefaults.buttonColors(containerColor = LightBrown)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SecondaryGray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
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

@Composable
private fun GarmentsPreviewSection(
    garments: List<GarmentEntity>,
    onGarmentClick: (GarmentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (garments.isEmpty()) {
                Text(
                    text = "No hay prendas disponibles",
                    fontSize = 14.sp,
                    color = SecondaryGray
                )
            } else {
                Text(
                    text = "Toca una prenda para calificarla",
                    fontSize = 12.sp,
                    color = SecondaryGray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Display in rows of 2
                garments.chunked(2).forEach { rowGarments ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowGarments.forEach { garment ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp)
                                    .clickable { onGarmentClick(garment) },
                                shape = RoundedCornerShape(12.dp),
                                color = LightBrown.copy(alpha = 0.1f) // Lighter background for images
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AsyncImage(
                                        model = garment.imageUrl,
                                        contentDescription = garment.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.BrokenImage) // Fallback
                                    )
                                    
                                    // Rating indicator (small star) if rated
                                    if (garment.rating > 0) {
                                        Surface(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp),
                                            color = Color.White.copy(alpha = 0.9f),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = StarYellow,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = garment.rating.toString(),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryBrown
                                                )
                                            }
                                        }
                                    }

                                    // Overlay with name at bottom
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        Text(
                                            text = garment.name,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 14.sp,
                                            modifier = Modifier
                                                .background(PrimaryBrown.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                        // Fill empty space if row has only 1 item
                        if (rowGarments.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    if (rowGarments != garments.chunked(2).last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OutfitInfoCard(
    outfitName: String,
    occasion: String,
    season: String,
    createdAt: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = outfitName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChipInfo(label = occasion)
                ChipInfo(label = season)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Creado el ${java.text.SimpleDateFormat("dd 'de' MMMM, yyyy", java.util.Locale("es", "ES")).format(java.util.Date(createdAt))}",
                fontSize = 14.sp,
                color = SecondaryGray
            )
        }
    }
}

@Composable
private fun ChipInfo(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = LightBrown.copy(alpha = 0.2f)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = LightBrown,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun OptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
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
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
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
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SecondaryGray
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SecondaryGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun StatisticsCard(
    rating: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = StarYellow,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rating.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown
            )

            Text(
                text = "Valoración",
                fontSize = 14.sp,
                color = SecondaryGray
            )
        }
    }
}