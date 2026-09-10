package com.example.tp1.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp1.data.model.Catastrofe
import com.example.tp1.data.repository.CatastrofeRepositorio
import com.example.tp1.data.repository.UbicacionRepositorio
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

//le pedimos al gps la ubicacion del cel
class CatastrofeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repoEfectos = CatastrofeRepositorio(application.applicationContext)
    private val repoUbicacion = UbicacionRepositorio()
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    var hayCatastrofe by mutableStateOf(false)
        private set

    var ubicacionDetectada by mutableStateOf("Detectando ubicación...")
        private set

    var estaCargandoUbicacion by mutableStateOf(false)
        private set

    // Inicia el proceso de detección de ubicación y luego escucha Firebase
    fun iniciarSimulacion() {
        estaCargandoUbicacion = true
        val context = getApplication<Application>().applicationContext
        
        viewModelScope.launch {
            try {
                val localizacion = repoUbicacion.obtenerUbicacionActual(context)
                
                if (localizacion != null) {
                    val ciudad = repoUbicacion.obtenerCiudad(
                        context, 
                        localizacion.latitude, 
                        localizacion.longitude
                    )
                    ubicacionDetectada = ciudad
                    estaCargandoUbicacion = false
                    escucharFirebase(ciudad)
                } else {
                    ubicacionDetectada = "GPS sin señal"
                    estaCargandoUbicacion = false
                }
            } catch (e: Exception) {
                ubicacionDetectada = "Error"
                estaCargandoUbicacion = false
            }
        }
    }

    private fun escucharFirebase(zona: String) {
        // Cancelamos listener previo si existiera
        listener?.remove()

        listener = db.collection("catastrofes")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                for (doc in snapshot.documentChanges) {
                    // Escuchamos tanto si se agrega un reporte nuevo como si se modifica uno viejo
                    if (doc.type == DocumentChange.Type.ADDED || doc.type == DocumentChange.Type.MODIFIED) {
                        try {
                            val catastrofe = doc.document.toObject(Catastrofe::class.java)
                            
                            val zonaFirebase = normalizarTexto(catastrofe.ubicacion)
                            val zonaGPS = normalizarTexto(zona)

                            // Verificamos si la fecha y hora son de "ahora" (máximo 10 minutos de diferencia)
                            val esReciente = catastrofe.fechaHora?.let { timestamp ->
                                val diferenciaMilis = Math.abs(System.currentTimeMillis() - timestamp.toDate().time)
                                diferenciaMilis < 10 * 60 * 1000 // 10 minutos en milisegundos
                            } ?: false

                            if (esReciente && (zonaFirebase.contains(zonaGPS) || zonaGPS.contains(zonaFirebase))) {
                                activarSimulacion()
                            }
                        } catch (ex: Exception) {
                            android.util.Log.e("FirebaseError", "Error al leer doc: ${ex.message}")
                        }
                    }
                }
            }
    }

    private fun activarSimulacion() {
        if (!hayCatastrofe) {
            hayCatastrofe = true
            repoEfectos.activarVibracion()
            repoEfectos.reproducirSonidoIrritante()
        }
    }

    fun detenerSimulacion() {
        hayCatastrofe = false
        repoEfectos.detenerTodo()
    }

    /**
     * Función para quitar acentos, puntos y pasar a minúsculas, así la comparación es más fácil.
     */
    private fun normalizarTexto(input: String): String {
        val original = arrayOf("á", "é", "í", "ó", "ú")
        val normal = arrayOf("a", "e", "i", "o", "u")
        var result = input.lowercase().trim()
        for (i in original.indices) {
            result = result.replace(original[i], normal[i])
        }
        // Quitamos puntos y comas que suelen molestar en las direcciones
        result = result.replace(".", "").replace(",", "")
        return result
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
