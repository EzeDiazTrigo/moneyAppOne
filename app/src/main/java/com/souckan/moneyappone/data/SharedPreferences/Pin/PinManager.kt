package com.souckan.moneyappone.data.SharedPreferences.Pin

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PinManager(context: Context) {

    private val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "PinPrefs",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun savePin(pin: String) {
        sharedPreferences.edit().putString("USER_PIN", pin).apply()
    }

    fun getPin(): String? {
        return sharedPreferences.getString("USER_PIN", null)
    }

    fun clearPin() {
        sharedPreferences.edit().remove("USER_PIN").apply()
    }

    //Borra la autenticación cuando la app se cierre completamente
    fun clearAuthentication() {
        sharedPreferences.edit().remove("AUTH_STATUS").apply()
    }

    //Guarda que el usuario ingresó correctamente
    fun setUserAuthenticated(authenticated: Boolean) {

        sharedPreferences.edit().putBoolean("is_authenticated", authenticated).apply()
    }
    //Verifica si ya está autenticado.
    fun isUserAuthenticated(): Boolean {
        return sharedPreferences.getBoolean("is_authenticated", false)
    }
    fun saveSecurityQuestion(question: String, answer: String) {
        sharedPreferences.edit()
            .putString("SECURITY_QUESTION", question)
            .putString("SECURITY_ANSWER", answer.lowercase())
            .apply()
    }

    fun getSecurityQuestion(): String? {
        return sharedPreferences.getString("SECURITY_QUESTION", null)
    }

    fun getSecurityAnswer(): String? {
        return sharedPreferences.getString("SECURITY_ANSWER", null)
    }

    fun isSecurityAnswerCorrect(answer: String): Boolean {
        val savedAnswer = sharedPreferences.getString("SECURITY_ANSWER", null)
        return savedAnswer != null && savedAnswer == answer.lowercase()
    }


}
