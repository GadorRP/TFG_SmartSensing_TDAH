package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.service.BackServiceSensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MenuPrincipal : Fragment() {
    //private val ioScope = CoroutineScope(Dispatchers.IO)

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_menu_principal, container, false)

        //Boton para test
        val botonTest = root.findViewById<Button>(R.id.buttonTest)

        botonTest.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipal_to_modoTest)
        }

        //Boton para calibrar
        val botonCalib = root.findViewById<Button>(R.id.buttonCalibrar)

        botonCalib.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipal_to_modoCalibracion)
        }

        //startServiceAndRepeat()

        // Get scheduler and prepare intent
        //val scheduler = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //val intent = Intent(context,BackServiceSensors::class.java)
        //val scheduledIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Start the service
        //scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, scheduledIntent)

        //CON ESTO FNCIONA
        //Log.d("Servicio cada 5 segundos", "Ejecutando tarea")
        val context = requireActivity().applicationContext
        val intent = Intent(context, BackServiceSensors::class.java)
        intent.putExtra("inputExtra", "Mensaje para el servicio")
        ContextCompat.startForegroundService(context, intent)

        Log.d("EventoTestForeground" , "hola")
        return root
    }
    /*private fun startServiceAndRepeat() {
        ioScope.launch {
            while (isActive) {
                // Logic to be executed every 5 seconds
                Log.d("Servicio cada 5 segundos", "Ejecutando tarea")
                BackServiceSensors.startService(requireContext(), "Mensaje desde el fragmento")
                delay(15000L) // Wait for 5 seconds
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.cancel()
    }*/

}