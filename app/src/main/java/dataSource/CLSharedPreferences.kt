package dataSource

import android.content.Context
import retrofit.models.CLToken

class CLSharedPreferences(private val context: Context) {
    companion object {
        private const val PRIVATE_MODE = 0
        private const val SHARED_PREF_USER_KEY = "ContactListUsers"
        private const val TOKEN = "token"
        private const val REFRESH = "refresh"
        private const val PASSWORD = "password"
    }

    private val savedUserData by lazy {
        context.getSharedPreferences(
            SHARED_PREF_USER_KEY,
            PRIVATE_MODE
        )
    }

    private val sharedPrefEdit by lazy {
        savedUserData.edit()
    }

    fun saveAuthToken(token: String?) {
        sharedPrefEdit.apply {
            putString(TOKEN, token)
            apply()
        }
    }

    fun saveTokens(tokens: CLToken) {
        sharedPrefEdit.apply {
            putString(TOKEN, tokens.authToken)
            putString(REFRESH, tokens.refreshToken)
            apply()
        }
    }

    fun savePassword(password: String) {
        sharedPrefEdit.apply {
            putString(PASSWORD, password)
            apply()
        }
    }

    fun getAuthToken(): String = savedUserData.getString(TOKEN, null).toString()

    fun getRefreshToken(): String = savedUserData.getString(REFRESH, null).toString()

    fun getPassword(): String = savedUserData.getString(PASSWORD, null).toString()

    fun destroySharedPreferences() {
        sharedPrefEdit.clear().apply()
    }
}