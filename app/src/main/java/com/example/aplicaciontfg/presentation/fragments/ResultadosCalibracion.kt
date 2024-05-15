package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfgprototipo.R
import com.example.aplicaciontfg.presentation.DatosViewModel

class ResultadosCalibracion : Fragment() {
    var pulsoMinimo = -1
    var pulsoMaximo = -1
    var malCalibrado = false
    val args : ResultadosCalibracionArgs by navArgs()
    private val viewModel : DatosViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pulsoMinimo = args.pulsoMinimo
        pulsoMaximo = args.pulsoMaximo


        if (pulsoMinimo != -1 && pulsoMaximo != -1){

            if (pulsoMinimo < pulsoMaximo){
                val preferencias = requireActivity().getPreferences(Context.MODE_PRIVATE)
                val editor = preferencias.edit()

                //actualizar los datos persistentes
                editor.putInt("pulsoMinimo", pulsoMinimo)
                editor.putInt("pulsoMaximo", pulsoMaximo)
                editor.commit()

                //actualizar viewmodel
                viewModel.setPulsoMinimo(pulsoMinimo)
                viewModel.setPulsoMaximo(pulsoMaximo)
                viewModel.setCalibrado(true)
            }else {
                malCalibrado = true
            }
        }

        val textoPulsaciones = view.findViewById<TextView>(R.id.tvResultadosCal)

        if (!malCalibrado){
            textoPulsaciones.text = "Tu pulso mínimo es $pulsoMinimo \n Tu pulso máximo es $pulsoMaximo"
        }
        else {
            textoPulsaciones.text = "No se ha calibrado correctamente"
        }
    }

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