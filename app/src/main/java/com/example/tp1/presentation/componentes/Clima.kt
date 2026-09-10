package com.example.tp1.presentation.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.presentation.viewmodel.ClimaViewModel

@Composable
fun Clima() {
    val viewModel: ClimaViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.obtenerClima(
            latitud = -36.62,
            longitud = -64.29
        )
    }

    val clima = viewModel.clima

    if (clima != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Clima Actual",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${clima.current.temperature_2m} °C",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Emoji basado en el código del clima
                Text(
                    text = obtenerEmojiClima(clima.current.weather_code),
                    fontSize = 40.sp
                )
            }
        }
    }
}

/**
 * Función simple para mapear códigos de WMO a Emojis
 */
private fun obtenerEmojiClima(codigo: Int): String {
    return when (codigo) {
        0 -> "☀️" // Despejado
        1, 2, 3 -> "⛅" // Nublado parcial
        45, 48 -> "🌫️" // Niebla
        51, 53, 55 -> "🌧️" // Llovizna
        61, 63, 65 -> "🌧️" // Lluvia
        71, 73, 75 -> "❄️" // Nieve
        80, 81, 82 -> "🌦️" // Chubascos
        95, 96, 99 -> "⛈️" // Tormenta
        else -> "🌡️" // Genérico
    }
}
