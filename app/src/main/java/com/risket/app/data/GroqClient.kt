package com.risket.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

data class GroqMessage(val role: String, val content: String)

object GroqClient {
    private const val ENDPOINT = "https://api.groq.com/openai/v1/chat/completions"
    private const val MODEL = "llama-3.3-70b-versatile"

    private val client = OkHttpClient()

    suspend fun chat(apiKey: String, systemPrompt: String, messages: List<GroqMessage>): String =
        withContext(Dispatchers.IO) {
            val messagesArray = JSONArray()
            messagesArray.put(JSONObject().put("role", "system").put("content", systemPrompt))
            messages.forEach {
                messagesArray.put(JSONObject().put("role", it.role).put("content", it.content))
            }

            val body = JSONObject()
                .put("model", MODEL)
                .put("messages", messagesArray)
                .put("response_format", JSONObject().put("type", "json_object"))
                .put("temperature", 0.4)

            val request = Request.Builder()
                .url(ENDPOINT)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                    ?: throw Exception("Empty response from Groq")
                if (!response.isSuccessful) {
                    throw Exception("Groq error ${response.code}: $responseBody")
                }
                val outer = JSONObject(responseBody)
                outer.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
            }
        }
}
