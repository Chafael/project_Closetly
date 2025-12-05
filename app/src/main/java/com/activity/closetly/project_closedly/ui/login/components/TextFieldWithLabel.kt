package com.activity.closetly.project_closedly.ui.login.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    enabled: Boolean = true,
    isError: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = if (isError) Color(0xFFD32F2F) else Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.LightGray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isError) Color(0xFFD32F2F) else Color.LightGray,
                focusedBorderColor = if (isError) Color(0xFFD32F2F) else Color(0xFFB59A7A),
<<<<<<< HEAD

                cursorColor = if (isError) Color(0xFFD32F2F) else Color(0xFFB59A7A),

                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                disabledTextColor = Color.Gray,

                focusedTextColor = Color(0xFF212121),
                unfocusedTextColor = Color(0xFF424242),

                errorBorderColor = Color(0xFFD32F2F),
                errorCursorColor = Color(0xFFD32F2F),
                errorLabelColor = Color(0xFFD32F2F),

=======
                cursorColor = if (isError) Color(0xFFD32F2F) else Color(0xFFB59A7A),
                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                disabledTextColor = Color.Gray,
                focusedTextColor = Color(0xFF212121),
                unfocusedTextColor = Color(0xFF424242),
                errorBorderColor = Color(0xFFD32F2F),
                errorCursorColor = Color(0xFFD32F2F),
                errorLabelColor = Color(0xFFD32F2F),
>>>>>>> register
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White
            ),
            singleLine = true,
            enabled = enabled,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = {
                if (isPassword) {
                    val icon = if (passwordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }
                    val description = if (passwordVisible) {
                        "Ocultar contraseña"
                    } else {
                        "Mostrar contraseña"
                    }
                    IconButton(onClick = onPasswordToggle, enabled = enabled) {
                        Icon(
                            imageVector = icon,
                            contentDescription = description,
                            tint = if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        )
    }
}