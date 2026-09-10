package com.example.tp1.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp1.data.model.ClimaRespuesta
import com.example.tp1.data.repository.ClimaRepositorio
import kotlinx.coroutines.launch

class ClimaViewModel : ViewModel() {

    // Creamos el Repositorio que se va a encargar de obtener los datos
    private val repository = ClimaRepositorio()

    // Guarda los datos del clima que recibimos de la API (mutablestateof permite a composer saber si la variable cambio para actualizar la interfaz)
    var clima by mutableStateOf<ClimaRespuesta?>(null)
        private set /*esto hace q la variable solo se pueda leer desde afuera y modificar desde adentro*/

    // Indica si estamos esperando la respuesta de la API
    var cargando by mutableStateOf(false)
        private set

    // Guarda un mensaje de error si ocurre algún problema
    var error by mutableStateOf<String?>(null)
        private set

    // pedir el clima
    fun obtenerClima(latitud: Double, longitud: Double) {

        // Iniciamos una tarea que puede ejecutarse mientras
        // la aplicación sigue funcionando normalmente
        viewModelScope.launch {

            // Avisamos que comenzó la carga
            cargando = true

            // Borramos cualquier error anterior
            error = null

            try {

                // Le pedimos al Repositorio que obtenga el clima
                clima = repository.obtenerClima(latitud, longitud)

            } catch (e: Exception) {

                // Si algo sale mal, guardamos un mensaje de error
                error = "No se pudo obtener el clima"
            }

            // Terminamos la carga
            cargando = false
        }
    }
}