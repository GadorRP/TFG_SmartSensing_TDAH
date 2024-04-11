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
import com.example.aplicaciontfg.presentation.ActivityNotificacion
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class BackServiceSensors : Service(), SensorEventListener {
    private val CHANNEL_ID = "ForegroundService SmartSensing"
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
        // obtenemos el sensor manager al comenzar el servicio
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // sensor que queremos monitorizar
        val sensorType = Sensor.TYPE_HEART_RATE

        val sensor = sensorManager!!.getDefaultSensor(sensorType)

        //programamos las lecturas cada 15 segundos
        executorService.scheduleAtFixedRate(
            {
                sensorManager!!.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }, 0, 15000, TimeUnit.MILLISECONDS
        )

        // creamos el canal para la notificacion
        createNotificationChannel()

        Log.d("BackSensorService" , "Registrado")

        //creamos la notificacion y comenzamos el servicio
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Leyendo sensores")
            .setContentText("SmartSensing esta recabando informacion")
            .setSmallIcon(R.drawable.nervioso)
            .build()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event != null && event.values[0] > 0) {
            Log.d("EventoTestForeground" , event.values[0].toString())
            ultimaLectura = event.values[0]

            if (ultimaLectura!! > 70){
                startActivity(Intent(this, ActivityNotificacion::class.java))
            }

            sensorManager?.unregisterListener(this)
        }
        //Log.d("Eventossssssss" , event.values[0].toString())

    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID,
            "Smart Sensing Foreground Service",
            NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)

    }
}