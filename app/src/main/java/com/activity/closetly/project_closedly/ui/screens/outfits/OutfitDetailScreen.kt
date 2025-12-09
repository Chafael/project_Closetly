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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onOutfitDeleted: () -> Unit = {}
) {
    val outfit by viewModel.outfit.collectAsState()
    val garments by viewModel.garments.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
                    icon = Icons.Default.Edit,
                    iconColor = LightBrown,
                    title = "Puntuar Prenda",
                    subtitle = "Modificar prendas y configuración",
                    onClick = {
                        if (garments.isNotEmpty()) {
                            onNavigateToRateGarment(garments.first().id)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* TODO: Editar */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar ahora", fontSize = 16.sp, color = Color.White)
                }

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
}

@Composable
private fun GarmentsPreviewSection(
    garments: List<GarmentEntity>,
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
            garments.forEach { garment ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = LightBrown
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = garment.imageUrl,
                            contentDescription = garment.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = garment.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(PrimaryBrown.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                if (garment != garments.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
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