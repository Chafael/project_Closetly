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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateGarmentScreen(
    garmentId: String,
    viewModel: RateGarmentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val garment by viewModel.garment.collectAsState()
    val selectedRating by viewModel.selectedRating.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(garmentId) {
        viewModel.loadGarment(garmentId)
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("Puntuar Prenda", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBrown) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = PrimaryBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        garment?.let { currentGarment ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier
                        .size(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = currentGarment.imageUrl,
                            contentDescription = currentGarment.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Toca las estrellas para calificar",
                    fontSize = 14.sp,
                    color = SecondaryGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentGarment.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    ChipInfo(label = currentGarment.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    ChipInfo(label = currentGarment.subcategory ?: "Casual")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "¿Qué tanto te gusta\nesta prenda?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                RatingSection(
                    selectedRating = selectedRating,
                    onRatingSelected = { viewModel.setRating(it) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.saveRating()
                        showSuccessDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightBrown),
                    enabled = selectedRating > 0
                ) {
                    Text("Guardar Puntuación", fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
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
                onNavigateBack()
            }
        )
    }
}

@Composable
private fun RatingSection(
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < selectedRating) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Estrella ${index + 1}",
                    tint = if (index < selectedRating) StarYellow else SecondaryGray.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onRatingSelected(index + 1) }
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "No me gusta",
                fontSize = 12.sp,
                color = SecondaryGray
            )
            Text(
                text = getRatingText(selectedRating),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown
            )
            Text(
                text = "Perfecto",
                fontSize = 12.sp,
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

private fun getRatingText(rating: Int): String {
    return when (rating) {
        1 -> "1 - Poco"
        2 -> "2 - Poco"
        3 -> "3 - Me gusta"
        4 -> "4 - Me encanta"
        5 -> "5 - Perfecto"
        else -> ""
    }
}