package rma.lv1.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import rma.lv1.model.BMIModel

class BMIViewModel : ViewModel() {
    private val bmiModel = BMIModel(weight = null, height = null, bmiResult = null)
    private val db = Firebase.firestore

    fun calculateBMI(weight: Float?, height: Float?): Float? {
        val bmi = weight?.div(((height?.div(100))?.times((height / 100))!!))
        bmiModel.height = height
        bmiModel.weight = weight
        bmiModel.bmiResult = bmi
        val docRef = db.collection("bmi").document("l1dskpUNYS5dM6a1IFFP")
        docRef.update("Tezina", bmiModel.weight)
            .addOnSuccessListener {
                Log.d("BMIViewModel", "Success updating weight")
            }
            .addOnFailureListener { e ->
                Log.e("BMIViewModel", "Error updating weight: $e")
            }
        docRef.update("Visina", bmiModel.height)
            .addOnSuccessListener {
                Log.d("BMIViewModel", "Success updating height")
            }
            .addOnFailureListener { e ->
                Log.e("BMIViewModel", "Error updating height: $e")
            }
        docRef.update("bmi", bmiModel.bmiResult)
            .addOnSuccessListener {
                Log.d("BMIViewModel", "Success updating bmi")
            }
            .addOnFailureListener { e ->
                Log.e("BMIViewModel", "Error updating Visina: $e")
            }
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val heightFromDB = document.getDouble("Visina")?.toFloat() ?: 0f
                    val weightFromDB = document.getDouble("Tezina")?.toFloat() ?: 0f
                    bmiModel.height = heightFromDB
                    bmiModel.weight = weightFromDB

                } else {
                    Log.d("MainActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error getting documents: ", exception)
            }

        return if (bmiModel.weight != null && bmiModel.height != null && bmiModel.height!! > 0) {
            val bmi = bmiModel.weight!! / ((bmiModel.height!! / 100) * (bmiModel.height!! / 100))
            bmiModel.bmiResult = bmi
            bmi
        } else {
            bmiModel.bmiResult = null
            null
        }
    }
}