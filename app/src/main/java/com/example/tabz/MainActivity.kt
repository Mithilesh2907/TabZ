package com.example.tabz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tabz.data.AppDatabase
import com.example.tabz.screens.MyScreen
import com.example.tabz.screens.TabsScreen
import com.example.tabz.screens.TransactionScreen
import com.example.tabz.ui.theme.TabZTheme
import com.example.tabz.viewmodel.TabsViewModel

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    private val tabsViewModel by viewModels<TabsViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TabsViewModel(database.appDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TabZTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                   val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "tabs_screen"
                    ) {
                        composable("tabs_screen") {
                            TabsScreen(
                                tabsViewModel = tabsViewModel,
                                onTabClick = { tabId ->
                                    navController.navigate("transaction_screen/$tabId")
                                }
                            )
                        }

                        composable(
                            route = "transaction_screen/{tabId}",
                            arguments = listOf(navArgument("tabId") { type = NavType.IntType})
                        ) { backStackEntry ->
                            val tabId = backStackEntry.arguments?.getInt("tabId") ?:0

                            val tabState by tabsViewModel.tabsWithTotals.collectAsState()
                            val tabName = tabState.keys.find { it.id == tabId }?.name ?: "Transactions"

                            TransactionScreen(
                                dao = database.appDao(),
                                tabId = tabId,
                                tabName = tabName,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TabZTheme {
        MyScreen()
    }
}