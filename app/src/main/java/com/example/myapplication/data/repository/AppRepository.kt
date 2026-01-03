package com.example.myapplication.data.repository

import android.content.Context
import android.net.Uri
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
import java.io.FileOutputStream
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

    fun signUp(email: String, password: String, fullName: String): Flow<Resource<String>> = flow {
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

    fun signIn(email: String, password: String): Flow<Resource<String>> = flow {
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

    fun googleSignIn(idToken: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val request = GoogleSignInRequest(idToken)
            val response = apiService.googleSignIn(request)

            if (response.isSuccessful) {
                val authResponse = response.body()
                authResponse?.let {
                    tokenManager.saveToken(it.accessToken)
                    emit(Resource.Success("Login successful"))
                } ?: emit(Resource.Error("No response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Google sign-in failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun forgotPassword(email: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val request = ForgotPasswordRequest(email)
            val response = apiService.forgotPassword(request)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.message ?: "Reset link sent to your email"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Failed to send reset link"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
    suspend fun logout() {
        tokenManager.clearToken()
    }

    // ==========================================
    // USER PROFILE
    // ==========================================

    fun getUserProfile(): Flow<Resource<UserData>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getCurrentUser()

            if (response.isSuccessful) {
                val userData = response.body()?.data
                userData?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("No user data"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Resource.Error(errorBody ?: "Failed to load profile"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun updateProfile(
        context: Context,
        fullName: String,
        currentPassword: String?,
        newPassword: String?,
        profilePictureUri: Uri?
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "📝 UPDATE PROFILE REQUEST")
            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "Full Name: $fullName")
            android.util.Log.d("AppRepository", "Changing Password: ${newPassword != null}")
            android.util.Log.d("AppRepository", "Profile Picture: ${profilePictureUri != null}")

            // Prepare full name
            val fullNamePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())

            // Prepare password (only if changing)
            val passwordPart = if (currentPassword != null && newPassword != null) {
                newPassword.toRequestBody("text/plain".toMediaTypeOrNull())
            } else null

            // Prepare profile picture
            val imagePart = profilePictureUri?.let { uri ->
                android.util.Log.d("AppRepository", "Converting URI to File: $uri")
                val file = uriToFile(context, uri)
                android.util.Log.d("AppRepository", "File created: ${file.absolutePath}, Size: ${file.length()} bytes")

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }

            android.util.Log.d("AppRepository", "Sending update request to backend...")

            val response = apiService.updateProfile(
                fullName = fullNamePart,
                password = passwordPart,
                profilePicture = imagePart
            )

            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "📥 BACKEND RESPONSE")
            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "HTTP Status: ${response.code()}")
            android.util.Log.d("AppRepository", "Is Successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                android.util.Log.d("AppRepository", "✅ Profile updated successfully!")
                emit(Resource.Success("Profile updated successfully"))
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AppRepository", "❌ Error: $errorBody")
                emit(Resource.Error(errorBody ?: "Failed to update profile"))
            }

            android.util.Log.d("AppRepository", "========================================")

        } catch (e: Exception) {
            android.util.Log.e("AppRepository", "========================================")
            android.util.Log.e("AppRepository", "💥 EXCEPTION: ${e.message}")
            android.util.Log.e("AppRepository", "Stack Trace:", e)
            android.util.Log.e("AppRepository", "========================================")
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val fileName = "profile_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    // ==========================================
    // FINGERPRINTING
    // ==========================================

    fun indexSongFromSpotify(spotifyUrl: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "🎵 INDEX SONG FROM SPOTIFY")
            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "Spotify URL: $spotifyUrl")

            val urlBody = spotifyUrl.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.indexSpotifyUrl(urlBody)

            android.util.Log.d("AppRepository", "HTTP Status: ${response.code()}")
            android.util.Log.d("AppRepository", "Is Successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                android.util.Log.d("AppRepository", "✅ Song indexed successfully!")
                emit(Resource.Success(response.body()?.message ?: "Song indexed successfully"))
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AppRepository", "❌ Error: $errorBody")
                emit(Resource.Error(errorBody ?: "Indexing failed"))
            }

            android.util.Log.d("AppRepository", "========================================")

        } catch (e: Exception) {
            android.util.Log.e("AppRepository", "💥 EXCEPTION: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

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
                file.name.endsWith(".m4a", ignoreCase = true) -> "audio/mp4"
                file.name.endsWith(".ogg", ignoreCase = true) -> "audio/ogg"
                else -> "audio/*"
            }

            android.util.Log.d("AppRepository", "📊 File Type: ${file.name.substringAfterLast('.')}")
            android.util.Log.d("AppRepository", "📊 MIME Type: $mimeType")

            // Create multipart request
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            android.util.Log.d("AppRepository", "📤 Sending request to backend...")

            val response = apiService.recognizeSong(filePart)

            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "📥 BACKEND RESPONSE")
            android.util.Log.d("AppRepository", "========================================")
            android.util.Log.d("AppRepository", "📥 HTTP Status: ${response.code()}")
            android.util.Log.d("AppRepository", "📥 Is Successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                response.body()?.let { result ->
                    android.util.Log.d("AppRepository", "✅ SUCCESS - Match: ${result.match}")

                    if (result.match && result.data != null) {
                        android.util.Log.d("AppRepository", "🎵 Song: ${result.data.title} by ${result.data.artist}")
                    }

                    emit(Resource.Success(result))
                } ?: run {
                    android.util.Log.e("AppRepository", "❌ ERROR: Response body is null!")
                    emit(Resource.Error("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AppRepository", "❌ Error: $errorBody")
                emit(Resource.Error(errorBody ?: "Recognition failed"))
            }

            android.util.Log.d("AppRepository", "========================================")

        } catch (e: Exception) {
            android.util.Log.e("AppRepository", "💥 EXCEPTION: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Network error occurred"))
        }
    }
}