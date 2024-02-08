package com.example.aplicaciontfg.presentation.fragments

import android.content.Context
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
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.IComunicacionActividadFragmentos


class ModoTest : Fragment() , IComunicacionActividadFragmentos{
    private var pulsoMinimo = -1
    private var pulsoMaximo = -1
    private val args : MenuPrincipalArgs by navArgs()
    private var obtenerEvSensor = false

    private var rangoAbsoluto = -1
    private var rangoIntervalo = -1
    private var estadoInicial = MutableLiveData<Int>(-1)
    private var estadoFinal = MutableLiveData<Int>(-1)

    private lateinit var actividad: IComunicacionActividadFragmentos
    private lateinit var sensorManager : SensorManager
    private var sensorPulso : Sensor? = null

    private val listenerPulso = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                Log.d("EventoTest" , event.values[0].toString())
            }

            if (event != null && obtenerEvSensor && event.values[0] > 0){
                obtenerEvSensor = false

                obtenerEstado(event.values[0].toInt())
            }

        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Registrar listener del sensor
        sensorPulso = sensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        sensorManager.registerListener(listenerPulso, sensorPulso, SensorManager.SENSOR_DELAY_NORMAL)

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_modo_test, container, false)

        val texto = root.findViewById<TextView>(R.id.tvTest1)

        estadoInicial.observe(viewLifecycleOwner) { nuevoEstado ->
            if (nuevoEstado != -1) {
                texto.text = "Al finalizar la prueba pulse el botón para obtener su estado"
            }
        }

        estadoFinal.observe(viewLifecycleOwner) { nuevoEstado ->
            if (nuevoEstado != -1) {
                stopListening()
                Log.d("valorEstadoInicial" , estadoInicial.value.toString())
                Log.d("valorEstadoFinal" , estadoFinal.value.toString())
                findNavController().navigate(ModoTestDirections.actionModoTestToResultadosTest(
                    estadoInicial = estadoInicial.value!!, estadoFinal = estadoFinal.value!!)
                )

            }
        }

        val botonEstado = root.findViewById<Button>(R.id.buttonEstado)

        botonEstado.setOnClickListener {
            obtenerEvSensor = true
        }

        val botonVolver = root.findViewById<Button>(R.id.buttonTestVolver)

        botonVolver.setOnClickListener {
            findNavController().navigate(R.id.action_modoTest_to_menuPrincipal)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pulsoMinimo = args.pulsoMinimo
        pulsoMaximo = args.pulsoMaximo

        rangoAbsoluto = pulsoMaximo - pulsoMinimo
        rangoIntervalo = rangoAbsoluto / 5
    }

    private fun obtenerEstado(valorPulso : Int) {
        var estado = 0

        for (i in pulsoMinimo..pulsoMaximo step rangoIntervalo){
            if (valorPulso >= i && valorPulso < i + rangoIntervalo){
                if (estadoInicial.value == -1){
                    estadoInicial.value = estado
                    Log.d("valorPulsoInicial" , valorPulso.toString())
                }
                else{
                    estadoFinal.value = estado
                    Log.d("valorPulsoFinal" , valorPulso.toString())
                }


                return
            }else{
                estado++
            }
        }

    }

    private fun stopListening() {
        sensorManager.unregisterListener(listenerPulso, sensorPulso)
    }

    /**override fun onPause() {
        super.onPause()
        stopListening()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listenerPulso, sensorPulso, SensorManager.SENSOR_DELAY_NORMAL)
    }**/

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is IComunicacionActividadFragmentos) {
            actividad = context
            sensorManager = actividad.getSenManager()!!
        } else {
            throw RuntimeException("La actividad no implementa la interfaz IComunicacionActividadFragmento")
        }
    }


    override fun getSenManager(): SensorManager? {
        return actividad.getSenManager()
    }

}