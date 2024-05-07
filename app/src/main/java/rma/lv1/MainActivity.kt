package rma.lv1

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import rma.lv1.ui.theme.LV1Theme
import java.security.AllPermission
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main_screen") {
                composable("main_screen") {
                    MainScreen(navController = navController)
                }
                composable("step_counter") {
                    StepCounter(navController = navController)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun UserPreview() {
    val db = Firebase.firestore
    val name = "Marko"
    var newTezina by remember { mutableStateOf(0f) }
    var newVisina by remember { mutableStateOf(0f) }
    var newBmi by remember { mutableStateOf(0f) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Pozdrav $name!",
            fontSize = 20.sp,
            lineHeight = 56.sp,
            modifier= Modifier
                .padding(top = 8.dp)
                .padding(start = 10.dp)
        )
        TextField(
            value = newTezina.takeIf { it != 0f }?.toString() ?: "",
            onValueChange = { newTezina = it.toFloatOrNull() ?: 0f },
            label = { Text("Nova Tezina:") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        TextField(
            value = newVisina.takeIf { it != 0f }?.toString() ?: "",
            onValueChange = { newVisina = it.toFloatOrNull() ?: 0f },
            label = { Text("Nova Visina:") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Button(
            onClick = {
            val docRef = db.collection("bmi").document("l1dskpUNYS5dM6a1IFFP")
            docRef.update("Tezina", newTezina)
                .addOnSuccessListener {
                    Log.d("MainActivtiy", "Success updating Tezina")
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Error updating Tezina: $e")
                }
            docRef.update("Visina", newVisina)
                .addOnSuccessListener {
                    Log.d("MainActivtiy", "Success updating Visina")
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Error updating Visina: $e")
                }
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val tezina = document.getDouble("Tezina")?.toFloat() ?: 0f
                        val visina = document.getDouble("Visina")?.toFloat() ?: 0f
                        newTezina = tezina
                        newVisina = visina
                        newBmi = newTezina / (newVisina * newVisina)
                    } else {
                        Log.d("MainActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting documents: ", exception)
                }
            }) {
            Text("Izracunaj BMI")
        }

        Text(
            text = "Tvoj BMI je:",
            fontSize = 55.sp,
            lineHeight = 61.sp,
            textAlign = TextAlign.Center,
        )

        Text(
            text = String.format("%.2f", newBmi),
            fontSize = 70.sp,
            lineHeight = 72.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}


@Composable
fun MainScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment =
    Alignment.Center) {

        BackgroundImage(modifier = Modifier.fillMaxSize())
        UserPreview()
        // Button to navigate to StepCounter
        Button(
            onClick = {
                // Navigate to OtherScreen when button clicked
                navController.navigate("step_counter")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(text = "Step Counter")
        }
    }
}

@Composable
fun StepCounter(navController: NavController) {
    val db = Firebase.firestore

    val sensorManager = (LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val sensorEventListener = remember { StepSensorEventListener() }

    var stepCount by remember { mutableStateOf(0) }

    val docRef = db.collection("steps").document("VhiYFlAiLDwzEsfBhnss")
    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                stepCount = document.getDouble("stepsNumber")?.toInt() ?: 0
            } else {
                Log.d("MainActivity", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("MainActivity", "Error getting documents: ", exception)
        }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        Column {
            Text(
                text = "Step Count",
                fontSize = 20.sp
            )
            Text(text = "Step Count: $stepCount", fontSize = 24.sp)
        }
        // Back button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("User Info")
        }
    }

    // Register sensor listener when the composable is first composed
    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        docRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("MainActivity", "Error listening to step count: ", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                stepCount = snapshot.getDouble("stepsNumber")?.toInt() ?: 0
            } else {
                Log.d("MainActivity", "No such document")
            }
        }
        onDispose {
            // Unregister sensor listener when the composable is disposed
            sensorManager.unregisterListener(sensorEventListener)
        }

    }
}

class StepSensorEventListener : SensorEventListener {
    private val db = Firebase.firestore
    // Variable to hold the step count
    val docRef = db.collection("steps").document("VhiYFlAiLDwzEsfBhnss")
    private var stepCount = 0

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val accelerationX = event.values[0]
            val accelerationY = event.values[1]
            val accelerationZ = event.values[2]

            val threshold = kotlin.math.sqrt(
                accelerationX * accelerationX +
                        accelerationY * accelerationY +
                        accelerationZ * accelerationZ
            )

            val STEP_THRESHOLD = 13.0f

            val docRef = db.collection("steps").document("VhiYFlAiLDwzEsfBhnss")
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        stepCount = document.getDouble("stepsNumber")?.toInt() ?: 0
                    } else {
                        Log.d("MainActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting documents: ", exception)
                }

            // Check if the threshold exceeds the predefined value
            if (threshold > STEP_THRESHOLD) {
                stepCount++
                docRef.update("stepsNumber", stepCount)
                    .addOnSuccessListener {
                        Log.d("MainActivtiy", "Success updating steps")
                    }
                    .addOnFailureListener { e ->
                        Log.e("MainActivity", "Error updating steps: $e")
                    }
            }
        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}

@Composable
fun BackgroundImage(modifier: Modifier) {
    Box (modifier){ Image(
        painter = painterResource(id = R.drawable.fitness),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.1F
    )
    }
}
