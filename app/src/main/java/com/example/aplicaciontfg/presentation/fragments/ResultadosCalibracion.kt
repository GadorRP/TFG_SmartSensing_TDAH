package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R

class ResultadosCalibracion : Fragment() {
    var pulsoMinimo = -1
    var pulsoMaximo = -1
    val args : ResultadosCalibracionArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pulsoMinimo = args.pulsoMinimo
        pulsoMaximo = args.pulsoMaximo

        val textoPulsaciones = view.findViewById<TextView>(R.id.tvResultadosCal)
        textoPulsaciones.text = "Tu pulso mínimo es $pulsoMinimo \n Tu pulso máximo es $pulsoMaximo"
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_resultados_calibracion, container, false)

        //Boton para volver
        val botonVolver = root.findViewById<Button>(R.id.buttonVolverCal)

        botonVolver.setOnClickListener {
            findNavController().navigate(ResultadosCalibracionDirections.actionResultadosCalibracionToMenuPrincipal(
                pulsoMinimo = pulsoMinimo, pulsoMaximo = pulsoMaximo)
            )
        }

        return root
    }

}