package com.example.tp1.data.model

data class ClimaRespuesta (
    val current: ClimaActual
)

data class ClimaActual(
    val temperature_2m: Double,
    val wind_speed_10m: Double,
    val weather_code: Int
)