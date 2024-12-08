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
    suspend fun processMessage(@Body request: ChatRequestBody): Response<List<TourismResponse>>
}