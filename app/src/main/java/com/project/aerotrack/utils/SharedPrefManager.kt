package com.project.aerotrack
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPrefManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREFS_NAME = "my_prefs"
        private const val KEY_USERNAME = "username"
        private const val FIREBASE_TOKEN="firebase_token"
    }

    // Save data
    fun saveToken(token: String) {
        editor.putString(KEY_USERNAME, token)
        editor.apply()  // Asynchronous commit
    }

    // Retrieve data
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun saveUserName(token: String){
        Log.d("messageSent",token)
        editor.putString(FIREBASE_TOKEN, token)
        editor.apply()
    }

    fun getUserName():String?{
        return sharedPreferences.getString(FIREBASE_TOKEN,null)
    }

    // Clear specific data
    fun clearUsername() {
        editor.remove(KEY_USERNAME)
        editor.apply()
    }

    // Clear all data
    fun clearAll() {
        editor.clear()
        editor.apply()
    }
}
