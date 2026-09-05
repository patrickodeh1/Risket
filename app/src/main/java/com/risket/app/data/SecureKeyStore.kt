package com.risket.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureKeyStore {
    private const val PREFS_NAME = "risket_secure_prefs"
    private const val KEY_GROQ_API = "groq_api_key"
    private const val KEY_GROQ_MODEL = "groq_model"

    // Groq periodically renames or deprecates models per-account. If the assistant starts
    // failing with a "model not found" error, change the model in Settings, no rebuild needed.
    const val DEFAULT_MODEL = "openai/gpt-oss-120b"

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getGroqKey(context: Context): String? = prefs(context).getString(KEY_GROQ_API, null)

    fun setGroqKey(context: Context, key: String) {
        prefs(context).edit().putString(KEY_GROQ_API, key).apply()
    }

    fun getModel(context: Context): String =
        prefs(context).getString(KEY_GROQ_MODEL, null)?.takeIf { it.isNotBlank() } ?: DEFAULT_MODEL

    fun setModel(context: Context, model: String) {
        prefs(context).edit().putString(KEY_GROQ_MODEL, model).apply()
    }
}
