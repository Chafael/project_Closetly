package com.activity.closetly.project_closedly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.local.entity.GarmentEntity
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.remote.FirebaseAuthService
import com.activity.closetly.project_closedly.data.repository.AuthRepository
import com.activity.closetly.project_closedly.data.repository.GarmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val garmentRepository: GarmentRepository,
    private val authRepository: AuthRepository,
    private val authService: FirebaseAuthService
) : ViewModel() {

    private val userId = authRepository.currentUser?.uid ?: ""

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _profilePhotoUrl = MutableStateFlow("")
    val profilePhotoUrl: StateFlow<String> = _profilePhotoUrl.asStateFlow()

    private val _userInitial = MutableStateFlow("U")
    val userInitial: StateFlow<String> = _userInitial.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            when (val result = authService.getUserData()) {
                is AuthResult.Success -> {
                    val data = result.data
                    _profilePhotoUrl.value = data["profilePhotoUrl"] as? String ?: ""
                    val username = data["username"] as? String ?: ""
                    _userInitial.value = username.firstOrNull()?.uppercase() ?: "U"
                }
                else -> {}
            }
        }
    }

    val garments: StateFlow<List<GarmentEntity>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == "Todas") {
                garmentRepository.getAllGarmentsByUser(userId)
            } else {
                garmentRepository.getGarmentsByCategory(userId, category)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val garmentCount: StateFlow<Int> = garmentRepository.getGarmentCount(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun deleteGarment(garment: GarmentEntity) {
        viewModelScope.launch {
            garmentRepository.deleteGarment(garment)
        }
    }

    fun toggleFavorite(garment: GarmentEntity) {
        viewModelScope.launch {
            garmentRepository.updateFavoriteStatus(garment.id, !garment.isFavorite)
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }
}
