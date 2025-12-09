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
import kotlin.math.roundToInt

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
            // Load outfit directly (since Repository returns Entity?, maybe we should have a Flow for it too ideally, but let's stick to what we have or adapt)
            // Ideally, we want to observe the Outfit to get updates.
            // Repository methods: getAllOutfitsByUser, getOutfitById (suspend)
            // If getOutfitById is suspend, we won't see changes to the Outfit unless we reload.
            // However, we are mainly interested in Garment changes triggering Outfit updates.
            
            // Let's keep fetching outfit once, but if we update rating, we might need to reflect that locals.
            
            val loadedOutfit = outfitRepository.getOutfitById(outfitId)
            _outfit.value = loadedOutfit

            loadedOutfit?.let { outfit ->
                val garmentIds = try {
                    val jsonArray = JSONArray(outfit.garmentIds)
                    List(jsonArray.length()) { jsonArray.getString(it) }
                } catch (e: Exception) {
                    emptyList()
                }

                if (garmentIds.isNotEmpty()) {
                    garmentRepository.getGarmentsByIds(garmentIds).collect { updatedGarments ->
                        _garments.value = updatedGarments
                        

                        // Calculate rating
                        if (updatedGarments.isNotEmpty()) {
                            val validRatings = updatedGarments.filter { it.rating > 0 }
                            val averageRating = if (validRatings.isNotEmpty()) {
                                validRatings.map { it.rating }.average().roundToInt()
                            } else {
                                0
                            }
                            
                            // If rating changed, update DB and local state
                            if (outfit.rating != averageRating) {
                                val updatedOutfit = outfit.copy(rating = averageRating, updatedAt = System.currentTimeMillis())
                                try {
                                    outfitRepository.updateRating(outfit.id, averageRating)
                                    // Also update local state so UI updates immediately
                                    _outfit.value = updatedOutfit
                                } catch (e: Exception) {
                                    // Handle error silently or log
                                }
                            }
                        }
                    }
                } else {
                    _garments.value = emptyList()
                }
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

    fun saveGarmentRating(garmentId: String, rating: Int) {
        viewModelScope.launch {
            val currentGarments = _garments.value
            val garment = currentGarments.find { it.id == garmentId }
            garment?.let {
                val updatedGarment = it.copy(rating = rating, updatedAt = System.currentTimeMillis())
                garmentRepository.updateGarment(updatedGarment)
                // Flow collection in loadOutfit will automatically pick up this change and update local state + outfit rating
            }
        }
    }
}