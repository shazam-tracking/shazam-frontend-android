package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AppRepository
import com.example.myapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FingerprintState(
    val isIndexing: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class FingerprintViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _fingerprintState = MutableStateFlow(FingerprintState())
    val fingerprintState: StateFlow<FingerprintState> = _fingerprintState.asStateFlow()

    fun indexSongFromSpotify(spotifyUrl: String) {
        viewModelScope.launch {
            repository.indexSongFromSpotify(spotifyUrl).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _fingerprintState.value = FingerprintState(isIndexing = true)
                    }
                    is Resource.Success -> {
                        _fingerprintState.value = FingerprintState(
                            isSuccess = true,
                            successMessage = result.data ?: "Song indexed successfully"
                        )
                    }
                    is Resource.Error -> {
                        _fingerprintState.value = FingerprintState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        _fingerprintState.value = FingerprintState()
    }
}