package com.example.tp1.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp1.data.repository.BateriaRepositorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//heredamos androidviewmodel para tener el context (la conexion con el sistema Android)
class BateriaViewModel(application: Application): AndroidViewModel(application) {
    // Estas son las variables que la pantalla se queda mirando para actualizarse
    var nivelBateria by mutableStateOf(0)
        private set
    var tiempoRestante by mutableStateOf("Calculando...")
        private set
    var horaApagado by mutableStateOf("")
        private set
    var estaCargando by mutableStateOf(false)
        private set
        
    private val repositorio = BateriaRepositorio(application.applicationContext)

    init {
        // Apenas nace el ViewModel, arrancamos a pedir datos solos
        iniciarRefrescoAutomatico()
    }

    /**
     * Hace que la batería se actualice solita cada tanto.
     */
    private fun iniciarRefrescoAutomatico() {
        viewModelScope.launch {
            while (true) {
                cargarDatosBateria()
                delay(10000) // refrescamos cada 10 segundos para no gastar de más
            }
        }
    }

    /**
     * Le pide al repositorio los datos reales del fierro y los acomoda para la pantalla.
     */
    fun cargarDatosBateria(){
        val info = repositorio.obtenerDatosBateria()
        nivelBateria = info.nivel
        estaCargando = info.estaCargando

        if(info.estaCargando){
            tiempoRestante = "Cargando"
            horaApagado = "Conectado a la corriente"
        } else if(info.milisegundosRestantes > 0 ){
            val totalMinutos = info.milisegundosRestantes / (1000*60)
            val horas = totalMinutos / 60
            val minutos = totalMinutos % 60

            tiempoRestante = "${horas}h ${minutos}m"
            
            // Calculamos a qué hora se nos va a apagar el boliche
            val cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, totalMinutos.toInt())
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            horaApagado = "Se apagará a las ${sdf.format(cal.time)}"
        } else {
            tiempoRestante = "Calculando..."
            horaApagado = "No hay datos suficientes"
        }
    }
}
