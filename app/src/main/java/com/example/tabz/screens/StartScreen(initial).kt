package com.example.tabz.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tabz.data.Records
import java.text.NumberFormat


@Composable
fun MyScreen() {
    val context = LocalContext.current
    val records = remember { mutableStateListOf<Records>() }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var input by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { AppBar("TabZ",false) { } },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(records) { record ->
                SingleRecordCard(
                    name = record.name,
                    amount = record.totalAmount
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Record") },
                text = {
                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { Text("Enter name or record") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                        if (input.isNotBlank()) {
                            records.add(Records(name = input))
                            Toast.makeText(context, "Record Added", Toast.LENGTH_SHORT).show()
                        }
                        input = ""
                        showDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                        showDialog = false
                        input = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(title)
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            if (canNavigateBack){
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        modifier = Modifier,
    )
}


@Composable
fun SingleRecordCard(
    name: String,
    amount: Double,
    onShareClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    var menuExapanded by rememberSaveable { mutableStateOf(false) }

    val amountColor = if (amount<0) Color.Red else Color(0xff008000)
    val formattedAmount = NumberFormat.getCurrencyInstance().format(amount)
    val signedAmount = if (amount >= 0) "+$formattedAmount" else formattedAmount

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = signedAmount,
                color = amountColor,
                style = MaterialTheme.typography.bodyLarge
            )
            Box {
                IconButton(
                    onClick = { menuExapanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                DropdownMenu(
                    expanded = menuExapanded,
                    onDismissRequest = { menuExapanded = false }
                ) {
                    if (onDeleteClick != null) {
                        DropdownMenuItem(
                            text = {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            },
                            onClick = {
                                onDeleteClick()
                                menuExapanded = false
                            }
                        )
                    }

                    if (onShareClick != null) {
                        DropdownMenuItem(
                            text = {
                                Text("Share", color = MaterialTheme.colorScheme.error)
                            },
                            onClick = {
                                onShareClick()
                                menuExapanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CardPreview() {
    SingleRecordCard(
        name = "Name",
        amount = 13.00,
        onDeleteClick = {}
    )
}


//@Preview(showBackground = true)
//@Composable
//fun AppPreview() {
//    MyScreen()
//}


//@Preview(showBackground = true)
//@Composable
//fun BarPreview() {
//    AppBar(true) {}
//}
