package com.example.aplicaciontfg.presentation

class AcelerometroDatos() {
    var ejeX: Float = 0f
    var ejeY: Float = 0f
    var ejeZ: Float = 0f
}

class GiroscopioDatos () {
    var ejeX: Float = 0f
    var ejeY: Float = 0f
    var ejeZ: Float = 0f
}

enum class Estado {
    MUY_RELAJADO,
    RELAJADO,
    NORMAL,
    EXCITADO,
    MUY_EXCITADO,
    DESCONOCIDO
}

