package com.example.tp1.presentation.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.presentation.viewmodel.LinternaViewModel

/* Componente de Linterna con diseño circular e iconos */
@Composable
fun Linterna() {
    val viewModel: LinternaViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (viewModel.isFlashOn) "ENCENDIDA" else "APAGADA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (viewModel.isFlashOn) Color(0xFFFFD600) else Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Surface(
                onClick = { viewModel.toggleFlash() },
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = if (viewModel.isFlashOn) Color(0xFFFFD600).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f),
                border = BorderStroke(2.dp, if (viewModel.isFlashOn) Color(0xFFFFD600) else Color.Gray),
                tonalElevation = if (viewModel.isFlashOn) 8.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🔦",
                        fontSize = 40.sp
                    )
                }
            }
        }
    }
}
