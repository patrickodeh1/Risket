package com.risket.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RisketRepository(private val dao: RisketDao) {

    suspend fun getColumnsOnce(tableId: Long): List<CustomColumnEntity> = dao.getColumnsForTable(tableId).first()

    fun getAllTables(): Flow<List<TableEntity>> = dao.getAllTables()

    fun getTable(id: Long): Flow<TableEntity?> = dao.getTable(id)

    fun getRows(tableId: Long): Flow<List<RowEntity>> = dao.getRowsForTable(tableId)

    fun getColumns(tableId: Long): Flow<List<CustomColumnEntity>> = dao.getColumnsForTable(tableId)

    fun getCells(tableId: Long): Flow<List<CustomCellEntity>> = dao.getCellsForTable(tableId)

    suspend fun createAvTable(name: String, initialBalance: Double): Long {
        val table = TableEntity(name = name, type = TYPE_AV, initialBalance = initialBalance)
        val tableId = dao.insertTable(table)

        val rows = mutableListOf<RowEntity>()
        var runningBalance = initialBalance
        for (serial in 1..100) {
            val risk = runningBalance * 0.05
            runningBalance += risk
            rows.add(
                RowEntity(
                    tableId = tableId,
                    serialNumber = serial,
                    risk = risk,
                    balance = runningBalance
                )
            )
        }
        dao.insertRows(rows)
        return tableId
    }

    suspend fun createNoteTable(name: String): Long {
        val table = TableEntity(name = name, type = TYPE_NOTE)
        return dao.insertTable(table)
    }

    suspend fun updateNoteContent(table: TableEntity, content: String) {
        dao.updateTable(table.copy(noteContent = content))
    }

    suspend fun createCustomTable(name: String, columnNames: List<String>, rowCount: Int): Long {
        val table = TableEntity(name = name, type = TYPE_CUSTOM)
        val tableId = dao.insertTable(table)

        val columns = columnNames.mapIndexed { index, colName ->
            CustomColumnEntity(tableId = tableId, name = colName, position = index)
        }
        dao.insertColumns(columns)

        return tableId
    }

    suspend fun initCustomCells(tableId: Long, columns: List<CustomColumnEntity>, rowCount: Int) {
        val cells = mutableListOf<CustomCellEntity>()
        for (r in 0 until rowCount) {
            for (col in columns) {
                cells.add(CustomCellEntity(tableId = tableId, rowIndex = r, columnId = col.id))
            }
        }
        dao.insertCells(cells)
    }

    suspend fun updateCell(cell: CustomCellEntity, newValue: String) {
        dao.updateCell(cell.copy(value = newValue))
    }

    suspend fun toggleRowChecked(row: RowEntity) {
        dao.updateRow(row.copy(checked = !row.checked, updatedAt = System.currentTimeMillis()))

        if (row.serialNumber == 100 && !row.checked) {
            // marking the last row as checked completes the table
        }
    }

    suspend fun markCompleteIfNeeded(tableId: Long, rows: List<RowEntity>, table: TableEntity) {
        val allChecked = rows.size == 100 && rows.all { it.checked }
        if (allChecked && !table.isComplete) {
            dao.updateTable(table.copy(isComplete = true))
        }
    }

    suspend fun deleteTable(table: TableEntity) {
        dao.deleteRowsForTable(table.id)
        dao.deleteColumnsForTable(table.id)
        dao.deleteCellsForTable(table.id)
        dao.deleteTable(table)
    }
}
