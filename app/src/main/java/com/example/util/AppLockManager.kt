package com.example.util

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object AppLockManager {
    private const val PREFS_NAME = "lifeos_security_prefs"
    private const val KEY_PIN_ENABLED = "key_pin_lock_enabled"
    private const val KEY_PIN_CODE = "key_pin_code"
    private const val DEFAULT_PIN = "1234"

    private var sessionUnlocked = false

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isPinEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_PIN_ENABLED, false)
    }

    fun setPinEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_PIN_ENABLED, enabled).apply()
        if (!enabled) {
            sessionUnlocked = true
        }
    }

    fun getPin(context: Context): String {
        return getPrefs(context).getString(KEY_PIN_CODE, DEFAULT_PIN) ?: DEFAULT_PIN
    }

    fun setPin(context: Context, newPin: String) {
        getPrefs(context).edit().putString(KEY_PIN_CODE, newPin).apply()
    }

    fun verifyPin(context: Context, inputPin: String): Boolean {
        val currentPin = getPin(context)
        val matches = inputPin == currentPin
        if (matches) {
            sessionUnlocked = true
        }
        return matches
    }

    fun isSessionUnlocked(context: Context): Boolean {
        if (!isPinEnabled(context)) return true
        return sessionUnlocked
    }

    fun lockSession() {
        sessionUnlocked = false
    }

    fun unlockSession() {
        sessionUnlocked = true
    }

    fun canAuthenticate(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        return canAuth == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // We only call onError so the UI can fallback to PIN
                    onError()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    unlockSession()
                    onSuccess()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock LifeOS")
            .setSubtitle("Authenticate to access your private data")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
