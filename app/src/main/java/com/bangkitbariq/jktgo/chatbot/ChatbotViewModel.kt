package com.bangkitbariq.jktgo.chatbot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class ChatbotViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(TAG, "Request URL: ${request.url}")
            Log.d(TAG, "Request Headers: ${request.headers}")

            val response = chain.proceed(request)
            Log.d(TAG, "Response Code: ${response.code}")
            Log.d(TAG, "Response Headers: ${response.headers}")

            // Safely log response body
            response.body?.let { responseBody ->
                val contentType = responseBody.contentType()
                val bodyString = responseBody.string()
                Log.d(TAG, "Response Body: $bodyString")

                // Create new response with a new body
                return@addInterceptor response.newBuilder()
                    .body(ResponseBody.create(contentType, bodyString))
                    .build()
            } ?: response
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://34.128.86.142:5000/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ChatbotAPI::class.java)

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                _networkState.value = NetworkState.LOADING

                Log.d(TAG, "Sending message: $message")
                addMessage(ChatMessage(text = message, isUser = true))

                val requestBody = ChatRequestBody(text = message)
                var retryCount = 0
                var lastException: Exception? = null

                while (retryCount < 3) {
                    try {
                        val response = api.sendMessage(requestBody)
                        if (response.isSuccessful) {
                            handleSuccessResponse(response)
                            _networkState.value = NetworkState.IDLE
                            return@launch
                        } else {
                            handleErrorResponse(response)
                            break
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Attempt ${retryCount + 1} failed", e)
                        lastException = e
                        retryCount++
                        if (retryCount < 3) {
                            delay(1000 * retryCount.toLong()) // Exponential backoff
                        }
                    }
                }

                if (lastException != null) {
                    handleException(lastException)
                }

                _networkState.value = NetworkState.ERROR
            } catch (e: Exception) {
                handleException(e)
                _networkState.value = NetworkState.ERROR
            }
        }
    }

    private fun handleSuccessResponse(response: Response<PreprocessResponse>) {
        response.body()?.let { preprocessResponse ->
            Log.d(TAG, "Success response: $preprocessResponse")
            addMessage(ChatMessage(
                text = preprocessResponse.response,
                isUser = false
            ))
        } ?: run {
            Log.e(TAG, "Response body is null")
            addMessage(ChatMessage(
                text = "Maaf, terjadi kesalahan dalam memproses respons",
                isUser = false
            ))
        }
    }

    private fun handleErrorResponse(response: Response<PreprocessResponse>) {
        val errorBody = response.errorBody()?.string()
        Log.e(TAG, "Error response: $errorBody")

        val errorMessage = when (response.code()) {
            400 -> "Format permintaan tidak valid"
            404 -> "Endpoint tidak ditemukan"
            500 -> "Terjadi kesalahan pada server"
            else -> "Terjadi kesalahan: ${response.code()}"
        }

        addMessage(ChatMessage(text = errorMessage, isUser = false))
    }

    private fun handleException(e: Exception) {
        Log.e(TAG, "Exception occurred", e)
        Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")

        val errorMessage = when (e) {
            is UnknownHostException -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
            is SocketTimeoutException -> "Waktu koneksi habis. Coba lagi nanti."
            else -> "Terjadi kesalahan koneksi: ${e.message}"
        }

        addMessage(ChatMessage(text = errorMessage, isUser = false))
    }

    private fun addMessage(message: ChatMessage) {
        val currentMessages = _messages.value.orEmpty().toMutableList()
        currentMessages.add(message)
        _messages.postValue(currentMessages)
    }

    companion object {
        private const val TAG = "ChatbotViewModel"
    }
}

enum class NetworkState {
    IDLE,
    LOADING,
    ERROR
}