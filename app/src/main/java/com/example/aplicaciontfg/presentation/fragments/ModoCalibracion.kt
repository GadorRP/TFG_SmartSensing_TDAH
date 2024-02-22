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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.DatosViewModel
import com.example.aplicaciontfg.presentation.IComunicacionActividadFragmentos


class ModoCalibracion : Fragment() {
    private var calibrado = MutableLiveData<Boolean>(false)
    private var pulsoMinimo = MutableLiveData<Int>(-1)
    private var pulsoMaximo = MutableLiveData<Int>(-1)
    private var obtenerValor = false
    private val viewModel : DatosViewModel by activityViewModels()

    //private lateinit var actividad: Activity
    private lateinit var sensorManager : SensorManager
    private var sensorPulso : Sensor? = null
    private lateinit var textPulso : TextView

    private val listenerPulso = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {

            if (event != null && obtenerValor && event.values[0] > 0) {
                obtenerValor = false

                if (pulsoMinimo.value == -1) {
                    pulsoMinimo.value = event.values[0].toInt()

                }
                else if(pulsoMaximo.value == -1){
                    pulsoMaximo.value = event.values[0].toInt()
                    calibrado.value = true
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sensorManager = viewModel.getSenManager()!!
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Registrar listener del sensor
        sensorPulso = sensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        sensorManager.registerListener(listenerPulso, sensorPulso, SensorManager.SENSOR_DELAY_NORMAL)

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_modo_calibracion, container, false)

        val textTitulo = root.findViewById<TextView>(R.id.textView1Cal)

        val textsubtitulo = root.findViewById<TextView>(R.id.textView2Cal)


        textPulso = root.findViewById<TextView>(R.id.textViewPulso)

        pulsoMinimo.observe(viewLifecycleOwner) { nuevoValor ->
            if (nuevoValor != -1) {
                textTitulo.text = "Vamos a movernos"
                textsubtitulo.text = "Realiza 5 saltos y pulse Tomar Pulso"
            }
        }

        calibrado.observe(viewLifecycleOwner) { nuevoValor ->
            if (nuevoValor == true) {
                stopListening()
                findNavController().navigate(ModoCalibracionDirections.actionModoCalibracionToResultadosCalibracion(
                    pulsoMinimo = pulsoMinimo.value!!, pulsoMaximo = pulsoMaximo.value!!)
                )

            }
        }

        val botonPulso = root.findViewById<Button>(R.id.buttonPulso)

        botonPulso.setOnClickListener {
            tomarPulso()
        }

        val botonVolver = root.findViewById<Button>(R.id.buttonCalVolver)

        botonVolver.setOnClickListener {
            stopListening()
            findNavController().navigate(R.id.action_modoCalibracion_to_menuPrincipal)
        }

        return root
    }


    fun tomarPulso() {
        obtenerValor = true
    }

    private fun stopListening() {
        sensorManager.unregisterListener(listenerPulso, sensorPulso)
    }

    /*override fun onPause() {
        super.onPause()
        stopListening()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listenerPulso, sensorPulso, SensorManager.SENSOR_DELAY_NORMAL)
    }*/


}