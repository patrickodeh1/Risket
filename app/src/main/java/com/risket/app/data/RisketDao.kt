package com.risket.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RisketDao {

    @Query("SELECT * FROM tables ORDER BY createdAt DESC")
    fun getAllTables(): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE id = :tableId")
    fun getTable(tableId: Long): Flow<TableEntity?>

    @Insert
    suspend fun insertTable(table: TableEntity): Long

    @Update
    suspend fun updateTable(table: TableEntity)

    @Delete
    suspend fun deleteTable(table: TableEntity)

    @Query("DELETE FROM rows WHERE tableId = :tableId")
    suspend fun deleteRowsForTable(tableId: Long)

    @Query("DELETE FROM custom_columns WHERE tableId = :tableId")
    suspend fun deleteColumnsForTable(tableId: Long)

    @Query("DELETE FROM custom_cells WHERE tableId = :tableId")
    suspend fun deleteCellsForTable(tableId: Long)

    @Insert
    suspend fun insertRows(rows: List<RowEntity>)

    @Query("SELECT * FROM rows WHERE tableId = :tableId ORDER BY serialNumber ASC")
    fun getRowsForTable(tableId: Long): Flow<List<RowEntity>>

    @Update
    suspend fun updateRow(row: RowEntity)

    @Insert
    suspend fun insertColumns(columns: List<CustomColumnEntity>)

    @Query("SELECT * FROM custom_columns WHERE tableId = :tableId ORDER BY position ASC")
    fun getColumnsForTable(tableId: Long): Flow<List<CustomColumnEntity>>

    @Insert
    suspend fun insertCells(cells: List<CustomCellEntity>)

    @Update
    suspend fun updateCell(cell: CustomCellEntity)

    @Query("SELECT * FROM custom_cells WHERE tableId = :tableId")
    fun getCellsForTable(tableId: Long): Flow<List<CustomCellEntity>>

    // Todo items DAO
    @Query("SELECT * FROM todo_items WHERE tableId = :tableId ORDER BY position ASC")
    fun getTodoItemsForTable(tableId: Long): Flow<List<TodoItemEntity>>

    // helper to read sync list when needed
    @Query("SELECT * FROM todo_items WHERE tableId = :tableId ORDER BY position ASC")
    suspend fun getTodoItems(tableId: Long): List<TodoItemEntity>

    @Insert
    suspend fun insertTodoItem(item: TodoItemEntity): Long

    @Update
    suspend fun updateTodoItem(item: TodoItemEntity)

    @Delete
    suspend fun deleteTodoItem(item: TodoItemEntity)

    @Query("DELETE FROM todo_items WHERE tableId = :tableId")
    suspend fun deleteTodoItemsForTable(tableId: Long)

    // Goals DAO
    @Query("SELECT * FROM goals WHERE isActive = 1 ORDER BY createdAt ASC")
    fun getActiveGoals(): Flow<List<GoalEntity>>

    @Insert
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)
}
