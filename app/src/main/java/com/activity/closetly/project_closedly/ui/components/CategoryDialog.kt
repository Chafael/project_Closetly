package com.activity.closetly.project_closedly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)

data class CategoryItem(
    val name: String,
    val icon: ImageVector
)

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        // CAMBIO AQUÍ: Usamos DoNotStep porque gráficamente es una Bota/Zapato
        CategoryItem("Zapatos", Icons.Default.DoNotStep),
        CategoryItem("Accesorios", Icons.Default.Watch),
        CategoryItem("Bolsos", Icons.Default.LocalMall),
        CategoryItem("Ropa interior", Icons.Default.Checkroom),
        CategoryItem("Trajes de baño", Icons.Default.Pool),
        CategoryItem("Deportiva", Icons.Default.FitnessCenter),
        CategoryItem("Vestidos", Icons.Default.Woman),
        CategoryItem("Chaquetas", Icons.Default.AcUnit)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Más categorías",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(categories) { category ->
                        CategoryItemRow(
                            category = category,
                            onClick = {
                                onCategorySelected(category.name)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        "Cerrar",
                        color = LightBrown,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItemRow(
    category: CategoryItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = LightBrown.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.name,
                        tint = LightBrown,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Text(
                text = category.name,
                fontSize = 16.sp,
                color = PrimaryBrown,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SecondaryGray.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}