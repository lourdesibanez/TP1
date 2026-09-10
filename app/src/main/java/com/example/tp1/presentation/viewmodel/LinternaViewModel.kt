package com.example.tp1.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.tp1.data.repository.LinternaRepositorio

//usamos AndroidViewModel para poder tener acceso al Contexto que necesita el Repositorio
class LinternaViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = LinternaRepositorio(application.applicationContext)

    // estado observable para la UI (Compose)
    var isFlashOn by mutableStateOf(false)
        private set

    // función que llamará el botón de la pantalla
    fun toggleFlash() {
        isFlashOn = !isFlashOn
        repositorio.cambiarEstadoFlash(isFlashOn) //le aviso al hardware
    }

    //útil cuando salimos de la app
    fun apagarFlash() {
        if (isFlashOn) {
            isFlashOn = false
            repositorio.cambiarEstadoFlash(false)
        }
    }
}