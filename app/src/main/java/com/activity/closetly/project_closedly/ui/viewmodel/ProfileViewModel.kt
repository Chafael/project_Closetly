package com.activity.closetly.project_closedly.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _user = MutableStateFlow(Firebase.auth.currentUser)
    val user = _user.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    init {
        _selectedImageUri.value = Firebase.auth.currentUser?.photoUrl
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun logout() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            _user.value = null
        }
    }
}
