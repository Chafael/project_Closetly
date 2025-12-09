package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import com.activity.closetly.project_closedly.data.repository.OutfitRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutfitsViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val userId = auth.currentUser?.uid ?: ""

    private val _selectedOccasion = MutableStateFlow("Todos")
    val selectedOccasion: StateFlow<String> = _selectedOccasion.asStateFlow()

    // Obtener todos los outfits del usuario
    private val allOutfits = outfitRepository.getAllOutfitsByUser(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtrar outfits según la ocasión seleccionada
    val outfits: StateFlow<List<OutfitEntity>> = combine(
        allOutfits,
        _selectedOccasion
    ) { outfits, occasion ->
        if (occasion == "Todos") {
            outfits
        } else {
            outfits.filter { it.occasion == occasion }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val outfitCount: StateFlow<Int> = allOutfits.map { it.size }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun selectOccasion(occasion: String) {
        _selectedOccasion.value = occasion
    }

    fun deleteOutfit(outfit: OutfitEntity) {
        viewModelScope.launch {
            outfitRepository.deleteOutfit(outfit)
        }
    }
}