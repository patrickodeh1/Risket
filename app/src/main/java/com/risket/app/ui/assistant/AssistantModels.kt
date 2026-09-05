package com.risket.app.ui.assistant

import com.risket.app.data.GoalEntity

data class AssistantAction(
    val type: String, // "create_goal" | "add_tasks" | "update_context"
    val goalId: Long?,
    val title: String?,
    val contextAppend: String?,
    val tasks: List<String>
)

data class AssistantResponse(
    val reply: String,
    val actions: List<AssistantAction>
)
