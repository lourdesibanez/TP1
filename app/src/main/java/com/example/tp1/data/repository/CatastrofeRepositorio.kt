package com.example.tp1.data.repository

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.tp1.R

class CatastrofeRepositorio(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    
    // Acceso seguro al servicio de vibración
    private val vibrator: Vibrator? by lazy {
        try {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } catch (e: Exception) {
            null
        }
    }

    fun activarVibracion() {
        try {
            vibrator?.let { vbr ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val pattern = longArrayOf(0, 500, 200, 500, 200)
                    vbr.vibrate(VibrationEffect.createWaveform(pattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vbr.vibrate(1000)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun reproducirSonidoIrritante() {
        // Envolvemos en try-catch robusto por si el archivo R.raw.alarma no existe
        try {
            // Intentamos crear el player. Si falta el recurso, fallará aquí sin crash
            mediaPlayer = MediaPlayer.create(context, R.raw.alarma)
            
            mediaPlayer?.apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            // Fallo silencioso si no hay audio, para evitar cierre de app
            e.printStackTrace()
            mediaPlayer = null
        }
    }

    fun detenerTodo() {
        try {
            vibrator?.cancel()
            mediaPlayer?.apply {
                try { if (isPlaying) stop() } catch (e: Exception) {}
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
