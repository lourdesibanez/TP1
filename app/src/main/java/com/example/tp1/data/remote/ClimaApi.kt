package com.example.tp1.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.tp1.data.model.ClimaRespuesta

/*esta interface describe qué petición quiero hacer*/
interface ClimaApi {
    @GET("v1/forecast")

    /*aca le indico los parametros que quiero obtener de la api*/
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") datosActuales: String
    ): ClimaRespuesta

}