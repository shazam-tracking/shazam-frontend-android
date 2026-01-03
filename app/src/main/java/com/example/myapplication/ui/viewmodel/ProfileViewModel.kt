package com.example.myapplication.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.UserData
import com.example.myapplication.data.repository.AppRepository
import com.example.myapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val userData: UserData? = null,
    val error: String? = null
)

data class EditProfileState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _editState = MutableStateFlow(EditProfileState())
    val editState: StateFlow<EditProfileState> = _editState.asStateFlow()

    fun loadUserProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _profileState.value = ProfileState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _profileState.value = ProfileState(userData = result.data)
                    }
                    is Resource.Error -> {
                        _profileState.value = ProfileState(error = result.message)
                    }
                }
            }
        }
    }

    fun updateProfile(
        context: Context,
        fullName: String,
        currentPassword: String?,
        newPassword: String?,
        profilePictureUri: Uri?
    ) {
        viewModelScope.launch {
            repository.updateProfile(
                context = context,
                fullName = fullName,
                currentPassword = currentPassword,
                newPassword = newPassword,
                profilePictureUri = profilePictureUri
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _editState.value = EditProfileState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _editState.value = EditProfileState(isSuccess = true)
                        loadUserProfile()
                    }
                    is Resource.Error -> {
                        _editState.value = EditProfileState(error = result.message)
                    }
                }
            }
        }
    }

    fun setEditError(message: String) {
        _editState.value = EditProfileState(error = message)
    }

    fun resetEditState() {
        _editState.value = EditProfileState()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}