package com.example.tp1.presentation.viewmodel

import android.app.Application
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.tp1.data.repository.VideoRepositorio

//maneja el estado de la grabacion y los mensajes de error
class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = VideoRepositorio(application.applicationContext)

    var isRecording by mutableStateOf(false)
        private set

    fun toggleRecording(videoCapture: VideoCapture<Recorder>?) {
        if (videoCapture == null) return

        if (isRecording) {
            repositorio.detenerGrabacion()
        } else {
            repositorio.iniciarGrabacion(videoCapture) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        isRecording = true
                    }
                    is VideoRecordEvent.Finalize -> {
                        isRecording = false
                    }
                }
            }
        }
    }
}