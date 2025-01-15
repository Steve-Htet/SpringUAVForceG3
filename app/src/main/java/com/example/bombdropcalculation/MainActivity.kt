package com.example.bombdropcalculation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bombdropcalculation.ui.theme.BombDropCalculationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BombDropCalculationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BombDropApp()
                }
            }
        }
    }
}

@Composable
fun BombDropApp() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background), // Replace with your logo
            contentDescription = null,
            modifier = Modifier.wrapContentSize(),
            contentScale = ContentScale.Crop
        )

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "Spring UAV Force G-3",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )

            // Subtitle
            Text(
                text = "Bomb Drop Calculation",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Yellow
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculator
            BombDropCalculator()
        }
    }
}

@Composable
fun BombDropCalculator() {
    var altitude by remember { mutableStateOf("") }
    var velocity by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = altitude,
            onValueChange = { altitude = it },
            label = { Text("Altitude (m)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = velocity,
            onValueChange = { velocity = it },
            label = { Text("Velocity (km/h)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                result = calculateBombDrop(altitude, velocity, weight)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }

        Text(
            text = result,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

fun calculateBombDrop(altitudeInput: String, velocityInput: String, weightInput: String): String {
    return try {
        val altitude = altitudeInput.toDouble()
        val velocity = velocityInput.toDouble()
        val weight = weightInput.toDouble()

        var v_x = velocity * (1000.0 / 3600.0)
        var v_y = 0.0
        val g = 9.81
        val dragCoefficient = 0.5
        val airDensity = 1.225
        val area = 0.00002

        var x = 0.0
        var y = altitude
        var t = 0.0
        val delta_t = 0.01

        while (y > 0) {
            val F_d_x = 0.5 * dragCoefficient * airDensity * area * v_x * v_x
            val F_d_y = 0.5 * dragCoefficient * airDensity * area * v_y * v_y

            val a_x = -F_d_x / weight
            val a_y = -g - (F_d_y / weight)

            v_x += a_x * delta_t
            v_y += a_y * delta_t

            x += v_x * delta_t
            y += v_y * delta_t

            t += delta_t
        }

        val dropPoint = "%.2f".format(x)
        val timeTaken = "%.2f".format(t)
        val tunePoint = "%.2f".format(x * 4)

        """
            Results:
            Time taken: $timeTaken seconds
            Drop point: $dropPoint meters
            Tune point: $tunePoint meters
        """.trimIndent()
    } catch (e: Exception) {
        "Error: Please enter valid numerical values."
    }
}
