package com.risket.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureKeyStore {
    private const val PREFS_NAME = "risket_secure_prefs"
    private const val KEY_GROQ = "groq_api_key"

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getGroqKey(context: Context): String? = prefs(context).getString(KEY_GROQ, null)

    fun setGroqKey(context: Context, key: String) {
        prefs(context).edit().putString(KEY_GROQ, key).apply()
    }
}
