package com.example.aplicaciontfg.presentation

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.service.BackServiceSensors

class MainActivity : AppCompatActivity() {
    private var permisosDados = false
    private var sensorManager: SensorManager? = null
    private val permisos = arrayOf(Manifest.permission.BODY_SENSORS,
        Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE,
        Manifest.permission.FOREGROUND_SERVICE)
    private val viewModel : DatosViewModel by viewModels()


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                permisosDados = true
                Log.d("MyActivity", "Permisos obtenidos")
            } else {
                Log.d("MyActivity", "Permisos denegado")

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Comprobar si tengo ya los permisos
        if (noTengoPermisos()) {
            for (permiso in permisos)
                requestPermissionLauncher.launch(permiso)
        }else {
            permisosDados = true
        }

        if (permisosDados){
            var masterKey: MasterKey = MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            var preferencias: SharedPreferences = EncryptedSharedPreferences.create(
                this,
                "preferences_datos",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            //DEPURACION
            //val editor = preferencias.edit()

            //editor.putInt("pulsoMinimo", 60)
            //editor.putInt("pulsoMaximo", 100)
            //editor.commit()

            //FINAL DEPURACION

            val pulsoMinimo = preferencias.getInt("pulsoMinimo", -1)
            val pulsoMaximo = preferencias.getInt("pulsoMaximo", -1)

            Log.d("ActividadPREFERENCIAS", "pulsoMinimo: $pulsoMinimo")
            Log.d("ActividadPREFERENCIAS", "pulsoMaximo: $pulsoMaximo")

            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

            viewModel.setSenManager(sensorManager!!)
            viewModel.setPulsoMinimo(pulsoMinimo)
            viewModel.setPulsoMaximo(pulsoMaximo)

            if (pulsoMinimo != -1 && pulsoMaximo != -1)
                viewModel.setCalibrado(true)

            setContentView(R.layout.activity_main)
        }

    }

    private fun noTengoPermisos(): Boolean {
        for (permiso in permisos){
            if (checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED)
                return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        val referencia = viewModel.getReferenciaServicio()
        if (referencia != null)
            BackServiceSensors.stopService(this.applicationContext)
    }
    
}

