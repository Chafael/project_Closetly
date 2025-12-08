package com.activity.closetly.project_closedly.ui.screens.wardrobe

import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
import com.activity.closetly.project_closedly.R
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.ui.viewmodel.ProfileViewModel
import com.activity.closetly.project_closedly.ui.viewmodel.WardrobeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    wardrobeViewModel: WardrobeViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToUpload: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val garments by wardrobeViewModel.garments.collectAsState()
    val garmentCount by wardrobeViewModel.garmentCount.collectAsState()
    val selectedCategory by wardrobeViewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = {
            WardrobeTopBar(
                garmentCount = garmentCount,
                onSearchClick = { },
                onProfileClick = onNavigateToProfile,
                profileViewModel = profileViewModel
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToUpload,
                containerColor = Color(0xFFB59A7A),
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
                .background(Color(0xFFFAFAFA))
        ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WardrobeTopBar(
    garmentCount: Int,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val selectedImageUri by profileViewModel.selectedImageUri.collectAsState()
    val profileImagePainter = rememberAsyncImagePainter(
        model = selectedImageUri ?: "https://via.placeholder.com/150"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                    color = Color(0xFF6D5D52)
                )
                Text(
                    text = "$garmentCount prendas",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = profileImagePainter,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = Color(0xFFB59A7A)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
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
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Todas", "Camisetas", "Pantalones", "Vestidos", "Chaquetas")

    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        edgePadding = 16.dp,
        containerColor = Color.White,
        contentColor = Color(0xFFB59A7A),
        indicator = { }
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) Color(0xFFB59A7A)
                        else Color.Transparent
                    )
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 14.sp
                )
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
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
                    color = Color(0xFF212121),
                    maxLines = 1
                )
                Text(
                    text = garment.subcategory ?: "Sin categoría",
                    fontSize = 12.sp,
                    color = Color.Gray,
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
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tu armario está vacío",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Comienza añadiendo tu primera prenda",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddGarment,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB59A7A)
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