package com.example.myapplication.data.repository

import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.model.*
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    // ==========================================
    // AUTHENTICATION
    // ==========================================

    suspend fun signUp(email: String, password: String, fullName: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val request = SignUpRequest(email, password, fullName)
            val response = apiService.signUp(request)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Account created successfully"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Sign up failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    suspend fun signIn(email: String, password: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val request = SignInRequest(email, password)
            val response = apiService.signIn(request)

            if (response.isSuccessful) {
                val authResponse = response.body()
                authResponse?.let {
                    tokenManager.saveToken(it.accessToken)
                    emit(Resource.Success("Login successful"))
                } ?: emit(Resource.Error("No response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Login failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }

    // ==========================================
    // FINGERPRINTING
    // ==========================================

    suspend fun indexSongFromFile(file: File, songName: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val songNameBody = songName.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.indexFile(songNameBody, filePart)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Song indexed successfully"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Indexing failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    suspend fun indexSongFromSpotify(spotifyUrl: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val urlBody = spotifyUrl.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.indexSpotifyUrl(urlBody)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Song indexed successfully"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Indexing failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    suspend fun recognizeSong(file: File): Flow<Resource<RecognitionResponse>> = flow {
        try {
            emit(Resource.Loading())

            val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.recognizeSong(filePart)

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("No response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Recognition failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}