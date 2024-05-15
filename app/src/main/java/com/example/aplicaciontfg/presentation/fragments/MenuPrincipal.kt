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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_menu_principal, container, false)

        //Boton para test
        val botonVerEstado = root.findViewById<Button>(R.id.buttonVerEstado)

        botonVerEstado.setOnClickListener {
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

        return root
    }


}