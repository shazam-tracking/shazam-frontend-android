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
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _signUpState = MutableStateFlow(AuthState())
    val signUpState: StateFlow<AuthState> = _signUpState.asStateFlow()

    private val _signInState = MutableStateFlow(AuthState())
    val signInState: StateFlow<AuthState> = _signInState.asStateFlow()

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            repository.signUp(email, password, fullName).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _signUpState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _signUpState.value = AuthState(successMessage = result.data)
                    }
                    is Resource.Error -> {
                        _signUpState.value = AuthState(errorMessage = result.message)
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
                        _signInState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _signInState.value = AuthState(successMessage = result.data)
                    }
                    is Resource.Error -> {
                        _signInState.value = AuthState(errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun resetSignUpState() {
        _signUpState.value = AuthState()
    }

    fun resetSignInState() {
        _signInState.value = AuthState()
    }
}