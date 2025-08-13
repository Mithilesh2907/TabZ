package com.example.tabz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tabz.data.AppDAO
import com.example.tabz.data.TabItem
import com.example.tabz.data.TransactionItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TabsViewModel(private val dao: AppDAO) : ViewModel(){

    @OptIn(ExperimentalCoroutinesApi::class)
    val tabsWithTotals: StateFlow<Map<TabItem,Double>> =
        dao.getAllTabs().flatMapLatest { tabs ->
            if (tabs.isEmpty()){
                flowOf(emptyMap())
            }else{
                val totalsFlows: List<Flow<Double?>> = tabs.map { tab ->
                    dao.getTotalForTab(tab.id)
                }
                combine(totalsFlows) { totalsArray ->
                    tabs.zip(totalsArray.map { it?: 0.0 })
                        .toMap()
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )


    fun addTab(name: String) {
        viewModelScope.launch {
            dao.insertTab(TabItem(name = name))
        }
    }

    fun deleteTab(tab: TabItem) {
        viewModelScope.launch {
            dao.deleteTab(tab)
        }
    }
}

class TransactionViewModel(private val dao: AppDAO, private val tabID: Int) : ViewModel() {
    val transactions : StateFlow<List<TransactionItem>> =
        dao.getTransactionsForTab(tabID)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addTransaction(description: String, amount: Double) {
        viewModelScope.launch {
            val transaction = TransactionItem(
                tabOwnerId = tabID,
                description = description,
                amount = amount
            )
            dao.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionItem) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
        }
    }
}



class TransactionViewModelFactory(private val dao: AppDAO, private val tabID: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("Unchecked cast")
            return TransactionViewModel(dao, tabID) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}