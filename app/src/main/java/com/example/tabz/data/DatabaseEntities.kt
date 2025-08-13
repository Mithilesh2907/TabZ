package com.example.tabz.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tabs")
data class TabItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)


@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = TabItem::class,
            parentColumns = ["id"],
            childColumns = ["tabOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tabOwnerId: Int,
    val description: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)