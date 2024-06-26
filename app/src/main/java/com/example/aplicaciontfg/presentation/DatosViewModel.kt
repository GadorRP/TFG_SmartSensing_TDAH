package com.example.aplicaciontfg.presentation

import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.ViewModel

class DatosViewModel : ViewModel() {
    private var pulsoMinimo = -1
    private var pulsoMaximo = -1
    private var calibrado = false
    private var sensorManager: SensorManager? = null
    private var referenciaServicio : Unit? = null

    fun setSenManager(nuevoSensorManager: SensorManager) {
        sensorManager = nuevoSensorManager
        Log.d("SenManager", sensorManager.toString())
    }
    fun getSenManager(): SensorManager? {
        return sensorManager
    }


    fun getPulsoMinimo(): Int {
        return pulsoMinimo
    }

    fun setPulsoMinimo(nuevoPulso : Int) {
        pulsoMinimo = nuevoPulso
    }

    fun getPulsoMaximo(): Int {
        return pulsoMaximo
    }

    fun setPulsoMaximo(nuevoPulso : Int) {
        pulsoMaximo = nuevoPulso
    }

    fun getCalibrado(): Boolean {
        return calibrado
    }

    fun setCalibrado(nuevoCalibrado : Boolean) {
        calibrado = nuevoCalibrado
    }

    fun getReferenciaServicio(): Unit? {
        return referenciaServicio
    }

    fun setReferenciaServicio(referencia: Unit?){
        referenciaServicio = referencia
    }

}