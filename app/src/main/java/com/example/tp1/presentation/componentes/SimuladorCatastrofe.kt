package com.example.tp1.presentation.componentes

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.tp1.R
import com.example.tp1.presentation.viewmodel.CatastrofeViewModel

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SimuladorCatastrofe(viewModel: CatastrofeViewModel = viewModel()) {
    val colorAlerta = remember { Animatable(Color.Transparent) }

    // APENAS ENTRA: Le damos la orden al cerebro de que empiece a buscar el GPS y Firebase
    LaunchedEffect(Unit) {
        viewModel.iniciarSimulacion()
    }

    // Cuando salimos de esta pantalla, apagamos la simulación para no gastar batería
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            viewModel.detenerSimulacion()
        }
    }

    // Efecto de parpadeo visual cuando hay catástrofe
    LaunchedEffect(viewModel.hayCatastrofe) {
        if (viewModel.hayCatastrofe) {
            while (viewModel.hayCatastrofe) {
                colorAlerta.animateTo(
                    targetValue = Color.Red.copy(alpha = 0.4f),
                    animationSpec = tween(durationMillis = 400)
                )
                colorAlerta.animateTo(
                    targetValue = Color.Transparent,
                    animationSpec = tween(durationMillis = 400)
                )
            }
        } else {
            colorAlerta.snapTo(Color.Transparent)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorAlerta.value),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.hayCatastrofe) {
            // UI de ALERTA ACTIVA
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "¡ALERTA DE CATÁSTROFE!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.detenerSimulacion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("DETENER ALERTA", color = Color.White)
                }
            }
        } else {
            // UI de ESPERA O DETECCIÓN
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                if (viewModel.estaCargandoUbicacion) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Detectando tu ubicación...")
                } else {
                    Text(
                        "Monitoreando zona: ${viewModel.ubicacionDetectada}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "La alarma se activará si Firebase reporta una catástrofe en esta ubicación.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
