package com.example.tp1.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp1.data.repository.UbicacionRepositorio
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tp1.data.model.Siniestro
import com.google.firebase.firestore.ListenerRegistration

class MapaViewModel(application: Application): AndroidViewModel(application) {

    // Repositorio para traducir coordenadas a direcciones de texto
    private val repositorio = UbicacionRepositorio()
    
    // Instancia de Firestore para interactuar con la base de datos
    private val db = FirebaseFirestore.getInstance()
    
    // El ID único de este celular para que siempre se pise el estado anterior
    private val idDispositivo: String = Settings.Secure.getString(
        application.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    
    // Listener para cancelar la suscripción a Firebase cuando el ViewModel se destruya
    private var communityListener: ListenerRegistration? = null

    // --- ESTADOS DE LA UI ---

    // Dirección que se muestra en la tarjeta superior
    var direccionTexto by mutableStateOf("Buscando dirección...")
        private set

    // Coordenadas actuales del usuario (para el marcador azul)
    var latitudActual by mutableDoubleStateOf(0.0)
    var longitudActual by mutableDoubleStateOf(0.0)

    // Lista observable de todos los reportes de la comunidad (marcadores en el mapa)
    val reportesComunidad = mutableStateListOf<Siniestro>()

    // Estado para saber si estamos mostrando los detalles de un marcador específico
    var reporteSeleccionado by mutableStateOf<Siniestro?>(null)

    // Variable para avisar si hubo un error técnico
    var error by mutableStateOf<String?>(null)

    init {
        // Apenas se crea el ViewModel, empezamos a escuchar a la comunidad
        escucharReportesComunidad()
    }

    /**
     * Se conecta a Firebase y escucha CUALQUIER cambio en la colección "siniestros".
     * Si un vecino agrega un punto, esta lista se actualiza sola y el mapa se redibuja.
     */
    private fun escucharReportesComunidad() {
        communityListener = db.collection("siniestros")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    error = "Error al conectar con la red comunitaria"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Limpiamos la lista local y la recargamos con los datos frescos de la nube
                    reportesComunidad.clear()
                    for (doc in snapshot.documents) {
                        val reporte = doc.toObject(Siniestro::class.java)
                        reporte?.let { reportesComunidad.add(it) }
                    }
                }
            }
    }

    /**
     * Traduce las coordenadas actuales a una dirección legible.
     */
    fun obtenerDireccion(context: Context, latitud: Double, longitud: Double) {
        latitudActual = latitud
        longitudActual = longitud
        
        viewModelScope.launch {
            try {
                val direccion = repositorio.obtenerDireccion(context, latitud, longitud)
                direccionTexto = direccion
            } catch (e: Exception) {
                direccionTexto = "Dirección no encontrada"
            }
        }
    }

    /**
     * Guarda un nuevo reporte en Firebase.
     * Usamos el ID del dispositivo para que SIEMPRE se pise el estado anterior
     * y no se llenen de puntitos en el mismo lugar.
     */
    fun guardarSiniestro(
        referencia: String, 
        tipo: String, 
        recurso: String, 
        alTerminar: (Boolean) -> Unit
    ) {
        val nuevoReporte = Siniestro(
            latitud = latitudActual,
            longitud = longitudActual,
            direccion = direccionTexto,
            referencia = referencia,
            tipo = tipo,
            recurso = recurso,
            usuarioId = idDispositivo // Guardamos quién lo mandó por las dudas
        )

        viewModelScope.launch {
            try {
                // Al usar idDispositivo como ID del documento, Firestore borra el viejo y pone el nuevo
                db.collection("siniestros")
                    .document(idDispositivo)
                    .set(nuevoReporte)
                alTerminar(true)
            } catch (e: Exception) {
                alTerminar(false)
            }
        }
    }

    /**
     * Limpia el reporte seleccionado (cierra el cuadro de detalles).
     */
    fun cerrarDetalles() {
        reporteSeleccionado = null
    }

    /**
     * Selecciona un reporte para mostrar sus detalles (se llama al tocar un marcador).
     */
    fun seleccionarReporte(reporte: Siniestro) {
        reporteSeleccionado = reporte
    }

    override fun onCleared() {
        super.onCleared()
        // Muy importante: dejamos de escuchar a Firebase para no gastar datos/batería
        communityListener?.remove()
    }
}
