package com.example.aplicaciontfg.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.ActivityNotificacion
import com.example.aplicaciontfg.presentation.Estado
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class BackServiceSensors : Service(), SensorEventListener {
    private val CHANNEL_ID = "ForegroundService SmartSensing"
    private var sensorManager: SensorManager? = null
    private var ultimaLectura: Float? = null
    private var hayDescanso = false
    private var minDescanso = -1

    private var pulsoMinimo = -1
    private var pulsoMaximo = -1
    private var rangoAbsoluto = -1
    private var rangoIntervalo = -1
    private var estadoActualInt = -1
    private var estadoActual = Estado.DESCONOCIDO

    private val executorService = Executors.newSingleThreadScheduledExecutor()

    companion object {
        fun startService(context: Context, descanso: Boolean, minDescanso : Int) {
            val startIntent = Intent(context, BackServiceSensors::class.java)
            startIntent.putExtra("infoDescanso", descanso)
            startIntent.putExtra("minDescanso", minDescanso)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, BackServiceSensors::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val preferencias = applicationContext.getSharedPreferences(
            "preferences_datos",Context.MODE_PRIVATE)

        pulsoMinimo = preferencias.getInt("pulsoMinimo", -1)
        pulsoMaximo = preferencias.getInt("pulsoMaximo", -1)

        calibrarMedicion()

        hayDescanso = intent.getBooleanExtra("infoDescanso", false)
        minDescanso = intent.getIntExtra("minDescanso", -1)

        Log.d("SharedPreferences", "pulsominimo $pulsoMinimo")
        Log.d("SharedPreferences", "pulsoMaximo $pulsoMaximo")

        // obtenemos el sensor manager al comenzar el servicio
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // sensor que queremos monitorizar
        val sensorType = Sensor.TYPE_HEART_RATE

        val sensor = sensorManager!!.getDefaultSensor(sensorType)

        //programamos las lecturas cada 20 segundos
        executorService.scheduleAtFixedRate(
            {
                sensorManager!!.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }, 0, 35 * 1000, TimeUnit.MILLISECONDS
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
        if (event.values[0] > 0) {
            Log.d("EventoTestForeground" , event.values[0].toString())
            ultimaLectura = event.values[0]

            obtenerEstado()

            if (estadoActual == Estado.EXCITADO || estadoActual == Estado.MUY_EXCITADO){
                val intent = Intent(this, ActivityNotificacion::class.java)
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
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

    override fun onDestroy() {
        super.onDestroy()
        executorService.shutdown()
        sensorManager?.unregisterListener(this)

        if (hayDescanso && minDescanso != 0){
            val intent = Intent(this, ActivityNotificacion::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)

            intent.putExtra("hayDescanso", hayDescanso)
            intent.putExtra("minDescanso", minDescanso)
            startActivity(intent)
        }

        Log.d("Servicio", "Servicio acabado")
    }

    private fun obtenerEstado() {
        var estado = 0
        var asignado = false

        for (i in pulsoMinimo..pulsoMaximo step rangoIntervalo){
            if (!asignado) {
                if (ultimaLectura!! >= i && ultimaLectura!! < i + rangoIntervalo ) { //si esta en el intervalo
                    if (estadoActualInt == -1) {
                        estadoActualInt = estado
                        asignado = true
                    }
                }else if (ultimaLectura!! <= pulsoMinimo){
                    if (estadoActualInt == -1){
                        estadoActualInt = 0
                        asignado = true
                    }
                }else if (ultimaLectura!! >= pulsoMaximo){
                    if (estadoActualInt == -1){
                        estadoActualInt = 4
                        asignado = true
                    }
                }
                else{
                    estado++
                }
            }
        }

        when (estadoActualInt) {
            0 -> estadoActual = Estado.MUY_RELAJADO
            1 -> estadoActual = Estado.RELAJADO
            2 -> estadoActual = Estado.NORMAL
            3 -> estadoActual = Estado.EXCITADO
            4 -> estadoActual = Estado.MUY_EXCITADO
            //dependiendo del intervalo (por la aproximacion) puede haber algún valor no obtenido en el 4
            5 -> estadoActual = Estado.MUY_EXCITADO
            else -> estadoActual  = Estado.DESCONOCIDO
        }
    }

    private fun calibrarMedicion(){
        rangoAbsoluto = pulsoMaximo - pulsoMinimo
        rangoIntervalo = rangoAbsoluto / 5

        val resto = rangoAbsoluto  % 5

        //comprobaciones
        if (resto > 2.5){
            rangoIntervalo += 1
        }
        else if (rangoIntervalo == 0){
            rangoIntervalo = 1
        }
    }
}