package com.risket.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rows")
data class RowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tableId: Long,
    val serialNumber: Int,
    val risk: Double,
    val balance: Double,
    val checked: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
