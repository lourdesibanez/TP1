package com.example.tp1.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.tp1.data.repository.ComandoVozRepositorio

class ComandoVozViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ComandoVozRepositorio(application.applicationContext)

    // Si está la oreja parada o no
    var estaEscuchando by mutableStateOf(false)
        private set

    // Lo último que cazó el micrófono
    var ultimoTexto by mutableStateOf("")
        private set

    // Si algo salió mal (ej: no hay internet)
    var mensajeError by mutableStateOf<String?>(null)
        private set

    /**
     * Prende o apaga el micrófono.
     */
    fun toggleEscucha(alDetectarLinterna: () -> Unit) {
        if (estaEscuchando) {
            repositorio.detenerEscucha()
            estaEscuchando = false
            ultimoTexto = ""
        } else {
            estaEscuchando = true
            mensajeError = null
            ultimoTexto = "Escuchando..."

            repositorio.escuchar(
                onResult = { texto ->
                    // Si el texto es un estado intermedio, lo mostramos pero no cortamos
                    if (texto.startsWith("¡Te escucho") || 
                        texto.startsWith("Procesando") || 
                        texto.startsWith("Escuchando:")) {
                        ultimoTexto = texto
                        return@escuchar
                    }

                    estaEscuchando = false
                    ultimoTexto = texto 
                    
                    val comando = texto.lowercase().trim()
                    val normalizado = normalizarTexto(comando)
                    
                    if (normalizado.contains("linterna") || 
                        normalizado.contains("luz") || 
                        normalizado.contains("prende") || 
                        normalizado.contains("encender") || 
                        normalizado.contains("apaga") ||
                        normalizado.contains("flash")) {
                        
                        alDetectarLinterna()
                    } else {
                        mensajeError = "No entendí: $texto"
                    }
                },
                onError = { error ->
                    estaEscuchando = false
                    mensajeError = error
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        repositorio.destruir()
    }

    /**
     * Función casera para sacarle los acentos a las palabras.
     */
    private fun normalizarTexto(input: String): String {
        val original = arrayOf("á", "é", "í", "ó", "ú")
        val normal = arrayOf("a", "e", "i", "o", "u")
        var result = input
        for (i in original.indices) {
            result = result.replace(original[i], normal[i])
        }
        return result
    }
}
