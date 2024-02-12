package com.example.aplicaciontfg.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.Estado


class ResultadosTest : Fragment() {
    var estadoInicialInt = -1
    var estadoFinalInt = -1
    var estadoInicialString = Estado.DESCONOCIDO.toString()
    var estadoFinalString = Estado.DESCONOCIDO.toString()
    val args : ResultadosTestArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_resultados_test, container, false)

        //Boton para volver
        val botonVolver = root.findViewById<Button>(R.id.buttonVolverTest)

        botonVolver.setOnClickListener {
            findNavController().navigate(R.id.action_resultadosTest_to_menuPrincipal)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        estadoInicialInt = args.estadoInicial
        estadoFinalInt = args.estadoFinal

        traducirEstado()

        val textoEstados = view.findViewById<TextView>(R.id.tvResultadosTest)
        textoEstados.text = "Inicial: ${estadoInicialString.lowercase()} \n Final: ${estadoFinalString.lowercase()}"
    }

    private fun traducirEstado(){
        when (estadoInicialInt) {
            0 -> estadoInicialString = Estado.MUY_RELAJADO.toString()
            1 -> estadoInicialString = Estado.RELAJADO.toString()
            2 -> estadoInicialString = Estado.NORMAL.toString()
            3 -> estadoInicialString = Estado.EXCITADO.toString()
            4 -> estadoInicialString = Estado.MUY_EXCITADO.toString()
            else -> estadoInicialString  = Estado.DESCONOCIDO.toString()
        }

        when (estadoFinalInt) {
            0 -> estadoFinalString = Estado.MUY_RELAJADO.toString()
            1 -> estadoFinalString = Estado.RELAJADO.toString()
            2 -> estadoFinalString = Estado.NORMAL.toString()
            3 -> estadoFinalString = Estado.EXCITADO.toString()
            4 -> estadoFinalString = Estado.MUY_EXCITADO.toString()
            else -> estadoFinalString  = Estado.DESCONOCIDO.toString()
        }

        Log.d("valorEstadoInicialString" , estadoInicialString)
        Log.d("valorEstadoFinalString" , estadoFinalString)

    }


}