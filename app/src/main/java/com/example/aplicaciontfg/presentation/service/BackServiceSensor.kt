package com.example.aplicaciontfg.presentation.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import java.util.Timer
import java.util.TimerTask


class BackServiceSensors : Service(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private val timer = Timer()
    private var ultimaLectura: Float? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // get sensor manager on starting the service
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // have a default sensor configured
        val sensorType = Sensor.TYPE_HEART_RATE

        val sensor = sensorManager!!.getDefaultSensor(sensorType)


        // in this use case since we work with the alarm manager
        sensorManager!!.registerListener(
            this, sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        Log.d("BackSensorServie" , "Registrado")

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        //Depuracion
        if (event != null && event.values[0] > 0) {
            Log.d("EventoTestForeground" , event.values[0].toString())
            ultimaLectura = event.values[0]

            sensorManager?.unregisterListener(this)
            stopSelf()
        }
        Log.d("Eventossssssss" , event.values[0].toString())

    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}