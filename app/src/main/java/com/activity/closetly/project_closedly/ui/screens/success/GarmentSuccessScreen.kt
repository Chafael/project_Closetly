package com.activity.closetly.project_closedly.ui.screens.success

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val PrimaryBrown = Color(0xFF705840)
private val LightBrown = Color(0xFFA28460)
private val SecondaryGray = Color(0xFF6B7280)

@Composable
fun GarmentSuccessScreen(
    onNavigateToWardrobe: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToWardrobe()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Prenda Guardada!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = LightBrown.copy(alpha = 0.3f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Éxito",
                    tint = LightBrown,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "¡Genial!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tu prenda se agregó exitosamente a tu armario digital",
            fontSize = 16.sp,
            color = SecondaryGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = LightBrown,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¡Consejo!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ahora puedes:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryBrown
            )

            Spacer(modifier = Modifier.height(8.dp))

            BulletPoint("Combinarla con otras prendas para crear outfits")
            BulletPoint("Usar las recomendaciones inteligentes")
            BulletPoint("Filtrar por categorías en tu armario")
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "•",
            fontSize = 14.sp,
            color = SecondaryGray,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = SecondaryGray,
            lineHeight = 20.sp
        )
    }
}