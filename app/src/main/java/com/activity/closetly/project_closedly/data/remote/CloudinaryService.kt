package com.activity.closetly.project_closedly.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "CloudinaryService"

sealed class UploadResult<out T> {
    data class Success<T>(val data: T) : UploadResult<T>()
    data class Error(val message: String) : UploadResult<Nothing>()
}

@Singleton
class CloudinaryService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isInitialized = false

    fun initialize(cloudName: String, apiKey: String, apiSecret: String) {
        if (!isInitialized) {
            val config = mapOf(
                "cloud_name" to cloudName,
                "api_key" to apiKey,
                "api_secret" to apiSecret
            )
            MediaManager.init(context, config)
            isInitialized = true
            Log.d(TAG, "Cloudinary inicializado: $cloudName")
        }
    }

    suspend fun uploadImage(imageUri: Uri, userId: String): UploadResult<String> {
        if (!isInitialized) {
            return UploadResult.Error("Cloudinary no está inicializado")
        }

        return suspendCancellableCoroutine { continuation ->
            try {
                Log.d(TAG, "Iniciando upload: $imageUri")

                MediaManager.get()
                    .upload(imageUri)
                    .unsigned("closetly_preset")
                    .option("folder", "garments/$userId")
                    .option("resource_type", "image")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            Log.d(TAG, "Upload iniciado: $requestId")
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            val progress = (bytes * 100 / totalBytes).toInt()
                            Log.d(TAG, "Progreso: $progress%")
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            val url = resultData["secure_url"] as? String
                            Log.d(TAG, "Upload exitoso: $url")

                            if (url != null) {
                                continuation.resume(UploadResult.Success(url))
                            } else {
                                continuation.resume(UploadResult.Error("URL no encontrada"))
                            }
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            Log.e(TAG, "Error en upload: ${error.description}")
                            continuation.resume(UploadResult.Error(error.description))
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            Log.w(TAG, "Upload reprogramado: ${error.description}")
                        }
                    })
                    .dispatch()

            } catch (e: Exception) {
                Log.e(TAG, "Error iniciando upload: ${e.message}", e)
                continuation.resume(UploadResult.Error(e.message ?: "Error desconocido"))
            }
        }
    }
}
