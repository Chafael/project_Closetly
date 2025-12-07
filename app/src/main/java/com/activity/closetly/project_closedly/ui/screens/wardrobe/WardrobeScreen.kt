package com.activity.closetly.project_closedly.ui.screens.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
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
import com.activity.closetly.project_closedly.ui.viewmodel.WardrobeViewModel

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    wardrobeViewModel: WardrobeViewModel = hiltViewModel(),
    onNavigateToUpload: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val garments by wardrobeViewModel.garments.collectAsState()
    val garmentCount by wardrobeViewModel.garmentCount.collectAsState()
    val selectedCategory by wardrobeViewModel.selectedCategory.collectAsState()

    Scaffold(
        containerColor = BackgroundGray,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToUpload,
                containerColor = LightBrown,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir prenda",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WardrobeTopBar(
                garmentCount = garmentCount,
                onSearchClick = { },
                onProfileClick = onNavigateToProfile
            )

            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = wardrobeViewModel::selectCategory
            )

            if (garments.isEmpty()) {
                EmptyWardrobeState(onAddGarment = onNavigateToUpload)
            } else {
                GarmentGrid(
                    garments = garments,
                    onGarmentClick = { }
                )
            }
        }
    }
}

@Composable
private fun WardrobeTopBar(
    garmentCount: Int,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mi Armario",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
                )
                Text(
                    text = "$garmentCount prendas",
                    fontSize = 14.sp,
                    color = SecondaryGray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = PrimaryBrown
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = onProfileClick),
                    shape = CircleShape,
                    color = LightBrown
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Todas", "Camisetas", "Pantalones", "Vestidos", "...")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory

            if (category == "...") {
                Surface(
                    modifier = Modifier
                        .height(36.dp)
                        .clickable { },
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Más categorías",
                            tint = SecondaryGray
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .height(36.dp)
                        .clickable { onCategorySelected(category) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) LightBrown else Color.Transparent
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else SecondaryGray,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GarmentGrid(
    garments: List<GarmentEntity>,
    onGarmentClick: (GarmentEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.background(BackgroundGray)
    ) {
        items(garments) { garment ->
            GarmentCard(
                garment = garment,
                onClick = { onGarmentClick(garment) }
            )
        }
    }
}

@Composable
private fun GarmentCard(
    garment: GarmentEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF5F5F5))
            ) {
                AsyncImage(
                    model = garment.imageUrl,
                    contentDescription = garment.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = garment.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown,
                    maxLines = 1
                )
                Text(
                    text = garment.subcategory ?: "Sin categoría",
                    fontSize = 12.sp,
                    color = SecondaryGray,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EmptyWardrobeState(
    onAddGarment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tu armario está vacío",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SecondaryGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Comienza añadiendo tu primera prenda",
            fontSize = 14.sp,
            color = SecondaryGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddGarment,
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBrown
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir prenda")
        }
    }
}