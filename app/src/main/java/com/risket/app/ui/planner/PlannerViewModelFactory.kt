package com.risket.app.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.risket.app.data.RisketRepository

class PlannerViewModelFactory(
    private val repository: RisketRepository,
    private val apiKey: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlannerViewModel(repository, apiKey) as T
    }
}
