package com.example.tp1.presentation.componentes

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.presentation.viewmodel.MapaViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

import androidx.appcompat.content.res.AppCompatResources

import androidx.compose.ui.res.painterResource
import com.example.tp1.R

/**
 * Permite ver tu ubicación, ver reportes de otros usuarios y publicar el tuyo.
 */
@Composable
fun Mapa(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: MapaViewModel = viewModel()

    // --- ESTADOS LOCALES PARA LOS DIÁLOGOS ---
    var mostrarDialogoRegistro by remember { mutableStateOf(false) }
    var textoReferencia by remember { mutableStateOf("") }
    var textoRecurso by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf("AYUDA") } // "AYUDA", "OFRECE" o "SALVO"

    // --- GESTIÓN DE PERMISOS Y GPS ---
    // Guardamos si el usuario nos dio permiso o no
    var permisosConcedidos by remember { mutableStateOf(true) }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        // Si no nos dio ninguno de los dos permisos de ubicación, avisamos
        permisosConcedidos = resultados.values.any { it }
    }
    
    LaunchedEffect(Unit) {
        launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    // Verificamos si el GPS del hardware está prendido
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    // Si no hay permisos o el GPS está apagado, mostramos el error y no dejamos seguir
    if (!permisosConcedidos || !isGpsEnabled) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⚠️ ATENCIÓN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (!permisosConcedidos) 
                        "No podemos mostrar el mapa porque no aceptaste los permisos de ubicación." 
                    else 
                        "El GPS de tu celular está apagado. Por favor, prendelo para usar el mapa.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { 
                    launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }) {
                    Text("Reintentar")
                }
            }
        }
    } else {
        // --- CONFIGURACIÓN DEL MAPA (osmdroid) ---
        val mapView = remember { MapView(context) }
        val locationOverlay = remember { MyLocationNewOverlay(GpsMyLocationProvider(context), mapView) }

        DisposableEffect(mapView) {
            mapView.onResume()
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
            onDispose {
                locationOverlay.disableMyLocation()
                mapView.onPause()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // AndroidView permite integrar el mapa clásico dentro de Compose
            AndroidView(
                factory = {
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(16.0)
                        overlays.add(locationOverlay)

                        locationOverlay.runOnFirstFix {
                            val ubicacion = locationOverlay.myLocation
                            if (ubicacion != null) {
                                post {
                                    controller.animateTo(ubicacion)
                                    viewModel.obtenerDireccion(context, ubicacion.latitude, ubicacion.longitude)
                                }
                            }
                        }
                    }
                },
                //punto de innovacion
                update = { mv ->
                    mv.overlays.removeAll { it is Marker }
                    viewModel.reportesComunidad.forEach { reporte ->
                        val marker = Marker(mv)
                        marker.position = GeoPoint(reporte.latitud, reporte.longitud)
                        marker.title = reporte.referencia
                        marker.snippet = "Tipo: ${reporte.tipo}\nRecurso: ${reporte.recurso}"
                        val iconRes = when (reporte.tipo) {
                            "AYUDA" -> android.R.drawable.ic_delete
                            "OFRECE" -> android.R.drawable.btn_star_big_on
                            else -> android.R.drawable.presence_online
                        }
                        marker.icon = AppCompatResources.getDrawable(context, iconRes)
                        marker.setOnMarkerClickListener { _, _ ->
                            viewModel.seleccionarReporte(reporte)
                            true
                        }
                        mv.overlays.add(marker)
                    }
                    mv.invalidate()
                },
                modifier = modifier.padding(top = 4.dp) // Pequeño padding para no chocar con la TopBar
            )

            // Tarjeta superior con la dirección actual del usuario
            Card(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = viewModel.direccionTexto,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Botón inferior para agregar tu propio reporte a la red
            FloatingActionButton(
                onClick = { mostrarDialogoRegistro = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
            ) {
                Text("Publicar mi estado", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }

    // --- DIÁLOGO DE REGISTRO COMUNITARIO ---
    if (mostrarDialogoRegistro) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRegistro = false },
            title = { Text("¿Cómo estás ahora?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona tu situación:", style = MaterialTheme.typography.labelMedium)
                    
                    // Selector de Tipo (Simplificado)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FilterChip(selected = tipoSeleccionado == "AYUDA", onClick = { tipoSeleccionado = "AYUDA" }, label = { Text("Ayuda") })
                        FilterChip(selected = tipoSeleccionado == "OFRECE", onClick = { tipoSeleccionado = "OFRECE" }, label = { Text("Ofrezco") })
                        FilterChip(selected = tipoSeleccionado == "SALVO", onClick = { tipoSeleccionado = "SALVO" }, label = { Text("A salvo") })
                    }

                    TextField(value = textoReferencia, onValueChange = { textoReferencia = it }, label = { Text("Título (ej: Mi casa)") })
                    TextField(value = textoRecurso, onValueChange = { textoRecurso = it }, label = { Text("¿Qué necesitas u ofreces?") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.guardarSiniestro(textoReferencia, tipoSeleccionado, textoRecurso) { exito ->
                        if (exito) {
                            mostrarDialogoRegistro = false
                            Toast.makeText(context, "Reporte compartido con la comunidad", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("Compartir") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoRegistro = false }) { Text("Cancelar") } }
        )
    }

    // --- DIÁLOGO DE DETALLES DE UN VECINO ---
    viewModel.reporteSeleccionado?.let { reporte ->
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDetalles() },
            title = { Text(reporte.referencia) },
            text = {
                Column {
                    Text("Estado: ${reporte.tipo}", fontWeight = FontWeight.Bold, color = if(reporte.tipo == "AYUDA") Color.Red else Color.Unspecified)
                    Text("Detalle: ${reporte.recurso}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ubicación: ${reporte.direccion}", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = { Button(onClick = { viewModel.cerrarDetalles() }) { Text("Cerrar") } }
        )
    }
}
