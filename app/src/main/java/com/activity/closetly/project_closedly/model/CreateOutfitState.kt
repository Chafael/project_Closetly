package com.activity.closetly.project_closedly.model

data class CreateOutfitState(
    val name: String = "",
    val description: String = "",
    val selectedGarmentIds: List<String> = emptyList(),
    val occasion: String = "Casual",
    val season: String = "Todo el año",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreateSuccess: Boolean = false
)