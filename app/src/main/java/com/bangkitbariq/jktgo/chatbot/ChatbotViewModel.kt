package com.bangkitbariq.jktgo.chatbot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
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

                when {
                    message.contains("semua hotel", ignoreCase = true) -> {
                        handleGetAllHotels()
                    }
                    message.contains("semua wisata", ignoreCase = true) -> {
                        handleGetAllTourism()
                    }
                    message.contains("wisata", ignoreCase = true) -> {
                        handleTourismSearch(message)
                    }
                    message.contains("hotel", ignoreCase = true) -> {
                        handleHotelSearch(message)
                    }
                    else -> {
                        handleTourismSearch(message)
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
        val response = api.getAllHotels()
        if (response.isSuccessful) {
            response.body()?.let { hotels ->
                val responseText = formatHotelResponse(hotels)
                addMessage(ChatMessage(text = responseText, isUser = false))
            } ?: addMessage(ChatMessage(text = "Tidak ada hotel yang ditemukan", isUser = false))
        } else {
            handleErrorResponse(response)
        }
    }

    private suspend fun handleGetAllTourism() {
        val response = api.getAllTourism()
        if (response.isSuccessful) {
            response.body()?.let { tourismList ->
                val responseText = formatTourismResponse(tourismList)
                addMessage(ChatMessage(text = responseText, isUser = false))
            } ?: addMessage(ChatMessage(text = "Tidak ada tempat wisata yang ditemukan", isUser = false))
        } else {
            handleErrorResponse(response)
        }
    }

    private suspend fun handleHotelSearch(message: String) {
        val hotelResponse = api.searchHotel(
            HotelFilterRequest(
                star_rating = 0,
                max_price = Double.MAX_VALUE,
                min_user_rating = 0.0,
                region = ""
            )
        )
        if (hotelResponse.isSuccessful) {
            hotelResponse.body()?.let { hotels ->
                val responseText = formatHotelResponse(hotels)
                addMessage(ChatMessage(text = responseText, isUser = false))
            } ?: addMessage(ChatMessage(text = "Maaf, tidak menemukan hotel yang sesuai", isUser = false))
        } else {
            handleErrorResponse(hotelResponse)
        }
    }

    private suspend fun handleTourismSearch(message: String) {
        val response = api.processMessage(ChatRequestBody(text = message))
        if (response.isSuccessful) {
            val tourismList = response.body()
            if (!tourismList.isNullOrEmpty()) {
                val responseText = formatTourismResponse(tourismList)
                addMessage(ChatMessage(text = responseText, isUser = false))
            } else {
                addMessage(ChatMessage(text = "Maaf, tidak ada tempat wisata yang ditemukan", isUser = false))
            }
        } else {
            handleErrorResponse(response)
        }
    }

    private fun formatHotelResponse(hotels: List<HotelResponse>): String {
        return if (hotels.isEmpty()) {
            "Maaf, tidak ada hotel yang ditemukan sesuai kriteria Anda."
        } else {
            buildString {
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
            else -> "Terjadi kesalahan: ${e.message}"
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