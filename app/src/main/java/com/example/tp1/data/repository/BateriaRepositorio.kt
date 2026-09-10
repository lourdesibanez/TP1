package com.example.tp1.data.repository

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

//habla con el sist para obtener los datos crudos de la bateria
class BateriaRepositorio (private val context: Context){

    private val manejadorBateria = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    fun obtenerDatosBateria(): InfoBateria {
        val nivel = manejadorBateria.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        // Obtener estado de carga más preciso mediante Intent
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val estaCargando = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        //corriente actual, microamperios si es negativo esta descargado
        val corriente = manejadorBateria.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val cargaRestante = manejadorBateria.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

        var milisegundosRestantes: Long = -1

        //carga sobre corriente (con valor absoluto ya que es negativa) y pasamos a milisegundos
        if(corriente < 0 ){
            milisegundosRestantes = (cargaRestante.toDouble() / Math.abs(corriente).toDouble() * 3600 * 1000).toLong()
        }

        return InfoBateria(
            nivel = nivel,
            milisegundosRestantes = milisegundosRestantes,
            estaCargando = estaCargando
        )
    }
}

data class InfoBateria(
    val nivel: Int,
    val milisegundosRestantes: Long,
    val estaCargando: Boolean
)