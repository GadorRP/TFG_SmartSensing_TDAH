package com.example.aplicaciontfg.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicaciontfgprototipo.R

class MainActivity : AppCompatActivity() {
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
            //obtener los datos persistentes
            val preferencias = getPreferences(Context.MODE_PRIVATE)
            val pulsoMinimo = preferencias.getInt("pulsoMinimo", -1)
            val pulsoMaximo = preferencias.getInt("pulsoMaximo", -1)

            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

            //incluir datos en el viewmodel
            viewModel.setSenManager(sensorManager!!)
            viewModel.setPulsoMinimo(pulsoMinimo)
            viewModel.setPulsoMaximo(pulsoMaximo)

            if (pulsoMinimo != -1 && pulsoMaximo != -1)
                viewModel.setCalibrado(true)

            setContentView(R.layout.activity_main)
        }

    }

    private fun noTengoPermisos(): Boolean {
        return checkSelfPermission(Manifest.permission.BODY_SENSORS) !=
                PackageManager.PERMISSION_GRANTED
    }

}

