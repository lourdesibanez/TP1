package com.example.tp1.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

//configurar la camara y guardar con recorder en mediastore para q aparezca en la galeria
class VideoRepositorio(private val context: Context) {

    private var recording: Recording? = null
    private val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    fun iniciarGrabacion(videoCapture: VideoCapture<Recorder>, onEvent: (VideoRecordEvent) -> Unit) {
        //nombre del archivo con fecha
        val name = "VIDEO_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())

        //configuración para que aparezca en la galería
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/TP1-Videos")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        //preparamos y empezamos la grabación
        recording = videoCapture.output
            .prepareRecording(context, mediaStoreOutputOptions)
            .apply {
                //si hay permiso de audio, lo activamos en el video
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }
            .start(mainExecutor, onEvent)
    }

    fun detenerGrabacion() {
        recording?.stop()
        recording = null
    }
}