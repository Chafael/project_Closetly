package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import com.activity.closetly.project_closedly.data.repository.GarmentRepository
import com.activity.closetly.project_closedly.data.repository.OutfitRepository
import com.activity.closetly.project_closedly.model.CreateOutfitState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateOutfitViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository,
    private val garmentRepository: GarmentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val userId = authRepository.currentUser?.uid ?: ""

    var uiState by mutableStateOf(CreateOutfitState())
        private set

    val availableGarments: StateFlow<List<GarmentEntity>> =
        garmentRepository.getAllGarmentsByUser(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name, errorMessage = null)
    }

    fun onDescriptionChange(description: String) {
        uiState = uiState.copy(description = description, errorMessage = null)
    }

    fun onOccasionChange(occasion: String) {
        uiState = uiState.copy(occasion = occasion, errorMessage = null)
    }

    fun onSeasonChange(season: String) {
        uiState = uiState.copy(season = season, errorMessage = null)
    }

    fun toggleGarmentSelection(garmentId: String) {
        val currentSelection = uiState.selectedGarmentIds.toMutableList()
        if (currentSelection.contains(garmentId)) {
            currentSelection.remove(garmentId)
        } else {
            currentSelection.add(garmentId)
        }
        uiState = uiState.copy(selectedGarmentIds = currentSelection, errorMessage = null)
    }

    fun onCreateClicked() {
        if (uiState.name.isBlank()) {
            uiState = uiState.copy(errorMessage = "El nombre del outfit es obligatorio")
            return
        }

        if (uiState.selectedGarmentIds.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Selecciona al menos una prenda")
            return
        }

        if (uiState.selectedGarmentIds.size < 2) {
            uiState = uiState.copy(errorMessage = "Un outfit debe tener al menos 2 prendas")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val garmentIdsJson = JSONArray(uiState.selectedGarmentIds).toString()

                val outfit = OutfitEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = uiState.name,
                    description = uiState.description.ifBlank { null },
                    garmentIds = garmentIdsJson,
                    occasion = uiState.occasion,
                    season = uiState.season.ifBlank { null },
                    rating = 0
                )

                outfitRepository.insertOutfit(outfit)

                uiState = uiState.copy(
                    isLoading = false,
                    isCreateSuccess = true
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Error al crear el outfit: ${e.message}"
                )
            }
        }
    }

    fun resetCreateSuccess() {
        uiState = uiState.copy(isCreateSuccess = false)
    }
}