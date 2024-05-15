package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.DatosViewModel
import com.example.aplicaciontfg.presentation.service.BackServiceSensors


class ModoEstudio : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_modo_estudio, container, false)

        val viewModel : DatosViewModel by activityViewModels()

        var texto = root.findViewById<TextView>(R.id.subtituloEstudio)


        val calibrado = viewModel.getCalibrado()

        //Boton para programado
        val botonProgramar = root.findViewById<Button>(R.id.buttonProgramar)

        //Boton para sin fin
        val botonIndefinido = root.findViewById<Button>(R.id.buttonSinFinal)

        if (calibrado) {
            botonProgramar.setOnClickListener {
                findNavController().navigate(R.id.action_modoEstudio_to_selectorIntervalo)
            }


            botonIndefinido.setOnClickListener {
                findNavController().navigate(R.id.action_modoEstudio_to_modoServicio)
            }
        }
        else {
            botonProgramar.visibility = INVISIBLE
            botonIndefinido.visibility = INVISIBLE
            texto.text = "No se ha calibrado el reloj. Este modo no esta disponible"
            texto.setTextColor(Color.RED)
        }


        //Boton para volver
        val botonVolver = root.findViewById<Button>(R.id.buttonVolverEstudio)

        botonVolver.setOnClickListener {
            findNavController().navigate(R.id.action_modoEstudio_to_menuPrincipal)
        }



        return root
    }
}