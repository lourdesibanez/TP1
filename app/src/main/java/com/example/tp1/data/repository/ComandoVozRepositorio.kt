package com.example.tp1.data.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Repositorio encargado de gestionar el reconocimiento de voz del sistema.
 */
class ComandoVozRepositorio(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    /**
     * Inicia la escucha de voz y devuelve el texto reconocido a través de [onResult].
     */
    fun escuchar(onResult: (String) -> Unit, onError: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("El reconocimiento de voz no está disponible en este equipo")
            return
        }

        // Lo creamos justo antes de usarlo para asegurar que esté fresco
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES") 
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000L)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                onResult("¡Te escucho! Hablá ahora...")
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                onResult("Procesando lo que dijiste...")
            }

            override fun onError(error: Int) {
                val mensaje = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                    SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Faltan permisos de micrófono"
                    SpeechRecognizer.ERROR_NETWORK -> "Error de red/internet"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No te entendí, probá de nuevo"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "El sistema está ocupado, esperá un toque"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Pasó mucho tiempo sin hablar"
                    else -> "Error en el reconocimiento: $error"
                }
                onError(mensaje)
                destruir()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult(matches.joinToString(" ")) 
                }
                destruir()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult("Escuchando: ${matches[0]}...")
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    fun detenerEscucha() {
        speechRecognizer?.stopListening()
        destruir()
    }

    fun destruir() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
