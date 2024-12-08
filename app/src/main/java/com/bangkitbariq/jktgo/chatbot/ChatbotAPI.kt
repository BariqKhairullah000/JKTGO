package com.bangkitbariq.jktgo.chatbot

import retrofit2.Response
import retrofit2.http.*

interface ChatbotAPI {
    @GET("hotel")
    suspend fun getAllHotels(): Response<List<HotelResponse>>

    @GET("tourism")
    suspend fun getAllTourism(): Response<List<TourismResponse>>

    @POST("recommend_hotel")
    suspend fun searchHotel(@Body request: HotelFilterRequest): Response<List<HotelResponse>>

    @POST("recommend_tourism")
    suspend fun searchTourism(@Body request: TourismFilterRequest): Response<List<TourismResponse>>

    @POST("preprocess_text")
    suspend fun sendMessage(@Body request: ChatRequestBody): Response<PreprocessResponse>
}

data class PreprocessResponse(
    val response: String,
    val status: String? = null
)

data class HotelResponse(
    val name: String,
    val address: String,
    val rating: Double,
    val price: Double,
    val region: String,
    val star_rating: Int
)

data class TourismResponse(
    val name: String,
    val address: String,
    val rating: Double,
    val price: Double,
    val category: String
)

data class ChatRequestBody(
    val text: String
)

data class HotelFilterRequest(
    val star_rating: Int,
    val max_price: Double,
    val min_user_rating: Double,
    val region: String
)

data class TourismFilterRequest(
    val category: String,
    val max_price: Double,
    val min_rating: Int
)