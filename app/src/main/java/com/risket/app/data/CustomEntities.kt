package com.risket.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_columns")
data class CustomColumnEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tableId: Long,
    val name: String,
    val position: Int
)

@Entity(tableName = "custom_cells")
data class CustomCellEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tableId: Long,
    val rowIndex: Int,
    val columnId: Long,
    val value: String = ""
)
