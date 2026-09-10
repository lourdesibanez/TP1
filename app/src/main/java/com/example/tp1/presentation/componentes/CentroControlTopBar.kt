package com.example.tp1.presentation.componentes

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.tp1.AppDestinations
import com.example.tp1.R

/**
 * Esta es la barra de arriba que aparece en toda la app.
 * Se encarga de mostrar el título de dónde estamos y el icono de la batería.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentroControlTopBar(
    currentDestination: AppDestinations,
    subScreenTarget: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            // Elegimos el título según si estamos en una sub-pantalla (como el chat) o en una pestaña principal
            val titulo = when {
                subScreenTarget == "CHAT" -> "CHAT BOT"
                subScreenTarget == "GUIA" -> "GUÍAS SOS"
                subScreenTarget == "SIMULADOR" -> "SIMULADOR"
                currentDestination == AppDestinations.HOME -> "CENTRO DE CONTROL"
                else -> currentDestination.label.uppercase()
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        },
        navigationIcon = {
            // Si hay una función para volver, mostramos la flechita
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_chevron),
                        contentDescription = "Volver"
                    )
                }
            }
        },
        actions = {
            // Metemos el componente de la batería a la derecha
            Bateria()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
