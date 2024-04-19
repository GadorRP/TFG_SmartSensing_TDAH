package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R


class MenuPrincipal : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_menu_principal, container, false)

        //Boton para test
        val botonTest = root.findViewById<Button>(R.id.buttonTest)

        botonTest.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipal_to_verEstado)
        }

        //Boton para calibrar
        val botonCalib = root.findViewById<Button>(R.id.buttonCalibrar)

        botonCalib.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipal_to_modoCalibracion)
        }

        //Boton para estudio
        val botonEstudio = root.findViewById<Button>(R.id.buttonEstudio)

        botonEstudio.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipal_to_modoEstudio)
        }

        /*//creamos la referencia al servicio
        val context = requireActivity().applicationContext
        val intent = Intent(context, BackServiceSensors::class.java)
        intent.putExtra("inputExtra", "Mensaje para el servicio")

        //Iniciar el servicio
        ContextCompat.startForegroundService(context, intent)
        Log.d("Control Servicio", "Se ha iniciado el servicio")

        // Iniciar la detención del servicio después de un tiempo determinado
        val handler = Handler(Looper.getMainLooper())

        val runnable = Runnable {
            val stopIntent = Intent(context, BackServiceSensors::class.java)
            Log.d("Control SERVICIO","Se ha parado el servicio")
            context?.stopService(stopIntent)
        }

        // Ajusta el tiempo en segundos
        handler.postDelayed(runnable, ( 60 * 1000).toLong())*/




        return root
    }


}