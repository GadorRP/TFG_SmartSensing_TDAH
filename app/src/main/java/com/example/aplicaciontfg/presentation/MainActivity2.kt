package com.example.aplicaciontfg.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.aplicaciontfg.R

class MainActivity2 : AppCompatActivity(), IComunicacionActividadFragmentos {
    var permisosDados = false
    val calibrado = false
    var sensorManager: SensorManager? = null
    val permiso = Manifest.permission.BODY_SENSORS

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                permisosDados = true
                Log.d("MyActivity", "Permiso BODY_SENSORS concedido")
            } else {
                Log.d("MyActivity", "Permiso BODY_SENSORS denegado")

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Comprobar si tengo ya los permisos
        if (noTengoPermisos()) {
            requestPermissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }else {
            permisosDados = true
        }

        if (permisosDados){
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            setContentView(R.layout.activity_main)
        }

    }

    private fun noTengoPermisos(): Boolean {
        return checkSelfPermission(Manifest.permission.BODY_SENSORS) !=
                PackageManager.PERMISSION_GRANTED
    }


    override fun getSenManager(): SensorManager? {
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