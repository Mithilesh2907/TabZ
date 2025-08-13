package com.example.tabz.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tabz.data.TabItem
import com.example.tabz.viewmodel.TabsViewModel

@Composable
fun TabsScreen(
    tabsViewModel: TabsViewModel,
    onTabClick: (Int) -> Unit
) {
    val context = LocalContext.current

    val tabsWithTotals by tabsViewModel.tabsWithTotals.collectAsStateWithLifecycle()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var newTabName by rememberSaveable { mutableStateOf("") }
    var tabToDelete by rememberSaveable { mutableStateOf<TabItem?>(null) }

    Scaffold(
        topBar = {
            AppBar("Tabs",false) {}
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add,"Add Tab")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            items(tabsWithTotals.entries.toList()) { (tab,total) ->
                Box(
                    modifier = Modifier
                        .clickable { onTabClick(tab.id) }
                ) {
                    SingleRecordCard(
                        name = tab.name,
                        amount = total,
                        onDeleteClick = {
                            tabToDelete = tab
                        }
                    )
                }
            }
        }

        if (tabToDelete != null) {
            AlertDialog(
                onDismissRequest = { tabToDelete = null },
                title = {
                    Text("Delete tab ?")
                },
                text = {
                    Text("Are you sure you want to delete tab '${tabToDelete!!.name}'? All of the transactions in it will be deleted permanently!!")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            tabsViewModel.deleteTab(tabToDelete!!)
                            tabToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            tabToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add new tab") },
                text = {
                    TextField(
                        value = newTabName,
                        onValueChange = { newTabName = it }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                             if (newTabName.isNotBlank()) {
                                 tabsViewModel.addTab(newTabName)
                                 Toast.makeText(context,"New tab added",Toast.LENGTH_SHORT).show()
                                 showDialog = false
                                 newTabName = ""
                             }
                        }
                    ) { Text("Add") }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDialog = false }
                    ) { Text("Cancel") }
                }
            )
        }
    }
}