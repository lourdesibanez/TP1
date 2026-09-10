package com.example.tp1

import android.os.Bundle
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.tp1.presentation.componentes.Bateria
import com.example.tp1.presentation.componentes.CentroControlTopBar
import com.example.tp1.presentation.componentes.ChatBot
import com.example.tp1.presentation.componentes.GuiaAccion
import com.example.tp1.presentation.componentes.SimuladorCatastrofe
import com.example.tp1.presentation.pantalla.PantallaHome
import com.example.tp1.presentation.pantalla.PantallaMapa
import com.example.tp1.presentation.pantalla.PantallaMultimedia
import com.example.tp1.presentation.viewmodel.CatastrofeViewModel
import com.example.tp1.presentation.viewmodel.LinternaViewModel
import com.example.tp1.ui.theme.TP1Theme
import org.osmdroid.config.Configuration
import android.preference.PreferenceManager

class MainActivity : ComponentActivity() {

    //para avisarle al viewModel cuando el usuario sale de la app
    private lateinit var linternaViewModel: LinternaViewModel

    //apenas comienza se ejecuta esto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración para que el mapa funcione (User Agent)
        // Esto le dice a los servidores de OpenStreetMap quién es nuestra app para que nos dejen bajar el fondo.
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = packageName

        //.java ya que el provider esta escrito en java
        linternaViewModel = ViewModelProvider(this).get(LinternaViewModel::class.java)

        enableEdgeToEdge() //le indico q mi app utilice toda la pantalla
        setContent { //le digo q voy a dibujar la interfaz aca
            TP1Theme { //el estilo
                TP1App() //la estructura, donde ocurre el intercambio de pantallas
            }
        }
    }

    //estado que ocurre cuando el usuario sale al menu principal o a otra app
    override fun onStop() {
        super.onStop()
        linternaViewModel.apagarFlash() //le aviso al cerebro de la linterna que apague todo
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/*unit = void*/
fun TP1App() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    
    // Estado para saber si queremos abrir una funcionalidad específica dentro de una pantalla
    var subScreenTarget by rememberSaveable { mutableStateOf<String?>(null) }

    // para resetear la pantalla cada vez q se toque en algun menu
    var resetTrigger by remember { mutableIntStateOf(0) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { 
                        // Al tocar el menú de abajo, limpiamos cualquier sub-pantalla pendiente
                        subScreenTarget = null
                        if (currentDestination == it) {
                            resetTrigger++
                        }
                        currentDestination = it  
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CentroControlTopBar(
                    currentDestination = currentDestination,
                    subScreenTarget = subScreenTarget,
                    onBackClick = if (subScreenTarget != null) {
                        { subScreenTarget = null }
                    } else null
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            key(currentDestination, resetTrigger) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    when (currentDestination) {
                        AppDestinations.HOME -> {
                            PantallaHome(
                                subScreen = subScreenTarget,
                                onNavigate = { dest, sub ->
                                    subScreenTarget = sub
                                    currentDestination = dest
                                }
                            )
                        }

                        AppDestinations.MULTIMEDIA -> {
                            PantallaMultimedia()
                        }

                        AppDestinations.MAPA -> {
                            PantallaMapa()
                        }
                    }
                }
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: Int) {
    HOME("Home", R.drawable.ic_home_baseline),
    MULTIMEDIA("Multimedia", R.drawable.ic_media_baseline),
    MAPA("Donde estoy?", R.drawable.ic_location_pin),
}

@Preview(showBackground = true)
@Composable
fun TP1AppPreview() {
    TP1Theme {
        TP1App()
    }
}