/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.aplicaciontfg.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.aplicaciontfg.R
import com.example.aplicaciontfg.presentation.theme.AplicacionTFGTheme
import java.lang.Thread.sleep
import com.mutualmobile.composesensors.rememberAccelerometerSensorState


class MainActivity : ComponentActivity() {

    var acelerometroDatosAnteriores: AcelerometroDatos = AcelerometroDatos()
    var acelerometroDatosActuales: AcelerometroDatos = AcelerometroDatos()

    var giroscopioDatosAnteriores: GiroscopioDatos = GiroscopioDatos()
    var giroscopioDatosActuales: GiroscopioDatos = GiroscopioDatos()

    var sensores: Sensores? = null
    var sensorManager: SensorManager? = null
    var permisosDados = false

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

                //creo el manejador para los sensores
                sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

                //creo una variable de la clase Sensores que es la responsable de monitorizar
                sensores = Sensores(sensorManager!!)

                var acelerometroDatos = rememberAccelerometerSensorState(autoStart= true)

                if (permisosDados == true) {

                    sleep(4000)



                    // Obtengo los datos de los sensores
                    if (sensores != null) {
                        acelerometroDatosActuales = sensores!!.obtenerAcelerometroActual()
                        giroscopioDatosActuales = sensores!!.obtenerGiroscopioActual()
                    }

                    //WearApp("hola")
                    // Muestro los datos por pantalla
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Force X:  ${acelerometroDatos.xForce}" +
                            "\nForce Y:  ${acelerometroDatos.yForce} "+
                                    "\nForce Z: ${acelerometroDatos.zForce}",
                            textAlign = TextAlign.Center
                        )

                    }
                }
            }
        }
    }
}


    /*override fun onResume() {
        super.onResume()

        setContent() {

            // Obtenemos los permisos
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    permisosDados = isGranted
                }
            )

            if (permisosDados == true) {
                // Creo un temporizador que se ejecutará cada dos segundos
                val timer = Timer()
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {

                        // Obtengo los datos de los sensores
                        if (sensores != null) {
                            acelerometroDatosActuales = sensores!!.obtenerAcelerometroActual()
                            giroscopioDatosActuales = sensores!!.obtenerGiroscopioActual()
                        }

                        // Muestro los datos por pantalla
                        setContent {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Force X: ${acelerometroDatosActuales.ejeX}" +
                                            "\nForce Y: ${acelerometroDatosActuales.ejeY}" +
                                            "\nForce Z: ${acelerometroDatosActuales.ejeZ}",
                                    textAlign = TextAlign.Center
                                )

                            }
                        }
                    }
                }, 2000, 2000)
            }
        }
    }*/





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
