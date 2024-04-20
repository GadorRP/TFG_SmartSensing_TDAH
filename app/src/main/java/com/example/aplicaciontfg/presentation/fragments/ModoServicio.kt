package com.example.aplicaciontfg.presentation.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.ActivityNotificacion
import com.example.aplicaciontfg.presentation.DatosViewModel
import com.example.aplicaciontfg.presentation.Estado
import com.example.aplicaciontfg.presentation.service.BackServiceSensors


class ModoServicio : Fragment() {
    private var minTarea = -1
    private var minDescanso = -1
    private var hayDescanso = false
    private var sinFin = false
    private var broadcastRegistrado : Intent ? = null
    private var ultimoEstado = Estado.DESCONOCIDO.toString()
    private val args : ModoServicioArgs by navArgs()
    private val viewModel : DatosViewModel by activityViewModels()
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            ultimoEstado = intent.getStringExtra("Estado").toString()
            Log.d("ModoServicio", "Recibido estado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var servicioIniciado = viewModel.getReferenciaServicio()

        if (servicioIniciado == null){
            minTarea = args.minsServicio
            minDescanso = args.minsDescanso
            hayDescanso = args.hayDescanso

            if (minTarea == -1)
                sinFin = true
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
            BackServiceSensors.stopService(requireActivity().applicationContext)
            findNavController().navigate(R.id.action_modoServicio_to_menuPrincipal)
        }

        val subtexto = root.findViewById<TextView>(R.id.subtextServicio)

        val botonEstado = root.findViewById<Button>(R.id.buttonEstadoSer)

        botonEstado.setOnClickListener {
            subtexto.text = "Tu estado es $ultimoEstado"
        }

        if (viewModel.getReferenciaServicio() == null)
            lanzaServicio()


        return root
    }


    private fun lanzaServicio(){

        //creamos la referencia al servicio
        val context = requireActivity().applicationContext

        var referenciaServicio = BackServiceSensors.startService(context, hayDescanso, minDescanso)
        viewModel.setReferenciaServicio(referenciaServicio)

        if (!sinFin && !hayDescanso){
            // Creamos la detencion del servicio
            val handler = Handler(Looper.getMainLooper())

            val runnable = Runnable {
                BackServiceSensors.stopService(context)
                viewModel.setReferenciaServicio(null)
            }

            // Ajusta el tiempo en segundos
            handler.postDelayed(runnable, minTarea.toLong() * 60 * 1000)

        }else if (hayDescanso){
            val mitad = minTarea / 2f

            // Creamos la detencion del servicio
            val handler = Handler(Looper.getMainLooper())

            val runnableSegundo = Runnable {
                var referenciaServicio2 = BackServiceSensors.startService(context,descanso = false, minDescanso)
                viewModel.setReferenciaServicio(referenciaServicio2)

                Log.d("Servicio con Descanso", "Comenzado segundo servicio")

                // Empezamos una cuenta atras para acabar el servicio
                val durationServicio = mitad * 60 * 1000
                val countDownTimer = object : CountDownTimer(durationServicio.toLong(), 10000) {
                    override fun onTick(milisHastaTerminar: Long) {
                    }

                    override fun onFinish() {
                        // termina el servicio
                        BackServiceSensors.stopService(context)
                        viewModel.setReferenciaServicio(null)

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
                viewModel.setReferenciaServicio(null)
                handler.postDelayed(runnableSegundo, minDescanso.toLong() * 60 * 1000)
                minDescanso = 0
            }

            // Ajusta el tiempo en segundos
            handler.postDelayed(runnablePrimero, mitad.toLong() * 60 * 1000)
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(receiver, IntentFilter("MensajeServicio"))
    }

    override fun onPause() {
        super.onPause()
        getActivity()?.unregisterReceiver(receiver);
    }

}