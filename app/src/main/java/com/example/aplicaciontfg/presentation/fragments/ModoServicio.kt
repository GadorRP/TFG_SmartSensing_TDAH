package com.example.aplicaciontfg.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.service.BackServiceSensors

class ModoServicio : Fragment() {
    private var minTarea = -1
    private var minDescanso = -1
    private var hayDescanso = false
    private val context = requireActivity().applicationContext
    val args : ModoServicioArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        minTarea = args.minsServicio
        minDescanso = args.minsDescanso
        hayDescanso = args.hayDescanso

        //creamos la referencia al servicio
        val intent = Intent(context, BackServiceSensors::class.java)
        intent.putExtra("inputExtra", "Mensaje para el servicio")

        if (!hayDescanso){ //comenzamos el servicio con el tiempo elegido
            //Iniciar el servicio
            ContextCompat.startForegroundService(context, intent)
            Log.d("Control Servicio", "Se ha iniciado el servicio")

            // Iniciar la detención del servicio después de un tiempo determinado
            val handler = Handler(Looper.getMainLooper())

            val runnable = Runnable {
                finalizarServicio()
            }

            // Ajusta el tiempo en segundos
            handler.postDelayed(runnable, (minTarea * 60 * 1000).toLong())
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_modo_servicio, container, false)

        //Boton para Finalizar el servicio
        val botonFinalizar = root.findViewById<Button>(R.id.buttonFinalServ)

        botonFinalizar.setOnClickListener {
            finalizarServicio()
            findNavController().navigate(R.id.action_modoServicio_to_menuPrincipal)
        }

        val botonEstado = root.findViewById<Button>(R.id.buttonEstadoSer)

        botonEstado.setOnClickListener {

        }

        return root
    }

    fun finalizarServicio() {
        val stopIntent = Intent(context, BackServiceSensors::class.java)
        Log.d("Control SERVICIO","Se ha parado el servicio")
        context?.stopService(stopIntent)
    }


}