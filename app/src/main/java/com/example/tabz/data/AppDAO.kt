package com.example.tabz.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface AppDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTab(tab: TabItem)

    @Query("SELECT * FROM tabs ORDER BY name ASC")
    fun getAllTabs(): Flow<List<TabItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: TransactionItem)

    @Query("SELECT * FROM transactions WHERE tabOwnerId = :tabId ORDER BY timestamp DESC")
    fun getTransactionsForTab(tabId: Int): Flow<List<TransactionItem>>

    @Query("SELECT SUM(amount) FROM transactions WHERE tabOwnerId = :tabId")
    fun getTotalForTab(tabId: Int): Flow<Double?>

    @Delete
    suspend fun deleteTab(tab: TabItem)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionItem)
}