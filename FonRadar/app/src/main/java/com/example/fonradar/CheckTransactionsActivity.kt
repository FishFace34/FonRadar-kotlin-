package com.example.fonradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment

class CheckTransactionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CheckTransactionsPage()
                }
            }
        }
    }
}

@Composable
fun CheckTransactionsPage() {
    var drawerId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var checkNumber by remember { mutableStateOf("") }
    var branchCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Check Transactions",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields
        CustomTextField(label = "Drawer ID", value = drawerId) { drawerId = it }
        CustomTextField(label = "Title of Drawer", value = title) { title = it }
        CustomTextField(label = "Bank Name", value = bankName) { bankName = it }
        CustomTextField(label = "Account Number", value = accountNumber) { accountNumber = it }
        CustomTextField(label = "Check Number", value = checkNumber) { checkNumber = it }
        CustomTextField(label = "Branch Code", value = branchCode) { branchCode = it }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                println("Submitted: $drawerId, $title, $bankName, $accountNumber, $checkNumber, $branchCode")
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}
