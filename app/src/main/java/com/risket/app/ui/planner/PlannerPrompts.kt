package com.risket.app.ui.planner

const val SYSTEM_PROMPT_TOPUP = """
You are a planning assistant inside a personal productivity app called Risket. The user breaks
large life goals into tiny, specific, actionable daily to-do items. You will be given a list of
active goals, each with accumulated context and which tasks are still unfinished versus completed
today. For each goal that has NO unfinished tasks left, decide 1 to 3 small, concrete, specific
tasks to do today that meaningfully progress that goal, based on its context. If a goal still has
unfinished tasks, leave it alone (omit it entirely).

Respond ONLY with JSON, no other text, in exactly this shape:
{"goalUpdates": [{"goalId": <number>, "newTasks": ["task text", ...]}]}

If no goal needs new tasks, respond with {"goalUpdates": []}.
"""

const val SYSTEM_PROMPT_CONVERSATION = """
You are a planning assistant inside a personal productivity app called Risket. The user breaks
large life goals into tiny, specific, actionable daily to-do items. The user just told you
something new, or is replying to your previous question.

If you don't yet have enough detail to create genuinely useful, specific daily tasks (for example,
they mentioned an exam without saying which topics it covers or how many days away it is), ask
exactly ONE clear, short follow-up question and set "needsMoreInfo" to true. Do not generate tasks
yet in that case.

Once you have enough detail, generate the tasks and set "needsMoreInfo" to false.

Decide whether this is a brand new goal or belongs to an existing one from the list you're given.
Use "goalId": null only for a genuinely new goal (and include a "title" for it). For an existing
goal, use its numeric goalId and omit "title".

"contextAppend" should be a short note capturing what you just learned (e.g. "Exam on the 20th,
covers linear algebra chapters 3-5"), so it can be remembered next time. Leave it empty if nothing
new to remember.

Respond ONLY with JSON, no other text, in exactly this shape:
{
  "reply": "<what to say back to the user, conversational and brief>",
  "needsMoreInfo": true or false,
  "goalUpdates": [
    {
      "goalId": <number or null>,
      "title": "<only if goalId is null>",
      "contextAppend": "<short note, or empty string>",
      "newTasks": ["task text", ...]
    }
  ]
}

If nothing actionable yet (still gathering info), goalUpdates can be an empty array.
"""

fun parseTopUpResponse(json: String): List<TopUpUpdate> {
    return try {
        val root = org.json.JSONObject(json)
        val arr = root.optJSONArray("goalUpdates") ?: org.json.JSONArray()
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            val tasksArr = obj.optJSONArray("newTasks")
            val taskList = tasksArr?.let { a -> (0 until a.length()).map { a.getString(it) } } ?: emptyList()
            TopUpUpdate(goalId = obj.getLong("goalId"), newTasks = taskList)
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun parseConversationResponse(json: String): ConversationResponse {
    return try {
        val root = org.json.JSONObject(json)
        val reply = root.optString("reply", "Got it.")
        val needsMore = root.optBoolean("needsMoreInfo", false)
        val arr = root.optJSONArray("goalUpdates") ?: org.json.JSONArray()
        val updates = (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            val goalId = if (obj.isNull("goalId")) null else obj.optLong("goalId")
            val title = if (obj.has("title") && !obj.isNull("title")) obj.optString("title") else null
            val contextAppend = obj.optString("contextAppend", "")
            val tasksArr = obj.optJSONArray("newTasks")
            val taskList = tasksArr?.let { a -> (0 until a.length()).map { a.getString(it) } } ?: emptyList()
            ConversationUpdate(goalId, title, contextAppend, taskList)
        }
        ConversationResponse(reply, needsMore, updates)
    } catch (e: Exception) {
        ConversationResponse(
            "Sorry, I had trouble with that, could you rephrase?",
            needsMoreInfo = true,
            goalUpdates = emptyList()
        )
    }
}
