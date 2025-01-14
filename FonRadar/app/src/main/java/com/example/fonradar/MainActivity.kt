package com.example.fonradar

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(this)
                }
            }
        }
    }
}

// Retrofit API Service
interface CurrencyApiService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String,
        @Query("symbols") symbols: String
    ): FixerResponse
}

data class FixerResponse(
    val success: Boolean,
    val rates: Map<String, Double>?,
    val error: FixerError?
)

data class FixerError(
    val code: Int,
    val type: String,
    val info: String
)

@Composable
fun MainScreen(activity: ComponentActivity) {
    val apiService = Retrofit.Builder()
        .baseUrl("https://data.fixer.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CurrencyApiService::class.java)

    val apiKey = "166d1fbf3a0689293beb477cfb498aeb"
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getExchangeRates(
                    accessKey = apiKey,
                    symbols = "USD,EUR,TRY,JPY"
                )
                if (response.success && response.rates != null) {
                    rates = response.rates
                } else {
                    errorMessage = response.error?.info ?: "Unknown error occurred."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Failed to fetch exchange rates: ${e.localizedMessage}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Başlık
        Text(
            text = "FonRadar Dashboard",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
        } else if (rates.isNotEmpty()) {
            rates.forEach { (currency, rate) ->
                Text(
                    text = "$currency: ${String.format("%.2f", rate)}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        } else {
            Text(text = "Loading exchange rates...", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val intent = Intent(activity, CheckTransactionsActivity::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(text = "Check Transactions")
        }

        Button(
            onClick = {
                val intent = Intent(activity, EstimateMoneyActivity::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(text = "Estimate Money")
        }

        Button(
            onClick = {
                val intent = Intent(activity, MyRequestsActivity::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(text = "My Requests")
        }
    }
}
