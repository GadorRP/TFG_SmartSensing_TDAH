package com.example.aplicaciontfg.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.DatosViewModel
import com.gildaswise.horizontalcounter.HorizontalCounter


class SelectorIntervalo : Fragment() {
    private var minTarea: Int? = null
    private var minDescanso: Int? = null
    private var hayDescanso = false
    private var pulsaciones = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_selector_intervalo, container, false)

        val contador = root.findViewById<HorizontalCounter>(R.id.horizontal_counter)

        val checkBox = root.findViewById<CheckBox>(R.id.checkBox)
        val textoCheckBox = root.findViewById<TextView>(R.id.textCheckBox)

        val texto = root.findViewById<TextView>(R.id.textSeleccion)

        val botonSiguiente = root.findViewById<Button>(R.id.buttonHechoSel)

        botonSiguiente.setOnClickListener {
            val numeroActual = contador.currentValue
            pulsaciones++

            //primera pulsacion
            if (pulsaciones == 0){
                texto.text = "¿Cuantos minutos \n dura tu tarea?"
                contador.visibility = VISIBLE
            }
            //se obtiene el contador
            else if  (pulsaciones == 1 && minTarea == null) {
                minTarea = numeroActual.toInt()
                texto.visibility = INVISIBLE
                contador.visibility = INVISIBLE
                checkBox.visibility = VISIBLE
                textoCheckBox.visibility = VISIBLE
            }
            //despues de asignar la duracion de la tarea se pregunta si se quiere descanso
            else if (pulsaciones == 2){
                hayDescanso = checkBox.isChecked

                if (hayDescanso ){
                    checkBox.visibility = INVISIBLE
                    textoCheckBox.visibility = INVISIBLE
                    texto.text = "¿Cuantos minutos \n dura tu descanso?"
                    botonSiguiente.text = "finalizar"
                    texto.visibility = VISIBLE
                    contador.visibility = VISIBLE
                }
                else {
                    texto.text = "Al dar a finalizar comenzará tu tarea"
                    texto.visibility = VISIBLE
                    checkBox.visibility = INVISIBLE
                    textoCheckBox.visibility = INVISIBLE
                }
            }
            //hay descanso, se obteniene la duracion
            else if ( pulsaciones == 3 && hayDescanso  && minDescanso == null){
                texto.text = "Al dar a finalizar comenzará tu tarea"
                texto.visibility = VISIBLE
                contador.visibility = INVISIBLE
                minDescanso = numeroActual.toInt()
            }
            //no hay descanso se pasa al servicio
            else if (pulsaciones == 3 && !hayDescanso){
                findNavController().navigate(SelectorIntervaloDirections.actionSelectorIntervaloToModoServicio(
                    minsServicio = minTarea!!))
            }
            else if (pulsaciones == 4 && hayDescanso){
                findNavController().navigate(SelectorIntervaloDirections.actionSelectorIntervaloToModoServicio(
                    minsServicio = minTarea!!, minsDescanso = minDescanso!!, hayDescanso = true)
                )
            }

        }

        val botonVolver = root.findViewById<Button>(R.id.buttonVolverSel)

        botonVolver.setOnClickListener {
            findNavController().navigate(R.id.action_selectorIntervalo_to_menuPrincipal)
        }

        return root
    }


}