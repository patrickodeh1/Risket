package com.risket.app.ui.assistant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.risket.app.data.GoalEntity
import com.risket.app.data.GroqClient
import com.risket.app.data.GroqMessage
import com.risket.app.data.RisketRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AssistantViewModel(
    private val repository: RisketRepository,
    private val apiKey: String,
    private val model: String
) : ViewModel() {

    val messages = repository.getChatMessages()

    var isLoading by mutableStateOf(false)
        private set
    var errorText by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            val existing = repository.getChatMessages().first()
            if (existing.isEmpty()) {
                repository.addChatMessage(
                    "assistant",
                    "Anything on your mind? Ask me to help plan today, or just talk it through."
                )
            }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            repository.addChatMessage("user", userText)
            runTurn(SYSTEM_PROMPT_ASSISTANT, includeLatestUserText = true)
        }
    }

    fun requestDailyCheckIn() {
        viewModelScope.launch {
            runTurn(SYSTEM_PROMPT_DAILY_CHECKIN, includeLatestUserText = false)
        }
    }

    private suspend fun runTurn(systemPromptBase: String, includeLatestUserText: Boolean) {
        isLoading = true
        errorText = null
        try {
            val goals = repository.getActiveGoals().first()
            val systemPrompt = systemPromptBase + "\n\nActive goals:\n" + buildGoalsSummary(goals)

            val history = repository.getChatMessages().first().takeLast(20)
            val groqMessages = if (includeLatestUserText) {
                history.map { GroqMessage(if (it.role == "user") "user" else "assistant", it.content) }
            } else {
                // Silent check-in: just ask the model to act, no user turn to replay.
                listOf(GroqMessage("user", "Run today's check-in."))
            }

            val json = GroqClient.chat(apiKey, model, systemPrompt, groqMessages)
            val parsed = parseAssistantResponse(json)

            repository.addChatMessage("assistant", parsed.reply)

            parsed.actions.forEach { action ->
                when (action.type) {
                    "create_goal" -> {
                        if (!action.title.isNullOrBlank()) {
                            val newGoal = repository.createGoal(action.title, action.contextAppend ?: "")
                            if (action.tasks.isNotEmpty()) repository.addTasksToGoal(newGoal, action.tasks)
                        }
                    }
                    "add_tasks" -> {
                        val goal = action.goalId?.let { id -> goals.find { it.id == id } }
                        if (goal != null && action.tasks.isNotEmpty()) {
                            repository.addTasksToGoal(goal, action.tasks)
                        }
                    }
                    "update_context" -> {
                        val goal = action.goalId?.let { id -> goals.find { it.id == id } }
                        if (goal != null && !action.contextAppend.isNullOrBlank()) {
                            repository.appendGoalContext(goal, action.contextAppend)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            errorText = e.message ?: "Something went wrong reaching Groq."
            repository.addChatMessage("assistant", "Sorry, I couldn't reach Groq just now.")
        } finally {
            isLoading = false
        }
    }

    private suspend fun buildGoalsSummary(goals: List<GoalEntity>): String {
        if (goals.isEmpty()) return "No active goals yet."
        val today = repository.todayString()
        val sb = StringBuilder("Today's date: $today\n\n")
        goals.forEach { goal ->
            val items = repository.getTodoItems(goal.linkedTableId).first()
            val unfinished = items.filter { !it.checked }.map { it.text }
            val doneToday = items.filter { it.checked && it.createdDate == today }.map { it.text }
            sb.append("Goal #${goal.id}: ${goal.title}\n")
            sb.append("Context: ${goal.context}\n")
            sb.append("Still unfinished: ${if (unfinished.isEmpty()) "none" else unfinished.joinToString("; ")}\n")
            sb.append("Completed today: ${if (doneToday.isEmpty()) "none" else doneToday.joinToString("; ")}\n\n")
        }
        return sb.toString()
    }
}
