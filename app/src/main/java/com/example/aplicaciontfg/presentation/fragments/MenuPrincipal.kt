package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.DatosViewModel


class MenuPrincipal : Fragment() {
    //val args : MenuPrincipalArgs by navArgs()
    //habia pulso minimo maximo y calibrado


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
            //DEPURACION
            //pulsoMinimo = 50
            //pulsoMaximo = 120


            //findNavController().navigate(MenuPrincipalDirections.actionMenuPrincipalToModoTest(
            //    pulsoMinimo = pulsoMinimo, pulsoMaximo = pulsoMaximo, calibrado = calibrado)
            //)
        }

        //Boton para calibrar
        val botonCalib = root.findViewById<Button>(R.id.buttonCalibrar)

        botonCalib.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipal_to_modoCalibracion)
        }

        return root
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.pulsoMinimo != -1 && args.pulsoMaximo != -1 || !calibrado){
            pulsoMinimo = args.pulsoMinimo
            pulsoMaximo = args.pulsoMaximo

            calibrado = true
        }
    }*/

}