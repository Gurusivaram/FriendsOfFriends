package common

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dataSource.CLSharedPreferences
import dataSource.CLUserDatabase

class CLApplication : Application() {
    companion object {
        private lateinit var instance: CLApplication

        fun getDB(): CLUserDatabase = CLUserDatabase.getDatabase(instance)

        fun getPref(): CLSharedPreferences = CLSharedPreferences(instance)

        fun isNetworkConnected(): Boolean {
            val connectivityManager =
                instance.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                return networkCapabilities != null &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
