package rma.lv1.view

import StepCounterViewModel
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun StepCounterScreen(navController: NavController) {
    val stepCounterViewModel: StepCounterViewModel = viewModel()
    StepCounter(navController = navController, viewModel = stepCounterViewModel)
}

@Composable
fun StepCounter(navController: NavController, viewModel: StepCounterViewModel) {
    val stepCount by viewModel.stepCount.collectAsState()

    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val sensorEventListener = remember { StepSensorEventListener(viewModel) }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        Column {
            Text(
                text = "Step Count",
                fontSize = 20.sp
            )
            Text(text = "Step Count: $stepCount", fontSize = 24.sp)
        }
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("User Info")
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
}

class StepSensorEventListener(private val viewModel: StepCounterViewModel) : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            viewModel.processSensorEvent(event.values[0], event.values[1], event.values[2])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

