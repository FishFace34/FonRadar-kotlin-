package com.example.fonradar

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.app.DatePickerDialog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class EstimateMoneyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            EstimateMoneyPage(this)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "estimate_notifications",
                "Estimate Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for estimate results"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun EstimateMoneyPage(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    val savedAmount = sharedPreferences.getString("lastAmount", "") ?: ""
    val savedEstimate = sharedPreferences.getString("lastEstimate", "") ?: ""

    var amount by remember { mutableStateOf(savedAmount) }
    var expiryDate by remember { mutableStateOf("") }
    var estimatedValue by remember { mutableStateOf(savedEstimate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Estimate Money",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = amount,
            onValueChange = { amount = it.filter { char -> char.isDigit() } },
            label = { Text("Enter Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = expiryDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Expiry Date") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    val datePicker = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            expiryDate = "$year-${month + 1}-$dayOfMonth"
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePicker.datePicker.minDate = System.currentTimeMillis() + 86400000
                    datePicker.show()
                }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (amount.isNotEmpty() && expiryDate.isNotEmpty()) {
                    val p = amount.toDoubleOrNull() ?: 0.0
                    val n = expiryDate.takeIf { it.isNotEmpty() }?.let {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val selectedDate = dateFormat.parse(it)
                        ((selectedDate?.time ?: 0) - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
                    }?.toInt() ?: 0

                    val estimate = calculateEstimate(p, 0.002, n)
                    estimatedValue = formatEstimate(estimate)

                    saveToSharedPreferences(sharedPreferences, amount, estimatedValue)

                    sendNotification(context, estimatedValue)
                } else {
                    estimatedValue = "Please enter valid inputs."
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Calculate")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (estimatedValue.isNotEmpty()) {
            Text(
                text = estimatedValue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun sendNotification(context: Context, estimatedValue: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationId = 1

    val notification = NotificationCompat.Builder(context, "estimate_notifications")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Estimation Complete")
        .setContentText("Your estimated value is $estimatedValue")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    notificationManager.notify(notificationId, notification)
}


fun formatEstimate(value: Double): String {
    return String.format(Locale.getDefault(), "Estimate: %.2f", value)
}

fun calculateEstimate(principal: Double, rate: Double, days: Int): Double {
    return principal * Math.pow((1 - rate), days.toDouble())
}

fun saveToSharedPreferences(sharedPreferences: android.content.SharedPreferences, amount: String, estimatedValue: String) {
    with(sharedPreferences.edit()) {
        putString("lastAmount", amount)
        putString("lastEstimate", estimatedValue)
        apply()
    }
}
