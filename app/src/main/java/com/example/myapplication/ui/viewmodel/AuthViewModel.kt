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

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            repository.signUp(email, password, fullName).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _authState.value = AuthState(
                            isSuccess = true,
                            message = result.data
                        )
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            repository.signIn(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _authState.value = AuthState(
                            isSuccess = true,
                            message = result.data
                        )
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun googleSignIn(idToken: String) {
        viewModelScope.launch {
            repository.googleSignIn(idToken).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _authState.value = AuthState(
                            isSuccess = true,
                            message = result.data
                        )
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            repository.forgotPassword(email).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _authState.value = AuthState(
                            isSuccess = true,
                            message = result.data
                        )
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState()
    }
}