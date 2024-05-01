package rma.lv1

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import rma.lv1.ui.theme.LV1Theme
import java.security.AllPermission

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LV1Theme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    BackgroundImage(modifier = Modifier)
                }
            }
        }
    }
}



@SuppressLint("DefaultLocale")
@Composable
fun UserPreview(name: String, modifier: Modifier = Modifier) {
    val db = Firebase.firestore
    var newTezina by remember { mutableStateOf(0f) }
    var newVisina by remember { mutableStateOf(0f) }
    var newBmi by remember { mutableStateOf(0f) }

    Text(
        text = "Pozdrav $name!",
        fontSize = 20.sp,
        lineHeight = 56.sp,
        modifier= Modifier
            .padding(top= 8.dp)
            .padding(start =10.dp)
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
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
fun BackgroundImage(modifier: Modifier) {

    Box (modifier){ Image(
        painter = painterResource(id = R.drawable.fitness),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.1F
    )
        UserPreview(name = "Marko", modifier = Modifier.fillMaxSize())
    }

}
@Preview(showBackground = false)
@Composable
fun UserPreview() {
    LV1Theme {
        BackgroundImage(modifier = Modifier)   }
}