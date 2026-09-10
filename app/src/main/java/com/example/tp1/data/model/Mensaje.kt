package com.example.tp1.data.model

//defino cómo va a ser el mensaje
data class Mensaje (

    val texto: String = "",
    val emisor: String = "", // "usuario" o "asistente"
    val timestamp: Long = System.currentTimeMillis()

)