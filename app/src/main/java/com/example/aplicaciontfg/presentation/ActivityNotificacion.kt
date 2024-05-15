package com.example.aplicaciontfg.presentation

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R


class ActivityNotificacion : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var keyguardManager: KeyguardManager? = null
    private var vibrator: Vibrator? = null
    private var hayDescanso = false
    private var minDescanso = -1
    private var servicioTerminado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = R.layout.activity_notificacion
        setContentView(root)

        Log.d("ACTIVIDAD NUEVA" , "CREADO")

        hayDescanso = intent.getBooleanExtra("hayDescanso", false)
        minDescanso = intent.getIntExtra("minDescanso", -1)
        servicioTerminado = intent.getBooleanExtra("servicioTerminado", false)

        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager!!.requestDismissKeyguard(this, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager: VibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        }

        // Definimos duracion de la vibracion
        val duracion = 200L


        //obtenemos el titulo y el subtitulo de la pantalla
        val titulo = findViewById<TextView>(R.id.tituloNot)
        val subtitulo = findViewById<TextView>(R.id.subTituloNot)

        var duracionActividad = 10000

        if (hayDescanso){
            titulo.text = "Estas en el descanso"
            subtitulo.text = "Intenta relajarte en estos $minDescanso minutos"

            duracionActividad = minDescanso * 60 * 1000

            //aviso de que va a acabar el descanso
            handler.postDelayed({
                titulo.text = "Tu descanso va a acabar"
                subtitulo.text = "Vuelve a concentrarte en la actividad"
                vibrator!!.vibrate(VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE))
            }, duracionActividad.toLong() - 10 * 1000)
        }

        if (servicioTerminado){
            Log.d("Notificacion", "Notificacion servicio")
            titulo.text = "Terminaste el tiempo de la tarea"
            subtitulo.text = "Ahora volverás al menú principal"
        }

        // Inicia el temporizador para autodestruir la actividad
        handler.postDelayed({
            if (hayDescanso || servicioTerminado) {
                vibrator!!.vibrate(VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            setShowWhenLocked(false)
            setTurnScreenOn(false)
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // Finaliza la actividad
            finish()
        }, duracionActividad.toLong()) // 10 segundos

        //Realizamos una vibracion al empezar la actividad
        if (vibrator != null ) {
            vibrator!!.vibrate(VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

}