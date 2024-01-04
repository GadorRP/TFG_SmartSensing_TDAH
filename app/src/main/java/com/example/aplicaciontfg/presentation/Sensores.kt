package com.example.aplicaciontfg.presentation


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.Text


class Sensores (private var sensorManager: SensorManager ) {

    //Sensores
    private var sensores: List<Sensor> = emptyList()

    // Listener de los eventos de los sensores
    private var listener: List<SensorEventListener> = emptyList()

    //Datos Sensores
    private var acelerometroDatosAnteriores: AcelerometroDatos = AcelerometroDatos()
    private var acelerometroDatosActuales: AcelerometroDatos = AcelerometroDatos()

    private var giroscopioDatosAnteriores: GiroscopioDatos = GiroscopioDatos()
    private var giroscopioDatosActuales: GiroscopioDatos = GiroscopioDatos()

    private var pulsoCardiaco = 0f

    val liveDataAcelerometro = MutableLiveData<AcelerometroDatos>()


    init {
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //sensores += acelerometro

        val acelerometroListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                procesarDatosAcelerometro(event)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //TODO("Not yet implemented")
            }
        }

        // Registra el SensorEventListener
        sensorManager.registerListener(acelerometroListener, acelerometro, SensorManager.SENSOR_DELAY_GAME)
        listener += acelerometroListener

        val giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        //sensores += giroscopio

        val giroscopioListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                Log.d("tfg","el evento es " + event.values[0])
                procesarDatosGiroscopio(event)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //TODO("Not yet implemented")
            }
        }
        sensorManager.registerListener(giroscopioListener, giroscopio, SensorManager.SENSOR_DELAY_GAME)
        listener += giroscopioListener

        val frecuenciaCardiaca = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        //sensores += frecuenciaCardiaca

        val frecuenciaListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                procesarDatosFrecuencia(event)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //TODO("Not yet implemented")
            }
        }
        sensorManager.registerListener(frecuenciaListener, frecuenciaCardiaca, SensorManager.SENSOR_DELAY_GAME)
        listener += frecuenciaListener
    }

    private fun procesarDatosFrecuencia(event: SensorEvent) {
        pulsoCardiaco = event.values[0]
    }

    private fun procesarDatosGiroscopio(event: SensorEvent) {
        giroscopioDatosAnteriores.ejeX = giroscopioDatosActuales.ejeX
        giroscopioDatosAnteriores.ejeY = giroscopioDatosActuales.ejeY
        giroscopioDatosAnteriores.ejeZ = giroscopioDatosActuales.ejeZ

        giroscopioDatosActuales.ejeX = event.values[0]
        giroscopioDatosActuales.ejeY = event.values[1]
        giroscopioDatosActuales.ejeZ = event.values[2]
    }

    private fun procesarDatosAcelerometro(event: SensorEvent) {
        acelerometroDatosAnteriores.ejeX = acelerometroDatosActuales.ejeX
        acelerometroDatosAnteriores.ejeY = acelerometroDatosActuales.ejeY
        acelerometroDatosAnteriores.ejeZ = acelerometroDatosActuales.ejeZ

        acelerometroDatosActuales.ejeX = event.values[0]
        acelerometroDatosActuales.ejeX = event.values[1]
        acelerometroDatosActuales.ejeX = event.values[2]

        liveDataAcelerometro.postValue(acelerometroDatosActuales)
    }


    fun obtenerFrecuencia(): Float {
        return pulsoCardiaco
    }

    fun obtenerAcelerometroAnterior() : AcelerometroDatos {
        return acelerometroDatosAnteriores
    }

    fun obtenerAcelerometroActual() : AcelerometroDatos {
        return acelerometroDatosActuales
    }

    fun obtenerGiroscopioAnterior() : GiroscopioDatos {
        return giroscopioDatosActuales
    }

    fun obtenerGiroscopioActual() : GiroscopioDatos {
        return giroscopioDatosActuales
    }

    fun getSensores() : List<Sensor> {
        return sensores
    }

    fun getListener() : List<SensorEventListener> {
        return listener
    }

}