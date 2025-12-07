package com.activity.closetly.project_closedly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
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

enum class DialogType {
    SUCCESS, WARNING, ERROR
}

@Composable
fun CustomDialog(
    type: DialogType,
    title: String,
    message: String,
    dismissButtonText: String = "Cerrar",
    confirmButtonText: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null
) {
    val (borderColor, backgroundColor, icon, iconColor, buttonColor) = when (type) {
        DialogType.SUCCESS -> listOf(
            Color(0xFF10B981),
            Color(0xFFECFDF5),
            Icons.Default.Check,
            Color(0xFF10B981),
            Color(0xFF10B981)
        )
        DialogType.WARNING -> listOf(
            Color(0xFFF59E0B),
            Color(0xFFFEF3C7),
            Icons.Default.Warning,
            Color(0xFFF59E0B),
            Color(0xFFDC2626)
        )
        DialogType.ERROR -> listOf(
            Color(0xFFDC2626),
            Color(0xFFFEE2E2),
            Icons.Default.Close,
            Color(0xFFDC2626),
            Color(0xFFDC2626)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, borderColor as Color, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = backgroundColor as Color
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = icon as ImageVector,
                                contentDescription = null,
                                tint = iconColor as Color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (type) {
                                DialogType.SUCCESS -> Color(0xFF059669)
                                DialogType.WARNING -> Color(0xFFD97706)
                                DialogType.ERROR -> Color(0xFFDC2626)
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF6B7280)
                        )
                    ) {
                        Text(
                            text = dismissButtonText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (confirmButtonText != null && onConfirm != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor as Color
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = confirmButtonText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}