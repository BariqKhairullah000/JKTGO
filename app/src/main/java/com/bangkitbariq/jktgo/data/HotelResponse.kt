package com.bangkitbariq.jktgo.data

data class HotelResponse(
    val id: String,
    val name: String,
    val region: String,  // Ini adalah field yang akan kita gunakan untuk filter
    val starRating: Int,
    val userRating: Double,
    val originalRate_perNight_totalFare: Double,
    val hotelFeatures: String
)
