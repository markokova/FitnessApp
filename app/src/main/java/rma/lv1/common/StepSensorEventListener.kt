package rma.lv1.common

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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