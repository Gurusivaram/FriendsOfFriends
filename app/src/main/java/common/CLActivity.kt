package common

import androidx.appcompat.app.AppCompatActivity
import com.example.contactlistapp.R

abstract class CLActivity : AppCompatActivity(),
    CLInternetMonitor.ConnectivityReceiverListener {
    protected val internetMonitor by lazy {
        CLInternetMonitor()
    }
    protected val alerts by lazy {
        CLAlerts(this)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            alerts.showToast(getString(R.string.alert_network_success))
        } else {
            alerts.showToast(getString(R.string.alert_network_failed))
        }
    }
}