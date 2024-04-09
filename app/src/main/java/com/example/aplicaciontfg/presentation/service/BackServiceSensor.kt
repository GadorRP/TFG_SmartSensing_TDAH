package com.example.aplicaciontfg.presentation.service

import android.app.ForegroundServiceTypeException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.aplicaciontfg.R
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class BackServiceSensors : Service(), SensorEventListener {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    private var sensorManager: SensorManager? = null
    private var ultimaLectura: Float? = null
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, BackServiceSensors::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, BackServiceSensors::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // get sensor manager on starting the service
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // have a default sensor configured
        val sensorType = Sensor.TYPE_HEART_RATE

        val sensor = sensorManager!!.getDefaultSensor(sensorType)


        executorService.scheduleAtFixedRate(
            {
                sensorManager!!.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            },
            0,
            15000,
            TimeUnit.MILLISECONDS
        )

        // create notification channel for API 33
        createNotificationChannel()

        Log.d("BackSensorServie" , "Registrado")

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio de sensores en segundo plano")
            .setContentText("Este servicio está recopilando datos de los sensores.")
            .setSmallIcon(R.drawable.nervioso)
            .build()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        //Depuracion
        if (event != null && event.values[0] > 0) {
            Log.d("EventoTestForeground" , event.values[0].toString())
            ultimaLectura = event.values[0]

            sensorManager?.unregisterListener(this)
            //stopSelf()
            // Stop the service when the data is collected
            //stopForeground(STOP_FOREGROUND_REMOVE)
        }
        //Log.d("Eventossssssss" , event.values[0].toString())

    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}