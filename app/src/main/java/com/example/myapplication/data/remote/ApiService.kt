package com.example.myapplication.data.remote

import com.example.myapplication.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication endpoints
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<MessageResponse>

    @POST("auth/signin")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    // Fingerprint/Index endpoints
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

    @Multipart
    @POST("api/recognize")
    suspend fun recognizeSong(
        @Part file: MultipartBody.Part
    ): Response<RecognitionResponse>
}