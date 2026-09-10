package com.example.tp1.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.presentation.viewmodel.BateriaViewModel

/**
 * Componente principal de Batería. 
 * Contiene tanto el indicador de la barra superior como la vista de detalles.
 */

/**
 * Muestra el detalle de la batería cuando lo necesitás (ej: en una tarjeta).
 */
@Composable
fun BateriaDetalle(viewModel: BateriaViewModel = viewModel()) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Nivel de Batería: ${viewModel.nivelBateria}%", style = MaterialTheme.typography.titleMedium)
        Text(text = "Duración estimada: ${viewModel.tiempoRestante}", style = MaterialTheme.typography.bodyMedium)
        if (viewModel.horaApagado.isNotEmpty()) {
            Text(text = viewModel.horaApagado, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

/**
 * Este es el dibujito de la batería que va en la barra de arriba.
 * Si lo tocás, te muestra cuánto tiempo le queda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bateria(viewModel: BateriaViewModel = viewModel()) {
    var showTooltip by remember { mutableStateOf(false) }
    val contentColor = MaterialTheme.colorScheme.onSurface
    
    // Cambiamos el color si le queda poca carga
    val colorBateria = when {
        viewModel.nivelBateria <= 15 -> Color.Red
        viewModel.nivelBateria <= 30 -> Color(0xFFFF9800)
        else -> contentColor
    }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showTooltip = !showTooltip } // Si tocás, mostramos el cartelito
                .minimumInteractiveComponentSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(width = 28.dp, height = 16.dp)
            ) {
                // Dibujamos el cuerpo de la batería con un borde
                Box(
                    modifier = Modifier
                        .size(width = 22.dp, height = 12.dp)
                        .border(1.2.dp, colorBateria, RoundedCornerShape(2.dp))
                        .padding(1.dp)
                ) {
                    // El relleno que indica el porcentaje
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(viewModel.nivelBateria / 100f)
                            .background(colorBateria, RoundedCornerShape(1.dp))
                    )
                }
                // La puntita de la batería
                Box(
                    modifier = Modifier
                        .offset(x = 12.dp)
                        .size(width = 2.dp, height = 5.dp)
                        .background(colorBateria, RoundedCornerShape(topEnd = 1.dp, bottomEnd = 1.dp))
                )
                // Si está cargando, le mandamos el rayito en el medio
                if (viewModel.estaCargando) {
                    Text(
                        text = "⚡",
                        fontSize = 12.sp,
                        color = Color(0xFFFFD600),
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.Center).offset(y = (-0.5).dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // El porcentaje al lado en numeritos
            Text(
                text = "${viewModel.nivelBateria}%",
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Este es el cartelito que flota cuando tocás la batería
        if (showTooltip) {
            DropdownMenu(
                expanded = showTooltip,
                onDismissRequest = { showTooltip = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = "Quedan: ${viewModel.tiempoRestante}\n${viewModel.horaApagado}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    onClick = { showTooltip = false }
                )
            }
        }
    }
}
