package com.risket.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TableEntity::class, RowEntity::class, CustomColumnEntity::class, CustomCellEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RisketDatabase : RoomDatabase() {
    abstract fun dao(): RisketDao

    companion object {
        @Volatile
        private var INSTANCE: RisketDatabase? = null

        fun getInstance(context: Context): RisketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RisketDatabase::class.java,
                    "risket.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
