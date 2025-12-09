package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.local.entity.OutfitEntity
import com.activity.closetly.project_closedly.data.repository.GarmentRepository
import com.activity.closetly.project_closedly.data.repository.OutfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class OutfitDetailViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository,
    private val garmentRepository: GarmentRepository
) : ViewModel() {

    private val _outfit = MutableStateFlow<OutfitEntity?>(null)
    val outfit: StateFlow<OutfitEntity?> = _outfit.asStateFlow()

    private val _garments = MutableStateFlow<List<GarmentEntity>>(emptyList())
    val garments: StateFlow<List<GarmentEntity>> = _garments.asStateFlow()

    private val _deletionSuccess = MutableSharedFlow<Boolean>()
    val deletionSuccess = _deletionSuccess.asSharedFlow()

    fun loadOutfit(outfitId: String) {
        viewModelScope.launch {
            val loadedOutfit = outfitRepository.getOutfitById(outfitId)
            _outfit.value = loadedOutfit

            loadedOutfit?.let { outfit ->
                val garmentIds = try {
                    val jsonArray = JSONArray(outfit.garmentIds)
                    List(jsonArray.length()) { jsonArray.getString(it) }
                } catch (e: Exception) {
                    emptyList()
                }

                // Cargar todas las prendas del usuario
                val allGarments = garmentRepository.getAllGarmentsByUser(outfit.userId).first()

                // Filtrar solo las prendas que están en el outfit
                val outfitGarments = allGarments.filter { it.id in garmentIds }
                _garments.value = outfitGarments
            }
        }
    }

    fun deleteOutfit() {
        viewModelScope.launch {
            _outfit.value?.let { outfit ->
                outfitRepository.deleteOutfit(outfit)
                _deletionSuccess.emit(true)
            }
        }
    }
}