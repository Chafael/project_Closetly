package com.activity.closetly.project_closedly.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.remote.CloudinaryService
import com.activity.closetly.project_closedly.data.remote.UploadResult
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import com.activity.closetly.project_closedly.data.repository.GarmentRepository
import com.activity.closetly.project_closedly.model.UploadGarmentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UploadGarmentViewModel @Inject constructor(
    private val garmentRepository: GarmentRepository,
    private val cloudinaryService: CloudinaryService,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(UploadGarmentState())
        private set

    private val userId = authRepository.currentUser?.uid ?: ""

    fun onImageSelected(uri: Uri) {
        uiState = uiState.copy(imageUri = uri, errorMessage = null)
    }

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name, errorMessage = null)
    }

    fun onCategoryChange(category: String) {
        uiState = uiState.copy(category = category, errorMessage = null)
    }

    fun onSubcategoryChange(subcategory: String) {
        uiState = uiState.copy(subcategory = subcategory, errorMessage = null)
    }

    fun onColorChange(color: String) {
        uiState = uiState.copy(color = color, errorMessage = null)
    }

    fun onBrandChange(brand: String) {
        uiState = uiState.copy(brand = brand, errorMessage = null)
    }

    fun onSeasonChange(season: String) {
        uiState = uiState.copy(season = season, errorMessage = null)
    }

    fun onUploadClicked() {
        if (uiState.imageUri == null) {
            uiState = uiState.copy(errorMessage = "Por favor selecciona una imagen")
            return
        }

        if (uiState.name.isBlank()) {
            uiState = uiState.copy(errorMessage = "El nombre es obligatorio")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            when (val result = cloudinaryService.uploadImage(uiState.imageUri!!, userId)) {
                is UploadResult.Success -> {
                    val garment = GarmentEntity(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        name = uiState.name,
                        category = uiState.category,
                        subcategory = uiState.subcategory,
                        color = uiState.color.ifBlank { null },
                        brand = uiState.brand.ifBlank { null },
                        season = uiState.season,
                        imageUrl = result.data
                    )

                    garmentRepository.insertGarment(garment)

                    uiState = uiState.copy(
                        isLoading = false,
                        isUploadSuccess = true
                    )
                }
                is UploadResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun resetUploadSuccess() {
        uiState = uiState.copy(isUploadSuccess = false)
    }
}