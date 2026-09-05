package com.risket.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE todo_items ADD COLUMN createdDate TEXT NOT NULL DEFAULT ''")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                context TEXT NOT NULL,
                linkedTableId INTEGER NOT NULL,
                isActive INTEGER NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

@Database(
    entities = [
        TableEntity::class,
        RowEntity::class,
        CustomColumnEntity::class,
        CustomCellEntity::class,
        TodoItemEntity::class,
        GoalEntity::class
    ],
    version = 2,
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
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }

        fun closeInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}
