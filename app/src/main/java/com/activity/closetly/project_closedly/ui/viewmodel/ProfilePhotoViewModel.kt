package com.activity.closetly.project_closedly.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.remote.CloudinaryService
import com.activity.closetly.project_closedly.data.remote.FirebaseAuthService
import com.activity.closetly.project_closedly.data.remote.UploadResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProfilePhotoViewModel"

sealed class ProfilePhotoState {
    data object Idle : ProfilePhotoState()
    data object Loading : ProfilePhotoState()
    data class Success(val message: String) : ProfilePhotoState()
    data class Error(val message: String) : ProfilePhotoState()
}

data class ProfilePhotoUiState(
    val currentPhotoUrl: String = "",
    val selectedImageUri: Uri? = null,
    val uploadState: ProfilePhotoState = ProfilePhotoState.Idle,
    val userInitial: String = "U"
)

@HiltViewModel
class ProfilePhotoViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val cloudinaryService: CloudinaryService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfilePhotoUiState())
    val uiState: StateFlow<ProfilePhotoUiState> = _uiState.asStateFlow()

    init {
        loadCurrentPhoto()
    }

    private fun loadCurrentPhoto() {
        viewModelScope.launch {
            when (val result = authService.getUserData()) {
                is AuthResult.Success -> {
                    val data = result.data
                    val photoUrl = data["profilePhotoUrl"] as? String ?: ""
                    val username = data["username"] as? String ?: ""
                    val initial = username.firstOrNull()?.uppercase() ?: "U"
                    
                    _uiState.update { 
                        it.copy(
                            currentPhotoUrl = photoUrl,
                            userInitial = initial
                        )
                    }
                    Log.d(TAG, "Foto de perfil cargada: $photoUrl")
                }
                is AuthResult.Error -> {
                    Log.e(TAG, "Error al cargar foto de perfil: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun setSelectedImage(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun uploadPhoto() {
        val imageUri = _uiState.value.selectedImageUri
        
        if (imageUri == null) {
            _uiState.update { 
                it.copy(uploadState = ProfilePhotoState.Error("Selecciona una imagen"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(uploadState = ProfilePhotoState.Loading) }
            
            try {
                val currentUser = authService.currentUser
                if (currentUser == null) {
                    _uiState.update { 
                        it.copy(uploadState = ProfilePhotoState.Error("No se pudo completar la acción"))
                    }
                    return@launch
                }

                when (val uploadResult = cloudinaryService.uploadImage(imageUri, currentUser.uid)) {
                    is UploadResult.Success -> {
                        val photoUrl = uploadResult.data
                        
                        when (val result = authService.updateProfilePhoto(photoUrl)) {
                            is AuthResult.Success -> {
                                _uiState.update { 
                                    it.copy(
                                        uploadState = ProfilePhotoState.Success("La acción se completó exitosamente"),
                                        currentPhotoUrl = photoUrl
                                    )
                                }
                                Log.d(TAG, "Foto de perfil subida exitosamente")
                            }
                            is AuthResult.Error -> {
                                _uiState.update { 
                                    it.copy(uploadState = ProfilePhotoState.Error("No se pudo completar la acción"))
                                }
                            }
                            else -> {}
                        }
                    }
                    is UploadResult.Error -> {
                        _uiState.update { 
                            it.copy(uploadState = ProfilePhotoState.Error("No se pudo completar la acción"))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al subir foto: ${e.message}", e)
                _uiState.update { 
                    it.copy(uploadState = ProfilePhotoState.Error("No se pudo completar la acción"))
                }
            }
        }
    }

    fun dismissUploadState() {
        _uiState.update { it.copy(uploadState = ProfilePhotoState.Idle) }
    }
}
