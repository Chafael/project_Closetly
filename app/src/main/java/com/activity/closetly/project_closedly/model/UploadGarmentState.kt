package com.activity.closetly.project_closedly.model

import android.net.Uri

data class UploadGarmentState(
    val imageUri: Uri? = null,
    val name: String = "",
    val category: String = "Camisetas",
    val subcategory: String = "Casual",
    val color: String = "",
    val brand: String = "",
    val season: String = "Todo el año",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUploadSuccess: Boolean = false
)