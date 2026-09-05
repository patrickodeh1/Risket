package com.risket.app.ui.planner

import com.risket.app.data.GoalEntity

data class ChatUiMessage(val fromUser: Boolean, val text: String)

data class TopUpUpdate(val goalId: Long, val newTasks: List<String>)

data class ConversationUpdate(
    val goalId: Long?,
    val title: String?,
    val contextAppend: String?,
    val newTasks: List<String>
)

data class ConversationResponse(
    val reply: String,
    val needsMoreInfo: Boolean,
    val goalUpdates: List<ConversationUpdate>
)
