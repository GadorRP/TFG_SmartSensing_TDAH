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
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.DatosViewModel
import com.example.aplicaciontfg.presentation.IComunicacionActividadFragmentos


class ModoTest : Fragment() {
    private var pulsoMinimo = -1
    private var pulsoMaximo = -1

    private var pulsoInicial = MutableLiveData<Int>(-1)
    private var pulsoFinal = MutableLiveData<Int>(-1)
    private var calibrado = false
    private val args : ModoTestArgs by navArgs()
    private var obtenerEvSensor = false

    private var rangoAbsoluto = -1
    private var rangoIntervalo = -1
    private var estadoInicial = MutableLiveData<Int>(-1)
    private var estadoFinal = MutableLiveData<Int>(-1)
    private lateinit var textoCalibrado: TextView
    private lateinit var textoPrincipal: TextView

    private lateinit var actividad: IComunicacionActividadFragmentos
    private lateinit var sensorManager : SensorManager
    private var sensorPulso : Sensor? = null
    private val viewModel : DatosViewModel by activityViewModels()

    private val listenerPulso = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            //Depuracion
            //if (event != null) {
            //    Log.d("EventoTest" , event.values[0].toString())
            //}

            if (event != null && obtenerEvSensor && event.values[0] > 0){
                obtenerEvSensor = false

                if (pulsoInicial.value == -1)
                    pulsoInicial.value = event.values[0].toInt()
                else{
                    pulsoFinal.value = event.values[0].toInt()
                    Log.d("valorPulsoFinal" , pulsoFinal.value.toString())
                }

                //obtenerEstado(event.values[0].toInt())
            }

        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = viewModel.getSenManager()!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_modo_test, container, false)

        textoCalibrado = root.findViewById<TextView>(R.id.tvCalibrado)

        textoPrincipal = root.findViewById<TextView>(R.id.tvTest1)

        //era pulsoInicial y no pulsoInicial
        pulsoInicial.observe(viewLifecycleOwner) { nuevoEstado ->
            if (nuevoEstado != -1) {
                textoPrincipal.text = "Al finalizar la prueba pulse Estado para terminar"
            }
        }

        //era estado final y no pulsoFinal
        pulsoFinal.observe(viewLifecycleOwner) { nuevoEstado ->
            if (nuevoEstado != -1) {
                stopListening()
                obtenerEstado()
                findNavController().navigate(ModoTestDirections.actionModoTestToResultadosTest(
                    estadoInicial = estadoInicial.value!!, estadoFinal = estadoFinal.value!!,
                    pulsoInicial = pulsoInicial.value!!, pulsoFinal = pulsoFinal.value!!)
                )

            }
        }

        val botonEstado = root.findViewById<Button>(R.id.buttonEstado)

        botonEstado.setOnClickListener {
            obtenerEvSensor = true
        }

        val botonVolver = root.findViewById<Button>(R.id.buttonTestVolver)

        botonVolver.setOnClickListener {
            stopListening()

            findNavController().navigate(R.id.action_modoTest_to_menuPrincipal)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //DEPURACION
        //viewModel.setPulsoMaximo(111)
        //viewModel.setPulsoMinimo(80)
        //viewModel.setCalibrado(true)


        calibrado = viewModel.getCalibrado();


        if (calibrado) {
            pulsoMinimo = viewModel.getPulsoMinimo()
            pulsoMaximo = viewModel.getPulsoMaximo()

            rangoAbsoluto = pulsoMaximo - pulsoMinimo
            rangoIntervalo = rangoAbsoluto / 5

            var resto = rangoAbsoluto  % 5

            //comprobaciones
            if (resto > 2.5){
                rangoIntervalo += 1
            }
            else if (rangoIntervalo == 0){
                rangoIntervalo = 1
            }


            Log.d("valorRango" , rangoIntervalo.toString())

            //Registrar listener del sensor
            sensorPulso = sensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)
            sensorManager.registerListener(listenerPulso, sensorPulso, SensorManager.SENSOR_DELAY_NORMAL)

            textoCalibrado.visibility = INVISIBLE
            textoPrincipal.visibility = VISIBLE
        }

    }

    private fun obtenerEstado(){
        var estado = 0
        var asignado = false
        pulsoInicial.value = 110

        for (i in pulsoMinimo..pulsoMaximo step rangoIntervalo){
            if (!asignado) {
                if (pulsoInicial.value!! >= i && pulsoInicial.value!! < i + rangoIntervalo ){
                    if (estadoInicial.value == -1){
                        estadoInicial.value = estado
                        Log.d("valorPulsoInicial" , pulsoInicial.value.toString())
                        asignado = true
                    }
                }else if (pulsoInicial.value!! <= pulsoMinimo){
                    if (estadoInicial.value == -1){
                        estadoInicial.value = 0
                        asignado = true
                    }
                }else if (pulsoInicial.value!! >= pulsoMaximo){
                    if (estadoInicial.value == -1){
                        estadoInicial.value = 4
                        asignado = true
                    }
                }
                else{
                    estado++
                }
            }
        }

        estado = 0

        Log.d("PREPulsoFinal" , pulsoFinal.value.toString())
        for (i in pulsoMinimo..pulsoMaximo step rangoIntervalo){
            if (pulsoFinal.value!! >= i && pulsoFinal.value!! < i + rangoIntervalo ){
                if (estadoFinal.value == -1){
                    estadoFinal.value = estado
                    Log.d("valorPulsoFinal" , pulsoFinal.value.toString())
                }
                return
            }else if (pulsoFinal.value!! < pulsoMinimo){
                if (estadoFinal.value == -1){
                    estadoFinal.value = 0
                }
                return
            }else if (pulsoFinal.value!! > pulsoMaximo){
                if (estadoFinal.value == -1){
                    estadoFinal.value = 4
                }
                return
            }
            else{
                estado++
            }
        }

    }

    //CAMBIADO PARA NO HACERLO CON EL SENSOR
    private fun obtenerEstado(valorPulso : Int) {
        var estado = 0

        for (i in pulsoMinimo..pulsoMaximo step rangoIntervalo){
            if (valorPulso >= i && valorPulso < i + rangoIntervalo ){
                if (estadoInicial.value == -1){
                    estadoInicial.value = estado
                    Log.d("valorPulsoInicial" , valorPulso.toString())
                }
                else{
                    estadoFinal.value = estado
                    Log.d("valorPulsoFinal" , valorPulso.toString())
                }

                return
            }else if (valorPulso < pulsoMinimo){
                if (estadoInicial.value == -1){
                    estadoInicial.value = 0
                }
                else{
                    estadoFinal.value = 0
                }

                return
            }else if (valorPulso > pulsoMaximo){
                if (estadoInicial.value == -1){
                    estadoInicial.value = 4
                }
                else{
                    estadoFinal.value = 4
                }

                return
            }
            else{
                estado++
            }
        }

    }

    private fun stopListening() {
        if (sensorPulso != null)
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


}