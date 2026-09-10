package com.example.tp1.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.tp1.data.repository.AudioRepositorio

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = AudioRepositorio(application.applicationContext)

    //variable para que la pantalla muestre lo que sucede
    var isRecording by mutableStateOf(false)
        private set

    var lastFilePath by mutableStateOf("")
        private set

    //
    fun toggleRecording() {
        if (isRecording) {
            val path = repositorio.detenerGrabacion()
            if (path != null) {
                lastFilePath = path
                isRecording = false
            }
        } else {
            val path = repositorio.iniciarGrabacion()
            if (path != null) {
                isRecording = true
            }
        }
    }
}
