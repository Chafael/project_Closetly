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

    fun loadGarment(garmentId: String) {
        viewModelScope.launch {
        }
    }

    fun setRating(rating: Int) {
        _selectedRating.value = rating
    }

    fun saveRating() {
        viewModelScope.launch {
        }
    }
}