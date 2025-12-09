package com.activity.closetly.project_closedly.ui.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import com.activity.closetly.project_closedly.ui.components.CustomDialog
import com.activity.closetly.project_closedly.ui.components.DialogType
import com.activity.closetly.project_closedly.ui.viewmodel.OutfitsViewModel
import org.json.JSONArray

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)
private val StarYellow = Color(0xFFFFC107)

@Composable
fun OutfitsScreen(
    outfitsViewModel: OutfitsViewModel = hiltViewModel(),
    onNavigateToCreate: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToWardrobe: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val outfits by outfitsViewModel.outfits.collectAsState()
    val outfitCount by outfitsViewModel.outfitCount.collectAsState()
    val selectedOccasion by outfitsViewModel.selectedOccasion.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var outfitToDelete by remember { mutableStateOf<OutfitEntity?>(null) }

    Scaffold(
        containerColor = BackgroundGray,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = LightBrown,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Crear outfit", tint = Color.White)
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 1,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToWardrobe()
                        2 -> onNavigateToCreate()
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
            OutfitsTopBar(
                outfitCount = outfitCount,
                onSearchClick = { }
            )

            OccasionTabs(
                selectedOccasion = selectedOccasion,
                onOccasionSelected = { outfitsViewModel.selectOccasion(it) }
            )

            if (outfits.isEmpty()) {
                EmptyOutfitsState(onCreateOutfit = onNavigateToCreate)
            } else {
                OutfitsGrid(
                    outfits = outfits,
                    onOutfitClick = { onNavigateToDetail(it.id) },
                    onDeleteClick = { outfitToDelete = it; showDeleteDialog = true }
                )
            }
        }
    }

    if (showDeleteDialog && outfitToDelete != null) {
        CustomDialog(
            type = DialogType.WARNING,
            title = "Eliminar Outfit",
            message = "¿Estás seguro de que quieres eliminar \"${outfitToDelete!!.name}\"?",
            dismissButtonText = "Cancelar",
            confirmButtonText = "Eliminar",
            onDismiss = { showDeleteDialog = false; outfitToDelete = null },
            onConfirm = {
                outfitsViewModel.deleteOutfit(outfitToDelete!!)
                showDeleteDialog = false
                outfitToDelete = null
                showSuccessDialog = true
            }
        )
    }

    if (showSuccessDialog) {
        CustomDialog(
            type = DialogType.SUCCESS,
            title = "Outfit Eliminado",
            message = "El outfit se eliminó correctamente",
            dismissButtonText = "Cerrar",
            onDismiss = { showSuccessDialog = false }
        )
    }
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
private fun OutfitsTopBar(
    outfitCount: Int,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Mi Outfit",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown
            )
            Text(
                "$outfitCount outfits",
                fontSize = 14.sp,
                color = SecondaryGray
            )
        }
        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, "Buscar", tint = SecondaryGray)
        }
    }
}

@Composable
private fun OccasionTabs(
    selectedOccasion: String,
    onOccasionSelected: (String) -> Unit
) {
    val occasions = listOf("Todos", "Casual", "Formal", "Trabajo", "Fiesta", "Deportivo")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        occasions.forEach { occasion ->
            OccasionChip(
                occasion = occasion,
                isSelected = occasion == selectedOccasion,
                onClick = { onOccasionSelected(occasion) }
            )
        }
    }
}

@Composable
private fun OccasionChip(
    occasion: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) LightBrown else Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                occasion,
                color = if (isSelected) Color.White else SecondaryGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun OutfitsGrid(
    outfits: List<OutfitEntity>,
    onOutfitClick: (OutfitEntity) -> Unit,
    onDeleteClick: (OutfitEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.background(BackgroundGray)
    ) {
        items(outfits) { outfit ->
            OutfitCard(
                outfit = outfit,
                onClick = { onOutfitClick(outfit) },
                onDeleteClick = { onDeleteClick(outfit) }
            )
        }
    }
}

@Composable
private fun OutfitCard(
    outfit: OutfitEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val garmentIds = try {
        val jsonArray = JSONArray(outfit.garmentIds)
        List(jsonArray.length()) { jsonArray.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                OutfitImageCollage(garmentIds = garmentIds)

                Text(
                    text = "${garmentIds.size} prendas",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(PrimaryBrown.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(0.9f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Delete,
                                "Eliminar",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    outfit.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    outfit.occasion ?: "Sin ocasión",
                    fontSize = 13.sp,
                    color = SecondaryGray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatingBar(
                    rating = outfit.rating,
                    isEditable = false
                )
            }
        }
    }
}

@Composable
private fun OutfitImageCollage(garmentIds: List<String>) {
    val displayCount = minOf(garmentIds.size, 3)

    when (displayCount) {
        0 -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F0F0))
            ) {
                Icon(
                    imageVector = Icons.Default.Checkroom,
                    contentDescription = null,
                    tint = SecondaryGray.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }
        }
        1 -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F0F0))
            ) {
                Icon(
                    imageVector = Icons.Default.Checkroom,
                    contentDescription = null,
                    tint = SecondaryGray.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }
        }
        2 -> {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFF0F0F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = SecondaryGray.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(1.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFE0E0E0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = SecondaryGray.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFF0F0F0))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checkroom,
                            contentDescription = null,
                            tint = SecondaryGray.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFE0E0E0))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checkroom,
                            contentDescription = null,
                            tint = SecondaryGray.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFD0D0D0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = SecondaryGray.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingBar(
    rating: Int,
    isEditable: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = "Estrella ${index + 1}",
                tint = if (index < rating) StarYellow else SecondaryGray.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyOutfitsState(
    onCreateOutfit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Checkroom,
            contentDescription = null,
            tint = SecondaryGray.copy(alpha = 0.5f),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "No tienes outfits",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SecondaryGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Crea tu primer outfit combinando prendas",
            fontSize = 14.sp,
            color = SecondaryGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateOutfit,
            colors = ButtonDefaults.buttonColors(LightBrown),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Crear outfit")
        }
    }
}