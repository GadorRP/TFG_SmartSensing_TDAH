package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.service.BackServiceSensors


class ModoEstudio : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_modo_estudio, container, false)

        //Boton para programado
        val botonProgramar = root.findViewById<Button>(R.id.buttonProgramar)

        botonProgramar.setOnClickListener {
            findNavController().navigate(R.id.action_modoEstudio_to_selectorIntervalo)
        }

        //Boton para sin fin
        val botonIndefinido = root.findViewById<Button>(R.id.buttonSinFinal)

        botonIndefinido.setOnClickListener {
            findNavController().navigate(R.id.action_modoEstudio_to_modoServicio)
        }

        //Boton para volver
        val botonVolver = root.findViewById<Button>(R.id.buttonVolverEstudio)

        botonVolver.setOnClickListener {
            findNavController().navigate(R.id.action_modoEstudio_to_menuPrincipal)
        }



        return root
    }
}