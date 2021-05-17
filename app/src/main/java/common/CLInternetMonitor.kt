package common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class CLInternetMonitor : BroadcastReceiver() {
    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (connectivityReceiverListener != null && intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            context?.let {
                isConnectedOrConnecting(it)
            }?.let {
                connectivityReceiverListener?.onNetworkConnectionChanged(it)
            }
        }
    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }
}