package com.example.retrofitforecaster

data class WeatherResponse(
    val list: List<WeatherData>
)

data class WeatherData(
    val main: Main,
    val weather: List<Weather>,
    val dt: Long
)

data class Main(
    val temp: Double
)

data class Weather(
    val icon: String
)