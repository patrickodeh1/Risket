package com.risket.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.risket.app.data.CustomCellEntity
import com.risket.app.data.CustomColumnEntity
import com.risket.app.data.RisketRepository
import com.risket.app.data.RowEntity
import com.risket.app.data.TableEntity
import kotlinx.coroutines.launch

class RisketViewModel(private val repository: RisketRepository) : ViewModel() {

    val tables = repository.getAllTables()

    fun tableFlow(id: Long) = repository.getTable(id)
    fun rowsFlow(id: Long) = repository.getRows(id)
    fun columnsFlow(id: Long) = repository.getColumns(id)
    fun cellsFlow(id: Long) = repository.getCells(id)

    fun createAvTable(name: String, initialBalance: Double, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createAvTable(name, initialBalance)
            onCreated(id)
        }
    }

    fun createNoteTable(name: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createNoteTable(name)
            onCreated(id)
        }
    }

    fun saveNote(table: TableEntity, content: String) {
        viewModelScope.launch {
            repository.updateNoteContent(table, content)
        }
    }

    fun createCustomTable(name: String, columnNames: List<String>, rowCount: Int, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createCustomTable(name, columnNames, rowCount)
            val createdColumns = repository.getColumnsOnce(id)
            repository.initCustomCells(id, createdColumns, rowCount)
            onCreated(id)
        }
    }

    fun initCustomCells(tableId: Long, columns: List<CustomColumnEntity>, rowCount: Int) {
        viewModelScope.launch {
            repository.initCustomCells(tableId, columns, rowCount)
        }
    }

    fun updateCell(cell: CustomCellEntity, newValue: String) {
        viewModelScope.launch {
            repository.updateCell(cell, newValue)
        }
    }

    fun toggleRow(row: RowEntity, allRows: List<RowEntity>, table: TableEntity) {
        viewModelScope.launch {
            repository.toggleRowChecked(row)
            val updated = allRows.map { if (it.id == row.id) it.copy(checked = !row.checked) else it }
            repository.markCompleteIfNeeded(table.id, updated, table)
        }
    }

    fun deleteTable(table: TableEntity) {
        viewModelScope.launch {
            repository.deleteTable(table)
        }
    }
}
