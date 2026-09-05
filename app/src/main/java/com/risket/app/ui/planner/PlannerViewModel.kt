package com.risket.app.ui.planner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

class PlannerViewModel(
    private val repository: RisketRepository,
    private val apiKey: String
) : ViewModel() {

    val messages = mutableStateListOf<ChatUiMessage>()
    var isLoading by mutableStateOf(false)
        private set
    var errorText by mutableStateOf<String?>(null)
        private set

    private val conversation = mutableListOf<GroqMessage>()

    init {
        messages.add(ChatUiMessage(fromUser = false, text = "Anything new today?"))
    }

    fun requestTopUp() {
        viewModelScope.launch {
            isLoading = true
            errorText = null
            try {
                val goals = repository.getActiveGoals().first()
                if (goals.isEmpty()) {
                    messages.add(ChatUiMessage(fromUser = false, text = "You don't have any goals yet, tell me about one."))
                    return@launch
                }
                val summary = buildGoalsSummary(goals)
                val json = GroqClient.chat(apiKey, SYSTEM_PROMPT_TOPUP, listOf(GroqMessage("user", summary)))
                val updates = parseTopUpResponse(json)

                var addedCount = 0
                updates.forEach { update ->
                    val goal = goals.find { it.id == update.goalId } ?: return@forEach
                    if (update.newTasks.isNotEmpty()) {
                        repository.addTasksToGoal(goal, update.newTasks)
                        addedCount += update.newTasks.size
                    }
                }
                messages.add(
                    ChatUiMessage(
                        fromUser = false,
                        text = if (addedCount > 0) "Added $addedCount new task(s) across your goals."
                        else "Everything's already covered for today."
                    )
                )
            } catch (e: Exception) {
                errorText = e.message ?: "Something went wrong reaching Groq."
            } finally {
                isLoading = false
            }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        messages.add(ChatUiMessage(fromUser = true, text = userText))
        conversation.add(GroqMessage("user", userText))

        viewModelScope.launch {
            isLoading = true
            errorText = null
            try {
                val goals = repository.getActiveGoals().first()
                val goalsContext = buildGoalsSummary(goals)
                val systemPrompt = SYSTEM_PROMPT_CONVERSATION + "\n\nActive goals:\n" + goalsContext
                val recentTurns = conversation.takeLast(8)

                val json = GroqClient.chat(apiKey, systemPrompt, recentTurns)
                conversation.add(GroqMessage("assistant", json))

                val parsed = parseConversationResponse(json)
                messages.add(ChatUiMessage(fromUser = false, text = parsed.reply))

                parsed.goalUpdates.forEach { update ->
                    if (update.goalId == null && !update.title.isNullOrBlank()) {
                        val newGoal = repository.createGoal(update.title, update.contextAppend ?: "")
                        if (update.newTasks.isNotEmpty()) repository.addTasksToGoal(newGoal, update.newTasks)
                    } else if (update.goalId != null) {
                        val goal = goals.find { it.id == update.goalId } ?: return@forEach
                        if (!update.contextAppend.isNullOrBlank()) repository.appendGoalContext(goal, update.contextAppend)
                        if (update.newTasks.isNotEmpty()) repository.addTasksToGoal(goal, update.newTasks)
                    }
                }
            } catch (e: Exception) {
                errorText = e.message ?: "Something went wrong reaching Groq."
                messages.add(ChatUiMessage(fromUser = false, text = "Sorry, I couldn't reach Groq just now."))
            } finally {
                isLoading = false
            }
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
