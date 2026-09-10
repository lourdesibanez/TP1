package com.example.tp1.presentation.componentes

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.presentation.viewmodel.AudioViewModel
import android.Manifest
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.tp1.R

@Composable
fun GrabarAudio(viewModel: AudioViewModel = viewModel()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Si el usuario acepta, iniciamos la grabación
                viewModel.toggleRecording()
            }
        }
        
        //con el estado del modelo cambia el texto del boton
        Text(text = if (viewModel.isRecording)
            "Grabando..."
        else
            "Micrófono listo")
        
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                // Ya tenemos permiso, grabamos normal
                viewModel.toggleRecording()
            } else {
                // No tenemos permiso, se lo pedimos al usuario
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) {
            //row permite poner una cosa al lado de la otra horizontal
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = null, // se utiliza para accesibilidad
                    modifier = Modifier.size(18.dp),

                    //cambia de color el icono
                    tint = if (viewModel.isRecording)
                        Color.Red
                    else
                        Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (viewModel.isRecording) "Detener Grabación" else "Iniciar Grabación")
            }
        }

        if (viewModel.lastFilePath.isNotEmpty() && !viewModel.isRecording) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Guardado en: ${viewModel.lastFilePath.split("/").last()}")
        }
    }
}
