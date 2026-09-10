package com.example.tp1.data.repository

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//es quien habla con el microfono
class AudioRepositorio(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String = ""

    //crea un archivo en la carpeta de musica de la app con nombre basado en la fecha y devuelve la ruta del archivo
    fun iniciarGrabacion(): String? {
        //al usar MediaStore el SO toma el archivo y lo pone en la bd publica
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "AUDIO_$timeStamp.3gp"

            //información para el sistema
            val values = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "audio/3gpp")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            }

            //le pido al sistema que me dé una ruta para escribir
            val uri = context.contentResolver.insert(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

            if (uri != null) {
                val descriptor = context.contentResolver.openFileDescriptor(uri, "w")
                outputFile = uri.toString() //guardo la URI para detener

                mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    MediaRecorder()
                }

                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(descriptor?.fileDescriptor) // Escribimos directo en la URI
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    prepare()
                    start()
                }
                fileName //devuelvo el nombre para mostrar en pantalla
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun detenerGrabacion(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null

            //para q el cel se entere q hay nuevo archivo y lo muestre en la galeria de musica
            android.media.MediaScannerConnection.scanFile(context, arrayOf(outputFile), null, null)

            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
