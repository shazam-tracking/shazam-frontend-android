package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.RecognitionResponse
import com.example.myapplication.data.repository.AppRepository
import com.example.myapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class RecognitionState(
    val isListening: Boolean = false,
    val isProcessing: Boolean = false,
    val recognitionResult: RecognitionResponse? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class MusicRecognitionViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _recognitionState = MutableStateFlow(RecognitionState())
    val recognitionState: StateFlow<RecognitionState> = _recognitionState.asStateFlow()

    fun startListening() {
        _recognitionState.value = _recognitionState.value.copy(
            isListening = true,
            errorMessage = null
        )
    }

    fun stopListening() {
        _recognitionState.value = _recognitionState.value.copy(isListening = false)
    }

    fun recognizeSong(audioFile: File) {
        viewModelScope.launch {
            repository.recognizeSong(audioFile).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _recognitionState.value = _recognitionState.value.copy(
                            isProcessing = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Success -> {
                        _recognitionState.value = _recognitionState.value.copy(
                            isProcessing = false,
                            isListening = false,
                            recognitionResult = result.data,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _recognitionState.value = _recognitionState.value.copy(
                            isProcessing = false,
                            isListening = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        _recognitionState.value = RecognitionState()
    }
}