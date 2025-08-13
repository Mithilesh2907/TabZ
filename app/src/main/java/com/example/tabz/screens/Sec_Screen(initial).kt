package com.example.tabz.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.tabz.data.Transactions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Second_Screen() {
    val context = LocalContext.current
    val transactions = remember{ mutableStateListOf<Transactions>()}
    var showDialog by remember { mutableStateOf(false) }
    var inputAmount by remember { mutableStateOf("") }
    var inputName by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            AppBar("Person",true) { }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            items(transactions) {transaction ->
                SingleRecordCard(transaction.transactionName,transaction.amount)
            }
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add new transaction") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        placeholder = { Text("Context(Lunch, breakfast etc)") }
                    )
                    TextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        placeholder = { Text("Enter transaction") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputAmount.isNotBlank()) {
                            transactions.add(Transactions(amount = inputAmount.toDouble(), transactionName = inputName))
                            Toast.makeText(context,"Transaction added",Toast.LENGTH_SHORT).show()
                        }
                        inputAmount = ""
                        inputName = ""
                        showDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDialog = false
                        inputAmount = ""
                        inputName = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}


@Preview(showBackground = true)
@Composable
fun SecScreenPreview() {
    Second_Screen()
}

//@Preview(showBackground = true)
//@Composable
//fun DialogPreview() {
//
//}