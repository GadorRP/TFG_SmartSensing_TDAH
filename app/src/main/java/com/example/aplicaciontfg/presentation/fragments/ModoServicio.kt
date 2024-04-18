package com.example.aplicaciontfg.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.ActivityNotificacion
import com.example.aplicaciontfg.presentation.service.BackServiceSensors

class ModoServicio : Fragment() {
    private var minTarea = -1
    private var minDescanso = -1
    private var hayDescanso = false
    private var sinFin = false
    val args : ModoServicioArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        minTarea = args.minsServicio
        minDescanso = args.minsDescanso
        hayDescanso = args.hayDescanso

        if (minTarea == -1)
            sinFin = true

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
            BackServiceSensors.stopService(requireActivity().applicationContext)
            findNavController().navigate(R.id.action_modoServicio_to_menuPrincipal)
        }

        val botonEstado = root.findViewById<Button>(R.id.buttonEstadoSer)

        botonEstado.setOnClickListener {

        }

        lanzaServicio()


        return root
    }

    private fun lanzaServicio(){

        //creamos la referencia al servicio
        val context = requireActivity().applicationContext

        BackServiceSensors.startService(context, hayDescanso, minDescanso)

        if (!sinFin && !hayDescanso){
            // Creamos la detencion del servicio
            val handler = Handler(Looper.getMainLooper())

            val runnable = Runnable {
                BackServiceSensors.stopService(context)
            }

            // Ajusta el tiempo en segundos
            handler.postDelayed(runnable, minTarea.toLong() * 60 * 1000)

        }else if (hayDescanso){
            val mitad = minTarea / 2f

            // Creamos la detencion del servicio
            val handler = Handler(Looper.getMainLooper())

            val runnableSegundo = Runnable {
                BackServiceSensors.startService(context,descanso = false, minDescanso)
                Log.d("Servicio con Descanso", "Comenzado segundo servicio")

                // Empezamos una cuenta atras para acabar el servicio
                val durationServicio = mitad * 60 * 1000
                val countDownTimer = object : CountDownTimer(durationServicio.toLong(), 10000) {
                    override fun onTick(milisHastaTerminar: Long) {
                    }

                    override fun onFinish() {
                        // termina el servicio
                        BackServiceSensors.stopService(context)

                        //crea la actividad de notificacion
                        var intent = Intent(context, ActivityNotificacion::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("servicioTerminado", true)
                        startActivity(intent)

                        handler.postDelayed({
                            findNavController().navigate(R.id.action_modoServicio_to_menuPrincipal)
                        },10 * 1000 + 300)

                        Log.d("Servicio con Descanso", "Parado segundo servicio")
                    }
                }
                countDownTimer.start()
            }

            val runnablePrimero = Runnable {
                Log.d("Servicio con Descanso", "Parado primer servicio")
                BackServiceSensors.stopService(context)
                handler.postDelayed(runnableSegundo, minDescanso.toLong() * 60 * 1000)
                minDescanso = 0
            }

            // Ajusta el tiempo en segundos
            handler.postDelayed(runnablePrimero, mitad.toLong() * 60 * 1000)
        }

    }

    /*
        //creamos la referencia al servicio
        val context = requireActivity().applicationContext

        val intent = Intent(context, BackServiceSensors::class.java)
        intent.putExtra("inputExtra", "Mensaje para el servicio")

        //Iniciamos
        ContextCompat.startForegroundService(context, intent)
        Log.d("Control Servicio", "Se ha iniciado el servicio")

        if (!sinFin){
            // Creamos la detencion del servicio
            val handler = Handler(Looper.getMainLooper())

            val runnable = Runnable {
                val stopIntent = Intent(context, BackServiceSensors::class.java)
                Log.d("Control SERVICIO","Se ha parado el servicio")
                context?.stopService(stopIntent)
            }

            // Ajusta el tiempo en segundos
            handler.postDelayed(runnable, duracion.toLong() * 60 * 1000)
            }
        */


}