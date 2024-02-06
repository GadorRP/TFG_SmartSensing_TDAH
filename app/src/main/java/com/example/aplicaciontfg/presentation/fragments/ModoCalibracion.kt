package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.IComunicacionActividadFragmentos


class ModoCalibracion : Fragment(), IComunicacionActividadFragmentos {
    private var calibrado = false
    private var pulsoMinimo = -1
    private var pulsoMaximo = -1
    private lateinit var actividad: IComunicacionActividadFragmentos
    private lateinit var sensorManager : SensorManager
    private var escuchando = false
    private var pulsaciones : Sensor? = null

    val listenerPulso = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null && escuchando && event.values[0] > 0) {
                escuchando = false

                if (pulsoMinimo != -1) {
                    pulsoMinimo = event.values[0].toInt()
                }
                else {
                    pulsoMaximo = event.values[0].toInt()
                }

                stopListening()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_modo_calibracion, container, false)

        val textTitulo = root.findViewById<TextView>(R.id.textView1Cal)

        val texsubtitulo = root.findViewById<TextView>(R.id.textView2Cal)

        val botonPulso = root.findViewById<Button>(R.id.buttonPulso)

        botonPulso.setOnClickListener {
            tomarPulso()
        }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is IComunicacionActividadFragmentos) {
            actividad = context
            sensorManager = actividad.getSensorManager()!!
        } else {
            throw RuntimeException("La actividad no implementa la interfaz IComunicacionActividadFragmento")
        }
    }

    fun tomarPulso() {

        //obtengo el sensor de la frecuencia cardiaca
        pulsaciones = sensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        escuchando = true
        sensorManager.registerListener(listenerPulso, pulsaciones, SensorManager.SENSOR_DELAY_NORMAL)

    }

    private fun stopListening() {
        sensorManager.unregisterListener(listenerPulso, pulsaciones)
    }

    override fun getSensorManager(): SensorManager? {
        return actividad.getSensorManager()
    }



}