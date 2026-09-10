package com.example.tp1.data.repository

import android.content.Context
import android.hardware.camera2.CameraManager

//aca solo hablamos con la camara
class LinternaRepositorio (context: Context){

    //accedo al servicio de sistema de camara y le asigno un nombre
    private val camaraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null

    init {
        //busco la cámara con flash
        try {
            val ids = camaraManager.cameraIdList
            android.util.Log.d("Linterna", "Cámaras detectadas: ${ids.size}")
            cameraId = ids.firstOrNull { id ->
                val hasFlash = camaraManager.getCameraCharacteristics(id)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                android.util.Log.d("Linterna", "Cámara $id tiene flash: $hasFlash")
                hasFlash
            }
        } catch (e: Exception) {
            android.util.Log.e("Linterna", "Error al buscar flash", e)
        }
    }

    //controlamos el flash de la camara
    fun cambiarEstadoFlash(encender: Boolean) {
        try {
            cameraId?.let { id ->
                android.util.Log.d("Linterna", "Cambiando flash a: $encender")
                camaraManager.setTorchMode(id, encender)
            } ?: run {
                android.util.Log.e("Linterna", "No se encontró cámara con flash")
            }
        } catch (e: Exception) {
            android.util.Log.e("Linterna", "Error al cambiar flash", e)
        }
    }
}