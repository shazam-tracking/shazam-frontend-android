package com.example.myapplication.data.remote

import com.example.myapplication.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================================
    // AUTHENTICATION ENDPOINTS
    // ==========================================

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<MessageResponse>

    @POST("auth/signin")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @POST("auth/google")
    suspend fun googleSignIn(@Body request: GoogleSignInRequest): Response<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    // ==========================================
    // FINGERPRINT/INDEX ENDPOINTS
    // ==========================================

    @Multipart
    @POST("api/index")
    suspend fun indexFile(
        @Part("song_name") songName: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<IndexResponse>

    @Multipart
    @POST("api/index")
    suspend fun indexSpotifyUrl(
        @Part("spotify_url") spotifyUrl: RequestBody
    ): Response<IndexResponse>

    // ==========================================
    // MUSIC RECOGNITION ENDPOINT
    // ==========================================

    @Multipart
    @POST("api/recognize")
    suspend fun recognizeSong(
        @Part file: MultipartBody.Part
    ): Response<RecognitionResponse>

    // ==========================================
    // USER PROFILE ENDPOINTS
    // ==========================================

    @GET("api/user/me")
    suspend fun getCurrentUser(): Response<UserProfileResponse>

    @Multipart
    @PUT("api/user/update")
    suspend fun updateProfile(
        @Part("full_name") fullName: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part profilePicture: MultipartBody.Part?
    ): Response<MessageResponse>
}