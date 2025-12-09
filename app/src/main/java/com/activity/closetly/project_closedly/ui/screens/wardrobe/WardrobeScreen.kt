package com.activity.closetly.project_closedly.ui.screens.wardrobe

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.activity.closetly.project_closedly.R
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.ui.components.CategoryDialog
import com.activity.closetly.project_closedly.ui.components.CustomDialog
import com.activity.closetly.project_closedly.ui.components.DialogType
import com.activity.closetly.project_closedly.ui.viewmodel.WardrobeViewModel

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)

@Composable
fun WardrobeScreen(
    wardrobeViewModel: WardrobeViewModel = hiltViewModel(),
    onNavigateToUpload: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOutfits: () -> Unit = {}
) {
    val garments by wardrobeViewModel.garments.collectAsState()
    val garmentCount by wardrobeViewModel.garmentCount.collectAsState()
    val selectedCategory by wardrobeViewModel.selectedCategory.collectAsState()
    val profilePhotoUrl by wardrobeViewModel.profilePhotoUrl.collectAsState()
    val userInitial by wardrobeViewModel.userInitial.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var garmentToDelete by remember { mutableStateOf<GarmentEntity?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        wardrobeViewModel.refreshProfile()
    }

    Scaffold(
        containerColor = BackgroundGray,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToUpload,
                containerColor = LightBrown,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Añadir prenda", tint = Color.White)
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        1 -> onNavigateToOutfits()
                        2 -> onNavigateToUpload()
                        3 -> onNavigateToProfile()
                    }
                }
            )
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            WardrobeTopBar(
                garmentCount = garmentCount,
                onSearchClick = { },
                onProfileClick = onNavigateToProfile,
                profilePhotoUrl = profilePhotoUrl,
                userInitial = userInitial
            )

            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { wardrobeViewModel.selectCategory(it) },
                onMoreClick = { showCategoryMenu = true }
            )

            if (garments.isEmpty()) {
                EmptyWardrobeState(category = selectedCategory, onAddGarment = onNavigateToUpload)
            } else {
                GarmentGrid(
                    garments = garments,
                    onGarmentClick = { },
                    onDeleteClick = { garmentToDelete = it; showDeleteDialog = true }
                )
            }
        }
    }

    if (showCategoryMenu) {
        CategoryDialog(
            onDismiss = { showCategoryMenu = false },
            onCategorySelected = { wardrobeViewModel.selectCategory(it); showCategoryMenu = false }
        )
    }

    if (showDeleteDialog && garmentToDelete != null) {
        CustomDialog(
            type = DialogType.WARNING,
            title = "Advertencia",
            message = "Esta acción no se puede deshacer",
            dismissButtonText = "Cancelar",
            confirmButtonText = "Confirmar",
            onDismiss = { showDeleteDialog = false; garmentToDelete = null },
            onConfirm = {
                wardrobeViewModel.deleteGarment(garmentToDelete!!)
                showDeleteDialog = false
                garmentToDelete = null
                showSuccessDialog = true
            }
        )
    }

    if (showSuccessDialog) {
        CustomDialog(
            type = DialogType.SUCCESS,
            title = "Confirmación",
            message = "La acción se completó exitosamente",
            dismissButtonText = "Cerrar",
            confirmButtonText = "Continuar",
            onDismiss = { showSuccessDialog = false },
            onConfirm = { showSuccessDialog = false }
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
private fun WardrobeTopBar(
    garmentCount: Int,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    profilePhotoUrl: String,
    userInitial: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Mi Armario", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBrown)
            Text("$garmentCount prendas", fontSize = 14.sp, color = SecondaryGray)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "Buscar", tint = SecondaryGray)
            }
            if (profilePhotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = profilePhotoUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onProfileClick),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(40.dp).clickable(onClick = onProfileClick),
                    shape = CircleShape,
                    color = LightBrown
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(userInitial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onMoreClick: () -> Unit
) {
    val mainCategories = listOf("Todas", "Camisetas", "Pantalones")
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White)
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        mainCategories.forEach { category ->
            CategoryChip(category, category == selectedCategory) { onCategorySelected(category) }
        }
        if (selectedCategory !in mainCategories && selectedCategory != "Todas") {
            CategoryChip(selectedCategory, true) { }
        }
        IconButton(onClick = onMoreClick, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.MoreHoriz, "Más", tint = SecondaryGray)
        }
    }
}

@Composable
private fun CategoryChip(category: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(36.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) LightBrown else Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(category, color = if (isSelected) Color.White else SecondaryGray, fontSize = 14.sp)
        }
    }
}

@Composable
private fun GarmentGrid(
    garments: List<GarmentEntity>,
    onGarmentClick: (GarmentEntity) -> Unit,
    onDeleteClick: (GarmentEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.background(BackgroundGray)
    ) {
        items(garments) { garment ->
            GarmentCard(garment, { onGarmentClick(garment) }, { onDeleteClick(garment) })
        }
    }
}

@Composable
private fun GarmentCard(garment: GarmentEntity, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.75f).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = garment.imageUrl,
                        contentDescription = garment.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.ic_launcher_foreground)
                    )
                }
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(garment.name, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = PrimaryBrown, maxLines = 1)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(garment.category, fontSize = 13.sp, color = SecondaryGray, maxLines = 1)
                }
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp)
            ) {
                Surface(shape = CircleShape, color = Color.White.copy(0.9f), modifier = Modifier.fillMaxSize()) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyWardrobeState(category: String, onAddGarment: () -> Unit) {
    val message = if (category == "Todas") "Tu armario está vacío" else "No tienes prendas en \"$category\""
    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundGray).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SecondaryGray)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Comienza añadiendo tu primera prenda", fontSize = 14.sp, color = SecondaryGray)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddGarment,
            colors = ButtonDefaults.buttonColors(LightBrown),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir prenda")
        }
    }
}