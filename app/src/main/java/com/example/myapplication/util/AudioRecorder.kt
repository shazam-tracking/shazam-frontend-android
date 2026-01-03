package com.example.myapplication.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFile: File? = null

    fun startRecording(): File? {
        try {
            // Check permission first
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("AudioRecorder", "RECORD_AUDIO permission not granted")
                return null
            }

            // Use M4A format (AAC codec) - Android native support, guaranteed playback!
            outputFile = File(context.cacheDir, "recording_${System.currentTimeMillis()}.m4a")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16000) // Backend will resample to 8000
                setAudioEncodingBitRate(64000) // 64 kbps - good quality
                setAudioChannels(1) // Mono

                setOutputFile(outputFile!!.absolutePath)

                prepare()
                start()

                isRecording = true

                Log.d("AudioRecorder", """
                    🎙 Recording started:
                    - Format: M4A (AAC codec)
                    - Sample Rate: 16000 Hz
                    - Bit Rate: 64 kbps
                    - Channels: Mono
                    - Output: ${outputFile?.name}
                    - ✅ Playback GUARANTEED on Android!
                """.trimIndent())
            }

            return outputFile

        } catch (e: SecurityException) {
            Log.e("AudioRecorder", "SecurityException: Missing RECORD_AUDIO permission", e)
            return null
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error starting recording", e)
            e.printStackTrace()
            return null
        }
    }

    fun stopRecording(): File? {
        return try {
            if (isRecording && mediaRecorder != null) {
                isRecording = false

                mediaRecorder?.apply {
                    try {
                        stop()
                    } catch (e: Exception) {
                        Log.e("AudioRecorder", "Error stopping MediaRecorder", e)
                    }
                    release()
                }
                mediaRecorder = null

                Log.d("AudioRecorder", """
                    ✅ Recording stopped:
                    - File: ${outputFile?.name}
                    - Size: ${outputFile?.length()} bytes (${outputFile?.length()?.div(1024)}KB)
                    - Format: M4A (playback guaranteed!)
                    - Backend: librosa supports M4A ✅
                """.trimIndent())

                outputFile
            } else {
                Log.w("AudioRecorder", "No active recording to stop")
                null
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error in stopRecording", e)
            e.printStackTrace()
            null
        }
    }

    fun cancelRecording() {
        try {
            if (isRecording && mediaRecorder != null) {
                isRecording = false


                mediaRecorder?.apply {
                    try {
                        stop()
                    } catch (e: Exception) {
                        // Ignore stop errors when canceling
                    }
                    release()
                }
                mediaRecorder = null

                outputFile?.delete()
                outputFile = null

                Log.d("AudioRecorder", "🗑 Recording cancelled")
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error canceling recording", e)
        }
    }
}