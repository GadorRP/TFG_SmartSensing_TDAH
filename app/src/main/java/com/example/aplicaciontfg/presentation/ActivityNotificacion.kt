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
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicaciontfg.R


class ActivityNotificacion : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var keyguardManager: KeyguardManager? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificacion)

        Log.d("ACTIVIDAD NUEVA" , "CREADO")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager: VibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager!!.requestDismissKeyguard(this, null)
        }

        // Vibra al iniciar la actividad
        val duracion = 200L
        if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator!!.vibrate(VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE))

        }

        // Inicia el temporizador para autodestruir la actividad
        handler.postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(false)
                setTurnScreenOn(false)
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            // Finaliza la actividad
            finish()
        }, 10000) // 10 segundos
    }

}