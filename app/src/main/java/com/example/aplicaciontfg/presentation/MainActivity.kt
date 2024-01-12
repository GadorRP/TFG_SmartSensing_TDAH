/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.aplicaciontfg.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.theme.AplicacionTFGTheme
import com.mutualmobile.composesensors.rememberAccelerometerSensorState
import java.lang.Thread.sleep


class MainActivity : ComponentActivity() {

    var acelerometroDatosAnteriores: AcelerometroDatos = AcelerometroDatos()
    var acelerometroDatosActuales: AcelerometroDatos = AcelerometroDatos()

    var giroscopioDatosAnteriores: GiroscopioDatos = GiroscopioDatos()
    var giroscopioDatosActuales: GiroscopioDatos = GiroscopioDatos()

    var pulsoCardiaco = 0f

    var sensorManager: SensorManager? = null
    var permisosDados = false
    var listenerAniadidos = false

    var acelerometro: Sensor? = null
    var giroscopio: Sensor? = null
    var pulsaciones: Sensor? = null

    var eventNum = 0
    var estado = mutableStateOf("Inicial")

    val listenerSensores : SensorEventListener = object : SensorEventListener{
        override fun onSensorChanged(event: SensorEvent?) {
            //obtengo el tipo de sensor
            val tipoSensor = event?.sensor?.type;

            if (tipoSensor == Sensor.TYPE_ACCELEROMETER) {
                eventNum++

                if (eventNum == 20){
                    acelerometroDatosAnteriores.ejeX = acelerometroDatosActuales.ejeX
                    acelerometroDatosAnteriores.ejeY = acelerometroDatosActuales.ejeY
                    acelerometroDatosAnteriores.ejeZ = acelerometroDatosActuales.ejeZ

                    acelerometroDatosActuales.ejeX = event.values[0]
                    acelerometroDatosActuales.ejeY = event.values[1]
                    acelerometroDatosActuales.ejeZ = event.values[2]

                    Log.d("Sensores", acelerometroDatosActuales.ejeX.toString());

                    eventNum = 0
                    procesarDatos()
                }

            }
            else if (tipoSensor == Sensor.TYPE_GYROSCOPE){
                giroscopioDatosAnteriores.ejeX = giroscopioDatosActuales.ejeX
                giroscopioDatosAnteriores.ejeY = giroscopioDatosActuales.ejeY
                giroscopioDatosAnteriores.ejeZ = giroscopioDatosActuales.ejeZ

                giroscopioDatosActuales.ejeX = event.values[0]
                giroscopioDatosActuales.ejeY = event.values[1]
                giroscopioDatosActuales.ejeZ = event.values[2]
            }
            else if (tipoSensor == Sensor.TYPE_HEART_RATE){
                pulsoCardiaco = event.values[0]
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d("MyActivity", "Accuracy: " + accuracy);
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent() {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center,
            ) {

                val lifecycleState by LocalLifecycleOwner.current.lifecycle.observeAsState()

                // Obtenemos los permisos
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        permisosDados = isGranted
                    }
                )

                LaunchedEffect(lifecycleState) {
                    if (lifecycleState == Lifecycle.Event.ON_RESUME) {
                        permisosDados = tengoPermisos
                        if (permisosDados != true) {
                            permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
                        }
                    }
                }

                if (permisosDados == true) {

                    sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

                    //obtengo los sensores que voy a monitorizar
                    acelerometro = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                    giroscopio = sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
                    pulsaciones = sensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)

                    if (!listenerAniadidos) {
                        sensorManager!!.registerListener(listenerSensores, acelerometro, 10000000)
                        sensorManager!!.registerListener(listenerSensores, giroscopio, 10000000)
                        sensorManager!!.registerListener(listenerSensores, pulsaciones, 10000000)

                        listenerAniadidos = true
                    }

                    // Muestro los datos por pantalla
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Estado: " + estado.value,
                            textAlign = TextAlign.Center
                            /*text = "Force X: ${acelerometroDatosActuales.ejeX}" +
                                    "\nForce hola Y: ${giroscopioDatosActuales.ejeY}" +
                                    "\nForce Z: ${acelerometroDatosActuales.ejeZ}",
                            textAlign = TextAlign.Center*/
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Register the listener for each sensor
        if (sensorManager != null) {
            if (!listenerAniadidos) {
                sensorManager!!.registerListener(listenerSensores, acelerometro, 10000000)
                sensorManager!!.registerListener(listenerSensores, giroscopio, 10000000)
                sensorManager!!.registerListener(listenerSensores, pulsaciones, 10000000)

                listenerAniadidos = true
            }
        }
    }

    /*override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(listenerSensores, acelerometro)
        sensorManager!!.unregisterListener(listenerSensores, giroscopio)
        sensorManager!!.unregisterListener(listenerSensores, pulsaciones)
        listenerAniadidos = false
    }*/

    fun procesarDatos() {
        if (estado.value == "bien")
            estado.value = "mal"
        else
            estado.value = "bien"
    }
}



@Composable
fun WearApp(greetingName: String) {
    AplicacionTFGTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}

private val Activity.tengoPermisos: Boolean
    get() {
        return checkSelfPermission(Manifest.permission.BODY_SENSORS) ==
                PackageManager.PERMISSION_GRANTED
    }

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event -> state.value = event }
        this@observeAsState.addObserver(observer)
        onDispose { this@observeAsState.removeObserver(observer) }
    }
    return state
}
