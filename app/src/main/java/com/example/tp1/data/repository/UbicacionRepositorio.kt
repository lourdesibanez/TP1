package com.example.tp1.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.isActive

//clase que se encarga solo de obtener una direccion o ubicacion
class UbicacionRepositorio {

    //recibo coordenadas y devuelvo direccion en texto completo
    suspend fun obtenerDireccion(context: Context, latitud: Double, longitud: Double):
            String = withContext(Dispatchers.IO){
        val geocoder = Geocoder(context, Locale.getDefault())
        val direcciones = geocoder.getFromLocation(latitud, longitud, 1)

        if (!direcciones.isNullOrEmpty()) {
            direcciones[0].getAddressLine(0) ?: "Dirección no encontrada"
        } else {
            "Dirección no encontrada"
        }
    }

    // Devuelve solo el nombre de la ciudad o zona para filtrar catástrofes
    suspend fun obtenerCiudad(context: Context, latitud: Double, longitud: Double):
            String = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val direcciones = geocoder.getFromLocation(latitud, longitud, 1)

        if (!direcciones.isNullOrEmpty()) {
            // Preferimos la ciudad (locality), sino subAdminArea (provincia/departamento)
            direcciones[0].locality ?: direcciones[0].subAdminArea ?: "Ubicación desconocida"
        } else {
            "Ubicación desconocida"
        }
    }

    // Obtiene una ubicación fresca pidiéndola activamente al GPS
    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacionActual(context: Context): Location? = suspendCancellableCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Al recibir la ubicación, apagamos el sensor para no gastar batería
                locationManager.removeUpdates(this)
                // Devolvemos el resultado a la corrutina
                if (continuation.isActive) continuation.resume(location)
            }
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                if (continuation.isActive) continuation.resume(null)
            }
        }

        try {
            // Pedimos una actualización única y fresca al GPS
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)

            // También pedimos por Red (Wifi/Antenas) por si el GPS está bajo techo y tarda mucho
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null)

            // Si se cancela la búsqueda, apagamos el sensor
            continuation.invokeOnCancellation {
                locationManager.removeUpdates(listener)
            }
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(null)
        }
    }
}
