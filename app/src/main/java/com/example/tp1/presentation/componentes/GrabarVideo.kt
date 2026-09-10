package com.example.tp1.presentation.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp1.presentation.viewmodel.VideoViewModel
import com.example.tp1.R

@Composable
fun GrabarVideo(viewModel: VideoViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    //para saber si el usuario pidio abrir la camara
    var isCameraStarted by remember { mutableStateOf(false) }

    //define qué cámara usar (frontal/trasera)
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }

    // Vista previa de la cámara y objeto de captura
    val previewView = remember { PreviewView(context) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }

    // Permisos necesarios
    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    // NOS ASEGURAMOS DE SOLTAR LA CÁMARA CUANDO CERRAMOS LA PANTALLA
    // Si no hacemos esto, la linterna se queda "bloqueada" porque el grabador no soltó el fierro.
    DisposableEffect(Unit) {
        onDispose {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll() // Liberamos todo
                isCameraStarted = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //config de CameraX: arranca la camara
    LaunchedEffect(isCameraStarted, cameraSelector) {
        if(isCameraStarted){
            if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    //configurar Preview
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    // configurar Captura de Video
                    val recorder = Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build()
                    videoCapture = VideoCapture.withOutput(recorder)

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, videoCapture)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
            } else {
                launcher.launch(permissions)
            }
        }
    }

    Box(modifier = if (isCameraStarted) Modifier.fillMaxSize() else Modifier.wrapContentSize()) {

        // capa 1: recuadro donde se ve la cámara
        if (isCameraStarted) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
            }
        }

        //capa 2: controles flotando
        Column(
            modifier = if (isCameraStarted) Modifier.fillMaxSize().padding(bottom = 40.dp) else Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (isCameraStarted) Arrangement.Bottom else Arrangement.Center
        ) {
            // Solo mostramos este botón si la cámara ya está abierta
            if (isCameraStarted && !viewModel.isRecording) {
                IconButton(onClick = {
                    // Lógica para alternar entre Frontal y Trasera
                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    } else {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_switch_camera),
                        contentDescription = "Cambiar Cámara",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Button(onClick = {
                if (!isCameraStarted) {
                    // PASO 1: Pedir permisos y abrir cámara
                    if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                        isCameraStarted = true
                    } else {
                        launcher.launch(permissions)
                    }
                } else {
                    // PASO 2 y 3: Iniciar o Detener grabación
                    viewModel.toggleRecording(videoCapture)
                }
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mantenemos el ícono
                    Icon(
                        painter = painterResource(id = R.drawable.ic_video),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (viewModel.isRecording) Color.Red else Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Texto dinámico según el estado
                    Text(
                        text = when {
                            viewModel.isRecording -> "Detener Grabación"
                            isCameraStarted -> "Empezar a Grabar"
                            else -> "Abrir Cámara"
                        }
                    )
                }
            }
        }
    }
}