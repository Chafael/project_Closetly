package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import com.activity.closetly.project_closedly.data.repository.OutfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutfitsViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val userId = authRepository.currentUser?.uid ?: ""

    private val _selectedOccasion = MutableStateFlow("Todos")
    val selectedOccasion: StateFlow<String> = _selectedOccasion.asStateFlow()

    val outfits: StateFlow<List<OutfitEntity>> = _selectedOccasion
        .flatMapLatest { occasion ->
            if (occasion == "Todos") {
                outfitRepository.getAllOutfitsByUser(userId)
            } else {
                outfitRepository.getOutfitsByOccasion(userId, occasion)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val outfitCount: StateFlow<Int> = outfitRepository.getOutfitCount(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun selectOccasion(occasion: String) {
        _selectedOccasion.value = occasion
    }

    fun deleteOutfit(outfit: OutfitEntity) {
        viewModelScope.launch {
            outfitRepository.deleteOutfit(outfit)
        }
    }

    fun updateRating(outfitId: String, rating: Int) {
        viewModelScope.launch {
            outfitRepository.updateRating(outfitId, rating)
        }
    }

    fun toggleFavorite(outfit: OutfitEntity) {
        viewModelScope.launch {
            outfitRepository.updateFavoriteStatus(outfit.id, !outfit.isFavorite)
        }
    }
}