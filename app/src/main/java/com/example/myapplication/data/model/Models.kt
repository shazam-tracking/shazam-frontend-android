package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

// ==========================================
// Auth Models
// ==========================================

data class SignUpRequest(
    val email: String,
    val password: String,
    @SerializedName("full_name") val fullName: String
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class GoogleSignInRequest(
    @SerializedName("idToken") val idToken: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    @SerializedName("new_password") val newPassword: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class MessageResponse(
    val message: String
)

// ==========================================
// User Profile Models
// ==========================================

data class UserProfileResponse(
    val status: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("profile_picture_url") val profilePictureUrl: String?,
    @SerializedName("auth_provider") val authProvider: String
)

// ==========================================
// Index/Fingerprint Models
// ==========================================

data class IndexResponse(
    val status: String,
    val message: String
)

// ==========================================
// Recognition Models
// ==========================================

data class RecognitionResponse(
    val status: String,
    val match: Boolean,
    val message: String? = null,
    val data: RecognitionData? = null
)

data class RecognitionData(
    val title: String,
    val artist: String,
    val album: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("track_url") val trackUrl: String?,
    @SerializedName("artist_image_url") val artistImageUrl: String?,
    @SerializedName("release_year") val releaseYear: Int?,
    val tempo: Float?,
    val energy: Float?,
    val dancability: Float?,
    @SerializedName("musical_key") val musicalKey: String?,
    @SerializedName("offset_seconds") val offsetSeconds: Float,
    val score: Int,
    val alternatives: List<AlternativeSong>? = null
)

data class AlternativeSong(
    val title: String,
    val artist: String,
    val album: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("track_url") val trackUrl: String?,
    @SerializedName("artist_image_url") val artistImageUrl: String?,
    @SerializedName("release_year") val releaseYear: Int?,
    val tempo: Float?,
    val energy: Float?,
    val dancability: Float?,
    @SerializedName("musical_key") val musicalKey: String?,
    @SerializedName("match_time_seconds") val matchTimeSeconds: Float,
    val score: Int
)