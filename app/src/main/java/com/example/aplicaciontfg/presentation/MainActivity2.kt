package com.example.aplicaciontfg.presentation

import android.Manifest
import androidx.fragment.app.activityViewModels
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import com.example.aplicaciontfg.R

class MainActivity2 : AppCompatActivity() {
    private var permisosDados = false
    private var sensorManager: SensorManager? = null
    private val permiso = Manifest.permission.BODY_SENSORS
    private val viewModel : DatosViewModel by viewModels()


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
            requestPermissionLauncher.launch(permiso)
        }else {
            permisosDados = true
        }

        if (permisosDados){
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

            viewModel.setSenManager(sensorManager!!)
            viewModel.setPulsoMaximo(100)
            Log.d("Pulso actividad", viewModel.getPulsoMaximo().toString())

            setContentView(R.layout.activity_main)
        }

    }

    private fun noTengoPermisos(): Boolean {
        return checkSelfPermission(Manifest.permission.BODY_SENSORS) !=
                PackageManager.PERMISSION_GRANTED
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