package com.example.tp1.presentation.pantalla

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.AppDestinations
import com.example.tp1.R
import com.example.tp1.presentation.componentes.ChatBot
import com.example.tp1.presentation.componentes.Clima
import com.example.tp1.presentation.componentes.GuiaAccion
import com.example.tp1.presentation.componentes.SimuladorCatastrofe
import com.example.tp1.presentation.componentes.TarjetaOpcion
import com.example.tp1.presentation.viewmodel.ComandoVozViewModel
import com.example.tp1.presentation.viewmodel.EmergenciaViewModel
import com.example.tp1.presentation.viewmodel.LinternaViewModel

/**
 * Pantalla principal que decide si mostrar el panel de control o una herramienta.
 */
@Composable
fun PantallaHome(
    subScreen: String?,
    onNavigate: (AppDestinations, String?) -> Unit
) {
    when (subScreen) {
        "CHAT" -> ChatBot()
        "GUIA" -> GuiaAccion()
        "SIMULADOR" -> SimuladorCatastrofe()
        else -> DashboardHome(onNavigate)
    }
}

/**
 * El tablero con todos los accesos directos y el clima.
 */
@Composable
fun DashboardHome(onNavigate: (AppDestinations, String?) -> Unit) {
    val viewModel: EmergenciaViewModel = viewModel()
    val linternaViewModel: LinternaViewModel = viewModel()
    val comandoVozViewModel: ComandoVozViewModel = viewModel()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Launcher para permisos
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            comandoVozViewModel.toggleEscucha { linternaViewModel.toggleFlash() }
        }
    }

    val camPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            linternaViewModel.toggleFlash()
        }
    }

    // Feedback de voz
    LaunchedEffect(comandoVozViewModel.mensajeError, comandoVozViewModel.ultimoTexto) {
        comandoVozViewModel.mensajeError?.let { snackbarHostState.showSnackbar(it) }
        if (comandoVozViewModel.ultimoTexto.isNotEmpty() && 
            !comandoVozViewModel.ultimoTexto.startsWith("Escuchando") &&
            !comandoVozViewModel.ultimoTexto.startsWith("¡Te escucho")) {
            snackbarHostState.showSnackbar("Voz: ${comandoVozViewModel.ultimoTexto}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Llamadas rápidas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        viewModel.emergencias.forEach { emergencia ->
                            BotonEmergenciaChico(
                                nombre = emergencia.nombre,
                                numero = emergencia.numero
                            ) {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${emergencia.numero}"))
                                context.startActivity(intent)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. Clima
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                    Clima()
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3. Herramientas
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    BotonAccionRapida("Chat Bot", R.drawable.ic_send, Color(0xFF4CAF50)) {
                        onNavigate(AppDestinations.HOME, "CHAT")
                    }
                    BotonAccionRapida("Guías SOS", R.drawable.ic_book, Color(0xFF2196F3)) {
                        onNavigate(AppDestinations.HOME, "GUIA")
                    }
                    BotonAccionRapida(
                        if (comandoVozViewModel.estaEscuchando) "Oigo..." else "Voz",
                        R.drawable.ic_mic,
                        if (comandoVozViewModel.estaEscuchando) Color.Red else Color(0xFFFF9800)
                    ) {
                        val permission = android.Manifest.permission.RECORD_AUDIO
                        if (androidx.core.content.ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            comandoVozViewModel.toggleEscucha { linternaViewModel.toggleFlash() }
                        } else {
                            micPermissionLauncher.launch(permission)
                        }
                    }
                    BotonAccionRapida(
                        if (linternaViewModel.isFlashOn) "Encendido" else "Linterna",
                        R.drawable.ic_flashlight,
                        if (linternaViewModel.isFlashOn) Color(0xFFFFD600) else Color.Gray
                    ) {
                        val permission = android.Manifest.permission.CAMERA
                        if (androidx.core.content.ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            linternaViewModel.toggleFlash()
                        } else {
                            camPermissionLauncher.launch(permission)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. Simulador
            item {
                TarjetaOpcion(
                    titulo = "Simulador de Catástrofes",
                    descripcion = "Activa una alerta de prueba para verificar sensores y sonidos.",
                    icono = R.drawable.ic_warning,
                    color = Color(0xFFFF9800),
                    onClick = {
                        onNavigate(AppDestinations.HOME, "SIMULADOR")
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun BotonEmergenciaChico(nombre: String, numero: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(4.dp)) {
        Text(text = nombre.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Surface(modifier = Modifier.size(54.dp).padding(vertical = 4.dp), shape = CircleShape, border = BorderStroke(1.dp, Color.Red), color = Color.Transparent) {
            Box(contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.ic_call), contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
            }
        }
        Text(text = numero, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Red)
    }
}

@Composable
fun BotonAccionRapida(label: String, icono: Int, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(modifier = Modifier.size(60.dp), shape = CircleShape, color = color.copy(alpha = 0.15f), border = BorderStroke(1.dp, color)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = icono), contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            }
        }
    }
}
