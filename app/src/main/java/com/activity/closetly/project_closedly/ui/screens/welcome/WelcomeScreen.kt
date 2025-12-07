package com.activity.closetly.project_closedly.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Sección superior: Confirmación de registro exitoso
        SuccessSection()

        // Sección inferior: Funcionalidades
        FeaturesSection(onContinue = onContinue)
    }
}

@Composable
private fun SuccessSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFB59A7A))
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Círculo con check
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Éxito",
                        tint = Color(0xFFB59A7A),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto de bienvenida
            Text(
                text = "¡Bienvenido a Closetly!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu cuenta se ha creado exitosamente",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun FeaturesSection(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Título
        Text(
            text = "¡Todo listo!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ya puedes comenzar a organizar tu armario y crear outfits increíbles",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Lista de características
        FeatureItem(
            icon = Icons.Outlined.Checkroom,
            iconColor = Color(0xFF4DD0E1),
            title = "Organiza tu armario",
            description = "Clasifica y gestiona todas tus prendas fácilmente"
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureItem(
            icon = Icons.Outlined.Palette,
            iconColor = Color(0xFFEF5350),
            title = "Crea outfits únicos",
            description = "Combina tus prendas y descubre nuevos looks"
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureItem(
            icon = Icons.Outlined.Group,
            iconColor = Color(0xFF66BB6A),
            title = "Comparte con amigos",
            description = "Muestra tus outfits favoritos a la comunidad"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón de continuar
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB59A7A)
            )
        ) {
            Text(
                text = "Comenzar ahora",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Icono con fondo circular
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Texto
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
    }
}