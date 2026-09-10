package com.example.tp1.data.model

data class GuiaAccion (

    val nombre: String,
    val imagen: Int,
    val video: Int,
    val antes: List<String>,
    val durante: List<String>,
    val despues: List<String>

)