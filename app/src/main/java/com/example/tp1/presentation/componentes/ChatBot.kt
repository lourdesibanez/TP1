package com.example.tp1.presentation.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.R
import com.example.tp1.data.model.Mensaje
import com.example.tp1.presentation.viewmodel.ChatBotViewModel

//mostramos la lista de mensajes y la caja de texto
@Composable
fun ChatBot(
    viewModel: ChatBotViewModel = viewModel()
) {
    var textoUsuario by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            //lazy para q la lista sea scrolleable
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(viewModel.mensajes) { msg ->
                    ChatBubble(msg)
                }
            }

            // input y botón de enviar
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = textoUsuario,
                    onValueChange = { textoUsuario = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe tu consulta...") }
                )
                IconButton(onClick = {
                    viewModel.enviarMensaje(textoUsuario)
                    textoUsuario = "" //limpiar campo
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Enviar"
                    )
                }
            }
        }
    }
}

//burbuja de chat para q si lo escribe el usuario el mensj salga a la derecha azul sino izq gris
@Composable
fun ChatBubble(mensaje: Mensaje) {
    val alineacion =
        if (mensaje.emisor == "usuario")
            Alignment.End
        else
            Alignment.Start
    val color =
        if (mensaje.emisor == "usuario")
            Color(0xFFBBDEFB)
        else
            Color(0xFFF5F5F5)

    Column(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalAlignment = alineacion) {
        Surface(shape = RoundedCornerShape(8.dp), color = color, shadowElevation = 2.dp) {
            Text(text = mensaje.texto, modifier = Modifier.padding(8.dp))
        }
    }
}
