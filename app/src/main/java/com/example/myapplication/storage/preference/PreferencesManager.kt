package com.example.myapplication.storage.preference

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(context: Context) {

    companion object {
        private const val PREFERENCE_NAME = "user_preferences"
        private const val KEY_PHONE_NUMBER = "phone_number"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun savePhone(phone: String) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phone).apply()
    }

    fun getPhone(): String? {
        return sharedPreferences.getString(KEY_PHONE_NUMBER, null)
    }

    fun clearPhone() {
        sharedPreferences.edit().remove(KEY_PHONE_NUMBER).apply()
    }
}