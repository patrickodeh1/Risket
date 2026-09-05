package com.risket.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

const val TYPE_AV = "AV"
const val TYPE_NOTE = "NOTE"
const val TYPE_CUSTOM = "CUSTOM"
const val TYPE_TODO = "TODO"

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val initialBalance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val isComplete: Boolean = false,
    val noteContent: String = ""
)
