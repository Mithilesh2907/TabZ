package com.example.tabz.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabz.data.AppDAO
import com.example.tabz.data.TransactionItem
import com.example.tabz.data.TransactionType
import com.example.tabz.viewmodel.TransactionViewModel
import com.example.tabz.viewmodel.TransactionViewModelFactory

@Composable
fun TransactionScreen(
    dao: AppDAO,
    tabId: Int,
    tabName: String,
    onNavigateBack: () -> Unit
) {
    val transactionViewModel : TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(dao,tabId)
    )

    val context = LocalContext.current
    val transactions by transactionViewModel.transactions.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var transactionToDelete by rememberSaveable { mutableStateOf<TransactionItem?>(null) }

    Scaffold(
        topBar = {
            AppBar(tabName,true, navigateBack = onNavigateBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add,"Add transaction")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            items(transactions){transaction ->
                SingleRecordCard(
                    name = transaction.description,
                    amount = transaction.amount,
                    onDeleteClick = {
                        transactionToDelete = transaction
                    }
                )
            }
        }
    }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = {
                Text("Delete transaction?")
            },
            text = {
                Text("Are you sure you want to delete this transaction?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        transactionViewModel.deleteTransaction(transactionToDelete!!)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        transactionToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDialog) {
        AddTransactionDialog(
            tabName = tabName,
            onDismissRequest = { showDialog = false },
            onConfirm = { description, amount ->
                transactionViewModel.addTransaction(description,amount)
                Toast.makeText(context,"Transaction added !!",Toast.LENGTH_SHORT).show()
                showDialog = false
            }
        )
    }
}


@Composable
fun AddTransactionDialog(
    tabName: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var selectedType by rememberSaveable { mutableStateOf(TransactionType.TAKE) }

    var description by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val I_TAKE_COLOR = Color(0xff1db954)
    val I_GIVE_COLOR = Color.White
    val TEXT_ON_PRIMARY_COLOR = Color.White
    val TEXT_ON_SECONDARY_COLOR = Color.Black

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Add Transaction") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-1).dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            selectedType = TransactionType.TAKE
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(topStart = 50f, bottomStart = 50f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.TAKE) I_TAKE_COLOR else I_GIVE_COLOR,
                            contentColor = if (selectedType == TransactionType.TAKE) TEXT_ON_PRIMARY_COLOR else TEXT_ON_SECONDARY_COLOR
                        )
                    ) {
                        Text("I Take")
                    }

                    Button(
                        onClick = {
                            selectedType = TransactionType.GIVE
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(topEnd = 50f, bottomEnd = 50f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.GIVE) I_TAKE_COLOR else I_GIVE_COLOR,
                            contentColor = if (selectedType == TransactionType.GIVE) TEXT_ON_PRIMARY_COLOR else TEXT_ON_SECONDARY_COLOR
                        )
                    ) {
                        Text("I Give")
                    }
                }

                Text(
                    text = if (selectedType == TransactionType.TAKE) "$tabName owes you" else "you owe $tabName",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Enter description") }
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        isError = it.toDoubleOrNull() == null && it.isNotEmpty()
                    },
                    placeholder = { Text("Enter amount") },
                    isError = isError,
                    supportingText = { if (isError) Text("Invalid number")}
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (description.isNotBlank() && amountDouble != null) {
                        val finalAmount = if (selectedType == TransactionType.TAKE) amountDouble else -1*amountDouble
                        onConfirm(description,finalAmount)
                    }
                },
                enabled = description.isNotBlank() && amount.toDoubleOrNull() != null
            ) { Text("Add") }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) { Text("Cancel") }
        }
    )
}