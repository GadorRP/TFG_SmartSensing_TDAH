package com.example.aplicaciontfg.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R
import com.gildaswise.horizontalcounter.HorizontalCounter


class SelectorIntervalo : Fragment() {
    private var minTarea: Double? = null
    private var minDescanso: Double? = null
    private var repetir = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_selector_intervalo, container, false)

        val contador = root.findViewById<HorizontalCounter>(R.id.horizontal_counter)

        val checkBox = root.findViewById<CheckBox>(R.id.checkBox)

        val texto = root.findViewById<TextView>(R.id.textSeleccion)

        val botonHecho = root.findViewById<Button>(R.id.buttonHechoSel)

        botonHecho.setOnClickListener {
            val numeroActual = contador.currentValue
            if  (minTarea == null) {
                minTarea = numeroActual
                texto.text = "¿Cuantos minutos \n dura tu descanso?"
            }else if (minDescanso == null){
                minDescanso = numeroActual
                contador.visibility = INVISIBLE
                checkBox.visibility = VISIBLE
            }
        }

        val botonVolver = root.findViewById<Button>(R.id.buttonVolverSel)

        botonVolver.setOnClickListener {
            findNavController().navigate(R.id.action_selectorIntervalo_to_menuPrincipal)
        }
        return root
    }


}