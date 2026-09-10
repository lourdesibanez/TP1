package com.example.tp1.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*esto configura retrofit la api y le indica a qué servidor conectarse*/

object RetrofitInstancia {
    val api: ClimaApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClimaApi::class.java)
}