package com.risket.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val context: String = "",
    val linkedTableId: Long,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
