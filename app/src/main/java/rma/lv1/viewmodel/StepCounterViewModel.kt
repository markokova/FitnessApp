import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StepCounterViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    init {
        fetchStepCount()
        listenToStepCountChanges()
    }

    private fun fetchStepCount() {
        val docRef = db.collection("steps").document("VhiYFlAiLDwzEsfBhnss")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _stepCount.value = document.getDouble("stepsNumber")?.toInt() ?: 0
                }
            }
            .addOnFailureListener { exception ->
                Log.e("StepCounterViewModel", "Error getting document: ", exception)
            }
    }

    private fun listenToStepCountChanges() {
        val docRef = db.collection("steps").document("VhiYFlAiLDwzEsfBhnss")
        docRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("StepCounterViewModel", "Error listening to step count: ", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                _stepCount.value = snapshot.getDouble("stepsNumber")?.toInt() ?: 0
            }
        }
    }

    fun updateStepCount(newStepCount: Int) {
        val docRef = db.collection("steps").document("VhiYFlAiLDwzEsfBhnss")
        docRef.update("stepsNumber", newStepCount)
            .addOnSuccessListener {
                Log.d("StepCounterViewModel", "Successfully updated steps")
            }
            .addOnFailureListener { e ->
                Log.e("StepCounterViewModel", "Error updating steps: $e")
            }
    }

    fun processSensorEvent(accelerationX: Float, accelerationY: Float, accelerationZ: Float) {
        val threshold = kotlin.math.sqrt(
            accelerationX * accelerationX +
                    accelerationY * accelerationY +
                    accelerationZ * accelerationZ
        )

        val STEP_THRESHOLD = 13.0f

        if (threshold > STEP_THRESHOLD) {
            _stepCount.value += 1
            updateStepCount(_stepCount.value)
        }
    }
}
