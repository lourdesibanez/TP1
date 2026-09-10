package com.example.tp1.data.repository

import com.example.tp1.data.model.ClimaRespuesta
import com.example.tp1.data.remote.RetrofitInstancia

/*cuando cambie la api, solo modifico la instancia*/
class ClimaRepositorio {
    suspend fun obtenerClima(
        latitud: Double,
        longitud: Double
    ): ClimaRespuesta {

        return RetrofitInstancia.api.obtenerClima(
            latitud = latitud,
            longitud = longitud,
            datosActuales = "temperature_2m,wind_speed_10m,weather_code"
        )
    }
}