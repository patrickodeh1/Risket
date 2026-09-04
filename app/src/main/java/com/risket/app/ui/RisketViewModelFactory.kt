package com.risket.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.risket.app.data.RisketRepository

class RisketViewModelFactory(private val repository: RisketRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RisketViewModel(repository) as T
    }
}
