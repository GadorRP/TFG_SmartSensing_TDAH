package com.example.aplicaciontfg.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.aplicaciontfg.R

class MainActivity2 : AppCompatActivity(), IComunicacionActividadFragmentos {
    var permisosDados = false
    val calibrado = false
    var sensorManager: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

            val lifecycleState by LocalLifecycleOwner.current.lifecycle.observeAsState()

            // Obtenemos los permisos
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    permisosDados = isGranted
                }
            )

            //Obtenemos el estado de la actividad y comprobamos si tenemos los permisos
            LaunchedEffect(lifecycleState) {
                if (lifecycleState == Lifecycle.Event.ON_RESUME) {
                    permisosDados = tengoPermisos()
                    if (permisosDados != true) {
                        permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
                    }
                }
            }

            //Cargamos la vista de la actividad
            if (permisosDados){
                sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

                setContentView(R.layout.activity_main)
            }

        }

    }

    private fun tengoPermisos(): Boolean {
        return checkSelfPermission(Manifest.permission.BODY_SENSORS) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun getSensorManager(): SensorManager? {
        return sensorManager
    }
}

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event -> state.value = event }
        this@observeAsState.addObserver(observer)
        onDispose { this@observeAsState.removeObserver(observer) }
    }
    return state
}