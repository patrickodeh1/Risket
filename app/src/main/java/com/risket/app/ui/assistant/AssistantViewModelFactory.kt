package com.risket.app.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.risket.app.data.RisketRepository

class AssistantViewModelFactory(
    private val repository: RisketRepository,
    private val apiKey: String,
    private val model: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AssistantViewModel(repository, apiKey, model) as T
    }
}
