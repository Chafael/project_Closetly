package com.activity.closetly.project_closedly.ui.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.activity.closetly.project_closedly.model.CreateOutfitState
import com.activity.closetly.project_closedly.ui.viewmodel.CreateOutfitViewModel

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)
private val BackgroundGray = Color(0xFFFAFAFA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOutfitScreen(
    createViewModel: CreateOutfitViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onCreateSuccess: () -> Unit = {},
    onNavigateToWardrobe: () -> Unit = {},
    onNavigateToOutfits: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState = createViewModel.uiState
    val availableGarments by createViewModel.availableGarments.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isCreateSuccess) {
        if (uiState.isCreateSuccess) {
            onCreateSuccess()
            createViewModel.resetCreateSuccess()
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("Crear Outfit", fontWeight = FontWeight.Bold, color = PrimaryBrown) },
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
                selectedTab = 2,
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
                .verticalScroll(scrollState)
                .background(BackgroundGray)
        ) {
            FormSection(
                uiState = uiState,
                onNameChange = createViewModel::onNameChange,
                onDescriptionChange = createViewModel::onDescriptionChange,
                onOccasionChange = createViewModel::onOccasionChange,
                onSeasonChange = createViewModel::onSeasonChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Selecciona prendas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Text(
                "Mínimo 2 prendas",
                fontSize = 14.sp,
                color = SecondaryGray,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            GarmentSelection(
                garments = availableGarments,
                selectedIds = uiState.selectedGarmentIds,
                onToggle = createViewModel::toggleGarmentSelection
            )

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = createViewModel::onCreateClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBrown),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear Outfit", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
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
private fun FormSection(
    uiState: CreateOutfitState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onOccasionChange: (String) -> Unit,
    onSeasonChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        CustomTextField(
            label = "Nombre del Outfit",
            value = uiState.name,
            onValueChange = onNameChange,
            placeholder = "Ej: Outfit casual de verano"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Descripción (Opcional)",
            value = uiState.description,
            onValueChange = onDescriptionChange,
            placeholder = "Describe tu outfit..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "Ocasión",
            value = uiState.occasion,
            options = listOf("Casual", "Formal", "Trabajo", "Fiesta", "Deportivo"),
            onValueChange = onOccasionChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownField(
            label = "Temporada",
            value = uiState.season,
            options = listOf("Todo el año", "Verano", "Invierno", "Primavera", "Otoño"),
            onValueChange = onSeasonChange
        )
    }
}

@Composable
private fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = PrimaryBrown)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedBorderColor = LightBrown,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = PrimaryBrown)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedBorderColor = LightBrown,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onValueChange(option); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun GarmentSelection(
    garments: List<GarmentEntity>,
    selectedIds: List<String>,
    onToggle: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(400.dp)
    ) {
        items(garments) { garment ->
            SelectableGarmentItem(
                garment = garment,
                isSelected = selectedIds.contains(garment.id),
                onToggle = { onToggle(garment.id) }
            )
        }
    }
}

@Composable
private fun SelectableGarmentItem(
    garment: GarmentEntity,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable(onClick = onToggle)
            .then(
                if (isSelected) Modifier.border(3.dp, LightBrown, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFFF0F0F0))
                ) {
                    AsyncImage(
                        model = garment.imageUrl,
                        contentDescription = garment.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(garment.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PrimaryBrown, maxLines = 1)
                    Text(garment.category, fontSize = 12.sp, color = SecondaryGray, maxLines = 1)
                }
            }
            if (isSelected) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = LightBrown
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, "Seleccionado", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}