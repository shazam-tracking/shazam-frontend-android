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

            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "🎵 RECOGNIZE SONG REQUEST")
            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "📁 File Path: ${file.absolutePath}")
            android.util.Log.d("AppRepository", "📁 File Name: ${file.name}")
            android.util.Log.d("AppRepository", "📁 File Exists: ${file.exists()}")
            android.util.Log.d("AppRepository", "📁 File Size: ${file.length()} bytes (${file.length() / 1024}KB)")

            if (!file.exists()) {
                android.util.Log.e("AppRepository", "❌ ERROR: File does not exist!")
                emit(Resource.Error("Audio file not found"))
                return@flow
            }

            if (file.length() == 0L) {
                android.util.Log.e("AppRepository", "❌ ERROR: File is empty!")
                emit(Resource.Error("Audio file is empty"))
                return@flow
            }

            // Auto-detect MIME type from file extension
            val mimeType = when {
                file.name.endsWith(".wav", ignoreCase = true) -> "audio/wav"
                file.name.endsWith(".webm", ignoreCase = true) -> "audio/webm"
                file.name.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
                file.name.endsWith(".ogg", ignoreCase = true) -> "audio/ogg"
                else -> "audio/*"
            }

            android.util.Log.d("AppRepository", "📊 File Type: ${file.name.substringAfterLast('.')}")
            android.util.Log.d("AppRepository", "📊 MIME Type: $mimeType")

            // Create multipart request
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            android.util.Log.d("AppRepository", "📤 Sending request to backend...")
            android.util.Log.d("AppRepository", "   Content-Type: $mimeType")
            android.util.Log.d("AppRepository", "   File name: ${file.name}")

            val response = apiService.recognizeSong(filePart)

            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "📥 BACKEND RESPONSE")
            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "📥 HTTP Status: ${response.code()}")
            android.util.Log.d("AppRepository", "📥 HTTP Message: ${response.message()}")
            android.util.Log.d("AppRepository", "📥 Is Successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                // Log raw JSON for debugging
                val rawJson = response.body()?.let {
                    com.google.gson.Gson().toJson(it)
                } ?: "null"
                android.util.Log.d("AppRepository", "📄 RAW JSON Response:")
                android.util.Log.d("AppRepository", rawJson)


                response.body()?.let { result ->
                    android.util.Log.d("AppRepository", "========================================")
                    android.util.Log.d("AppRepository", "✅ SUCCESS - RECOGNITION RESULT")
                    android.util.Log.d("AppRepository", "========================================")
                    android.util.Log.d("AppRepository", "🔹 Status: ${result.status}")
                    android.util.Log.d("AppRepository", "🔹 Match: ${result.match}")
                    android.util.Log.d("AppRepository", "🔹 Message: ${result.message}")

                    if (result.match && result.data != null) {
                        android.util.Log.d("AppRepository", "========================================")
                        android.util.Log.d("AppRepository", "🎵 SONG DETAILS")
                        android.util.Log.d("AppRepository", "========================================")
                        android.util.Log.d("AppRepository", "🎵 Title: ${result.data.title}")
                        android.util.Log.d("AppRepository", "🎤 Artist: ${result.data.artist}")
                        android.util.Log.d("AppRepository", "💿 Album: ${result.data.album}")
                        android.util.Log.d("AppRepository", "📊 Score: ${result.data.score}")
                        android.util.Log.d("AppRepository", "🎼 Tempo: ${result.data.tempo}")
                        android.util.Log.d("AppRepository", "⚡️ Energy: ${result.data.energy}")
                        android.util.Log.d("AppRepository", "💃 Dancability: ${result.data.dancability}")
                        android.util.Log.d("AppRepository", "🖼 Image URL: ${result.data.imageUrl}")
                        android.util.Log.d("AppRepository", "🔗 Track URL: ${result.data.trackUrl}")

                        // Log alternatives if present
                        result.data.alternatives?.let { alts ->
                            android.util.Log.d("AppRepository", "========================================")
                            android.util.Log.d("AppRepository", "🎭 ALTERNATIVE MATCHES: ${alts.size}")
                            android.util.Log.d("AppRepository", "========================================")
                            alts.forEachIndexed { index, alt ->
                                android.util.Log.d("AppRepository", "${index + 1}. ${alt.title} by ${alt.artist} (Score: ${alt.score})")
                            }
                        }
                    } else {
                        android.util.Log.d("AppRepository", "❌ No match found in database")
                    }

                    android.util.Log.d("AppRepository", "========================================")

                    emit(Resource.Success(result))
                } ?: run {
                    android.util.Log.e("AppRepository", "❌ ERROR: Response body is null!")
                    emit(Resource.Error("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AppRepository", "========================================")
                android.util.Log.e("AppRepository", "❌ API ERROR")
                android.util.Log.e("AppRepository", "========================================")
                android.util.Log.e("AppRepository", "Error Body: $errorBody")
                android.util.Log.e("AppRepository", "========================================")
                emit(Resource.Error(errorBody ?: "Recognition failed"))
            }


        } catch (e: Exception) {
            android.util.Log.e("AppRepository", "========================================")
            android.util.Log.e("AppRepository", "💥 EXCEPTION OCCURRED")
            android.util.Log.e("AppRepository", "========================================")
            android.util.Log.e("AppRepository", "Exception Type: ${e.javaClass.simpleName}")
            android.util.Log.e("AppRepository", "Exception Message: ${e.message}")
            android.util.Log.e("AppRepository", "Localized Message: ${e.localizedMessage}")
            android.util.Log.e("AppRepository", "Stack Trace:", e)
            android.util.Log.e("AppRepository", "========================================")
            emit(Resource.Error(e.localizedMessage ?: "Network error occurred"))
        }
    }
}