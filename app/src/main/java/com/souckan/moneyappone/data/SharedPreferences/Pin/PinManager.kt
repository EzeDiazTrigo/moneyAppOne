package com.souckan.moneyappone.data.SharedPreferences.Pin

import android.content.Context

class PinManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("PinPrefs", Context.MODE_PRIVATE)

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

}
