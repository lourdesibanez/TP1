package com.example.tp1.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp1.data.model.Mensaje
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatBotViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val mensajes = mutableStateListOf<Mensaje>()

    init {
        escucharMensajes()
    }

    private fun escucharMensajes() {
        db.collection("chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    mensajes.clear()
                    for (doc in snapshot.documents) {
                        val mensaje = doc.toObject(Mensaje::class.java)
                        if (mensaje != null) mensajes.add(mensaje)
                    }
                }
            }
    }

    //guarrda un documento en la coleccion llamada chat de firebase
    fun enviarMensaje(texto: String) {
        if (texto.isBlank()) return
        val nuevoMensaje = Mensaje(texto = texto, emisor = "usuario")
        db.collection("chat").add(nuevoMensaje)

        responderComoAsistente(texto)
    }

    //
    private fun responderComoAsistente(consulta: String) {
        //lanzo la respuesta del asistente como una tarea en segundo plano
        viewModelScope.launch {
            delay(1500) // Simula 1.5 segundos de espera
            val respuestaText =
                when {
                    consulta.contains("hola", ignoreCase = true) || consulta.contains("ayuda", ignoreCase = true) ->
                        """¡Hola! Soy tu asistente de emergencias. 
                        Escribe la palabra que represente la catástrofe:
                        1. Inundación
                        2. Incendio
                        3. Herido
                        4. Ayuda
                        
                        ¿Qué sucede?
                        """.trimIndent()

                    consulta.contains("inundacion", ignoreCase = true) || consulta.contains("1", ignoreCase = true) ->
                        "⚠️ Corta la luz, sube a zonas altas y no cruces corrientes de agua. Mira el 'Mapa' para rutas seguras."

                    consulta.contains("incendio", ignoreCase = true) || consulta.contains("2", ignoreCase = true) ->
                        "🔥 Agáchate para evitar el humo, busca la salida y no uses ascensores. Usa la 'Linterna' si hay poca visibilidad."

                    consulta.contains("herido", ignoreCase = true) || consulta.contains("3", ignoreCase = true) ->
                        "🆘 Presiona heridas sangrantes, no muevas a personas con posibles fracturas y llama al 911 ahora mismo."

                    consulta.contains("ayuda", ignoreCase = true) || consulta.contains("4", ignoreCase = true) ->
                        "📞 Revisa la pestaña de 'Home' allí encontrarás los numeros telefonicos para comunicarte con emergencias."


                else -> "Entiendo. Mantén la calma y usa las herramientas de la app para protegerte. ¿Necesitas saber algo más?"
            }
            val mensajeAsistente = Mensaje(texto = respuestaText, emisor = "asistente")
            db.collection("chat").add(mensajeAsistente)
        }
    }

}