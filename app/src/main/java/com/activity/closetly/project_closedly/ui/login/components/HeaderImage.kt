package com.activity.closetly.project_closedly.ui.login.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderImage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Closetly",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF705840)  // Color especificado
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tu Armario Digital",
            fontSize = 16.sp,
            color = Color(0xFF9E8B7B)  // Color más claro para el subtítulo
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Crear cuenta",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF705840)  // Color especificado
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Únete a la comunidad de moda",
            fontSize = 14.sp,
            color = Color(0xFF9E8B7B)  // Color más suave para el subtítulo
        )
    }
}