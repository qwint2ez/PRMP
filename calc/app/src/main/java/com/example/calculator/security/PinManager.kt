package com.example.calculator.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.MessageDigest

object PinManager {
    private const val PREFS_FILENAME = "secure_pin_prefs"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_IS_PIN_SET = "is_pin_set"

    private fun getEncryptedPrefs(context: Context): EncryptedSharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREFS_FILENAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    private fun hashPin(pin: String): String {
        val bytes = pin.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun isPinSet(context: Context): Boolean {
        val prefs = getEncryptedPrefs(context)
        return prefs.getBoolean(KEY_IS_PIN_SET, false)
    }

    fun savePin(context: Context, newPin: String) {
        val prefs = getEncryptedPrefs(context)
        val pinHash = hashPin(newPin)
        prefs.edit().putString(KEY_PIN_HASH, pinHash).putBoolean(KEY_IS_PIN_SET, true).apply()
    }

    fun validatePin(context: Context, enteredPin: String): Boolean {
        if (!isPinSet(context)) return false
        val prefs = getEncryptedPrefs(context)
        val storedHash = prefs.getString(KEY_PIN_HASH, "")
        val enteredHash = hashPin(enteredPin)
        return storedHash == enteredHash
    }

    fun clearPin(context: Context) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().remove(KEY_PIN_HASH).remove(KEY_IS_PIN_SET).apply()
    }
}