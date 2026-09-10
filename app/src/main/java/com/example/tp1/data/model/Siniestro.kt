package com.example.tp1.data.model

import com.google.firebase.Timestamp

/**
 * Modelo de datos para representar un reporte en la Red de Ayuda Comunitaria.
 * 
 * @property latitud Coordenada de latitud del reporte.
 * @property longitud Coordenada de longitud del reporte.
 * @property direccion Dirección legible por humanos (traducción de Geocoder).
 * @property referencia Nombre o título breve del reporte (ej: "Muro caído").
 * @property tipo Categoría del reporte: "AYUDA" (rojo), "OFRECE" (verde), "SALVO" (azul).
 * @property recurso Descripción de lo que se necesita u ofrece (ej: "Tengo agua mineral").
 * @property fecha Timestamp de Firebase para saber cuándo se creó el reporte.
 */
data class Siniestro (
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val direccion: String = "",
    val referencia: String = "",
    val tipo: String = "AYUDA", // Valor por defecto para evitar nulos
    val recurso: String = "",
    val usuarioId: String = "", // Para saber de quién es el reporte y no duplicar
    val fecha: Timestamp = Timestamp.now()
)
