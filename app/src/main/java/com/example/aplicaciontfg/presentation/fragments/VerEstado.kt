package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.graphics.Color
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.DatosViewModel
import com.example.aplicaciontfg.presentation.Estado
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class VerEstado : Fragment() {
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    private lateinit var sensorManager : SensorManager
    private var obtenerEvSensor = true
    private var sensor : Sensor? = null
    private val viewModel : DatosViewModel by activityViewModels()

    private var pulsoMinimo = -1
    private var pulsoMaximo = -1
    private var calibrado = false
    private var rangoAbsoluto = -1
    private var rangoIntervalo = -1
    private var pulsoActual = -1
    private var estadoActual = MutableLiveData<Estado>(Estado.DESCONOCIDO)

    private val listenerPulso = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null && obtenerEvSensor && event.values[0] > 0){
                obtenerEvSensor = false
                pulsoActual = event.values[0].toInt()
                Log.d("ValorEstado", "$pulsoActual")
                obtenerEstado()
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
        val root = inflater.inflate(R.layout.fragment_ver_estado, container, false)

        val texto = root.findViewById<TextView>(R.id.textEstado)

        val subTexto = root.findViewById<TextView>(R.id.subtextEstado)

        val boton = root.findViewById<Button>(R.id.buttonVolverEstado)

        boton.setOnClickListener {
            stopListening()
            findNavController().navigate(R.id.action_verEstado_to_menuPrincipal)
        }

        calibrado = viewModel.getCalibrado();

        if (calibrado) {
            pulsoMinimo = viewModel.getPulsoMinimo()
            pulsoMaximo = viewModel.getPulsoMaximo()

            calibrarMedicion()

            //Registrar listener del sensor
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
            sensorManager.registerListener(listenerPulso, sensor,
                SensorManager.SENSOR_DELAY_NORMAL)

            //programamos las lecturas cada 30 segundos
            executorService.scheduleAtFixedRate(
                {
                    obtenerEvSensor = true
                }, 0, 30 * 1000, TimeUnit.MILLISECONDS
            )

            if (estadoActual.value == Estado.DESCONOCIDO) {
                texto.text = "Estamos obteniendo tu estado. Espera unos segundos"
            }

            estadoActual.observe(viewLifecycleOwner) {nuevoEstado ->

                if (nuevoEstado == Estado.EXCITADO || nuevoEstado == Estado.MUY_EXCITADO || nuevoEstado == Estado.MUY_RELAJADO){
                    var estadoString = ""

                    if (nuevoEstado == Estado.MUY_EXCITADO)
                        estadoString = "muy nervioso"
                    else if (nuevoEstado == Estado.EXCITADO)
                        estadoString = "nervioso"
                    else
                        estadoString = "muy relajado"

                    texto.text = "Tu estado actual es $estadoString "
                }
                else
                    texto.text = "Tu estado actual es ${nuevoEstado.toString().lowercase()} "

                if (nuevoEstado == Estado.EXCITADO || nuevoEstado == Estado.MUY_EXCITADO){
                    subTexto.text = "Intenta relajarte antes de estar con la tarea"
                }else {
                    subTexto.text = "Es un buen estado para realizar las tareas"
                }

                if (nuevoEstado == Estado.DESCONOCIDO) {
                    texto.text = "Estamos obteniendo tu estado. Espera unos segundos"
                    subTexto.visibility = INVISIBLE
                }else
                    subTexto.visibility = VISIBLE
            }
        }
        else {
            texto.text = "Debes calibrar primero el reloj"
            subTexto.text = "Vuelve al menú principal"
            texto.setTextColor(Color.RED)
        }

        return root
    }

    private fun obtenerEstado() {
        var estado = 0
        var estadoActualInt = -1
        var asignado = false

        for (i in pulsoMinimo..pulsoMaximo step rangoIntervalo){
            if (!asignado) {
                if (pulsoActual >= i && pulsoActual < i + rangoIntervalo ) { //si esta en el intervalo
                    if (estadoActualInt == -1) {
                        estadoActualInt = estado
                        asignado = true
                    }
                }else if (pulsoActual <= pulsoMinimo){
                    if (estadoActualInt == -1){
                        estadoActualInt = 0
                        asignado = true
                    }
                }else if (pulsoActual!! >= pulsoMaximo){
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
            0 -> estadoActual.value = Estado.MUY_RELAJADO
            1 -> estadoActual.value = Estado.RELAJADO
            2 -> estadoActual.value = Estado.NORMAL
            3 -> estadoActual.value = Estado.EXCITADO
            4 -> estadoActual.value = Estado.MUY_EXCITADO
            //dependiendo del intervalo (por la aproximacion) puede haber algún valor no obtenido en el 4
            5 -> estadoActual.value = Estado.MUY_EXCITADO
            else -> estadoActual.value  = Estado.DESCONOCIDO
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

    override fun onDestroy() {
        super.onDestroy()
        stopListening()
    }

    private fun stopListening() {
        if (sensor != null)
            sensorManager.unregisterListener(listenerPulso, sensor)
    }
}