package com.bangkitbariq.jktgo.chatbot

data class TourismResponse(
    val Place_Name: String = "",
    val Category: String = "",
    val Description: String = "",
    val City: String = "",
    val Price: Double = 0.0,
    val Rating: Int = 0,
    val image: String = "",
    val Coordinate: String = ""
)

data class HotelResponse(
    val name: String = "",
    val Description: String = "",
    val hotelFeatures: String = "",
    val image: String = "",
    val originalRate_perNight_totalFare: Double = 0.0,
    val region: String = "",
    val starRating: Double = 0.0,
    val userRating: Double = 0.0
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

data class ChatRequestBody(
    val text: String
)


