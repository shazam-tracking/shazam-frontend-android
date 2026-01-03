package com.example.myapplication.ui.viewmodel

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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

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

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}