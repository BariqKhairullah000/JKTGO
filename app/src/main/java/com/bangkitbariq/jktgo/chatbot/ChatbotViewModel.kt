package com.bangkitbariq.jktgo.chatbot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Locale
import java.util.concurrent.TimeUnit

class ChatbotViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .protocols(listOf(Protocol.HTTP_1_1))
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Connection", "close")
            chain.proceed(requestBuilder.build())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://34.128.86.142:5000/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder()
                .setLenient()
                .create()
        ))
        .build()

    private val api = retrofit.create(ChatbotAPI::class.java)

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                _networkState.value = NetworkState.LOADING
                Log.d(TAG, "Sending message: $message")
                addMessage(ChatMessage(text = message, isUser = true))

                when {
                    message.contains("semua hotel", ignoreCase = true) -> {
                        handleGetAllHotels()
                    }
                    message.contains("semua wisata", ignoreCase = true) -> {
                        handleGetAllTourism()
                    }
                    message.contains("hotel", ignoreCase = true) -> {
                        handleHotelSearch(message)
                    }
                    message.contains("wisata", ignoreCase = true) -> {
                        handleTourismSearch(message)
                    }
                    else -> {
                        // Try tourism first, if no results, try hotel
                        val tourismResponse = api.processMessage(ChatRequestBody(text = message))
                        if (tourismResponse.isSuccessful && !tourismResponse.body().isNullOrEmpty()) {
                            handleTourismResponse(tourismResponse)
                        } else {
                            // Try hotel search as fallback
                            val hotelRequest = HotelFilterRequest(0, Double.MAX_VALUE, 0.0, "")
                            val hotelResponse = api.searchHotel(hotelRequest)
                            handleHotelResponse(hotelResponse)
                        }
                    }
                }

                _networkState.value = NetworkState.IDLE
            } catch (e: Exception) {
                handleException(e)
                _networkState.value = NetworkState.ERROR
            }
        }
    }

    private suspend fun handleGetAllHotels() {
        try {
            val response = withContext(Dispatchers.IO) {
                api.getAllHotels()
            }
            handleHotelResponse(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun handleGetAllTourism() {
        try {
            val response = withContext(Dispatchers.IO) {
                api.getAllTourism()
            }
            handleTourismResponse(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun handleHotelSearch(message: String) {
        try {
            val request = HotelFilterRequest(0, Double.MAX_VALUE, 0.0, "")
            val response = withContext(Dispatchers.IO) {
                api.searchHotel(request)
            }
            handleHotelResponse(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun handleTourismSearch(message: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                api.processMessage(ChatRequestBody(text = message))
            }
            handleTourismResponse(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleHotelResponse(response: Response<List<HotelResponse>>) {
        if (response.isSuccessful) {
            response.body()?.let { hotels ->
                if (hotels.isNotEmpty()) {
                    val responseText = formatHotelResponse(hotels)
                    addMessage(ChatMessage(text = responseText, isUser = false))
                } else {
                    addMessage(ChatMessage(text = "Maaf, tidak ada hotel yang ditemukan", isUser = false))
                }
            } ?: addMessage(ChatMessage(text = "Maaf, tidak dapat memuat data hotel", isUser = false))
        } else {
            handleErrorResponse(response)
        }
    }

    private fun handleTourismResponse(response: Response<List<TourismResponse>>) {
        if (response.isSuccessful) {
            response.body()?.let { tourismList ->
                if (tourismList.isNotEmpty()) {
                    val responseText = formatTourismResponse(tourismList)
                    addMessage(ChatMessage(text = responseText, isUser = false))
                } else {
                    addMessage(ChatMessage(text = "Maaf, tidak ada tempat wisata yang ditemukan", isUser = false))
                }
            } ?: addMessage(ChatMessage(text = "Maaf, tidak dapat memuat data wisata", isUser = false))
        } else {
            handleErrorResponse(response)
        }
    }

    private fun formatHotelResponse(hotels: List<HotelResponse>): String {
        return buildString {
            appendLine("Berikut hotel yang saya temukan:")
            hotels.forEachIndexed { index, hotel ->
                appendLine("\n${index + 1}. ${hotel.name}")
                appendLine("   Rating: ⭐ ${hotel.starRating} (${hotel.userRating}/10)")
                appendLine("   Lokasi: ${hotel.region}")
                appendLine("   Harga: Rp${String.format(Locale.US, "%,.0f", hotel.originalRate_perNight_totalFare)}/malam")
                if (hotel.hotelFeatures.isNotBlank()) {
                    appendLine("   Fasilitas: ${hotel.hotelFeatures}")
                }
            }
        }
    }

    private fun formatTourismResponse(tourismList: List<TourismResponse>): String {
        return buildString {
            appendLine("Berikut tempat wisata yang saya temukan:")
            tourismList.forEachIndexed { index, tourism ->
                appendLine("\n${index + 1}. ${tourism.Place_Name}")
                appendLine("   Kategori: ${tourism.Category}")
                appendLine("   Rating: ⭐ ${tourism.Rating}/100")
                appendLine("   Lokasi: ${tourism.City}")
                appendLine("   Harga: Rp${String.format(Locale.US, "%,d", tourism.Price.toInt())}")
                appendLine("   Deskripsi: ${tourism.Description.take(150)}...")
            }
        }
    }

    private fun handleErrorResponse(response: Response<*>) {
        val errorMessage = when (response.code()) {
            400 -> "Format permintaan tidak valid"
            404 -> "Data tidak ditemukan"
            500 -> "Terjadi kesalahan pada server"
            else -> "Terjadi kesalahan: ${response.code()}"
        }
        addMessage(ChatMessage(text = errorMessage, isUser = false))
    }

    private fun handleException(e: Exception) {
        Log.e(TAG, "Exception occurred", e)
        val errorMessage = when (e) {
            is UnknownHostException -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
            is SocketTimeoutException -> "Waktu koneksi habis. Coba lagi nanti."
            is JsonParseException -> "Terjadi kesalahan saat memproses data. Mohon coba lagi."
            else -> "Terjadi kesalahan koneksi. Mohon coba lagi beberapa saat."
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