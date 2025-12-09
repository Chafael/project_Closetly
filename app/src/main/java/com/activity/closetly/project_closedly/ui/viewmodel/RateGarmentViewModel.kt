package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.repository.GarmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateGarmentViewModel @Inject constructor(
    private val garmentRepository: GarmentRepository
) : ViewModel() {

    private val _garment = MutableStateFlow<GarmentEntity?>(null)
    val garment: StateFlow<GarmentEntity?> = _garment.asStateFlow()

    private val _selectedRating = MutableStateFlow(0)
    val selectedRating: StateFlow<Int> = _selectedRating.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadGarment(garmentId: String) {
        viewModelScope.launch {
            val loadedGarment = garmentRepository.getGarmentById(garmentId)
            _garment.value = loadedGarment
            // Cargar el rating actual de la prenda
            _selectedRating.value = loadedGarment?.rating ?: 0
        }
    }

    fun setRating(rating: Int) {
        _selectedRating.value = rating
    }

    fun saveRating(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true

            _garment.value?.let { currentGarment ->
                // Actualizar el garment con el nuevo rating y updatedAt
                val updatedGarment = currentGarment.copy(
                    rating = _selectedRating.value,
                    updatedAt = System.currentTimeMillis()
                )

                // Guardar en la base de datos
                garmentRepository.updateGarment(updatedGarment)

                // Actualizar el estado local
                _garment.value = updatedGarment

                _isSaving.value = false
                onSuccess()
            }
        }
    }
}