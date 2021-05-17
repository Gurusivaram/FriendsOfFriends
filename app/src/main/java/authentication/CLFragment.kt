package authentication

import android.content.Context
import androidx.fragment.app.Fragment
import common.CLAlerts

abstract class CLFragment : Fragment() {
    protected lateinit var authenticationActivityContext: CLAuthenticationActivity
    protected lateinit var alerts: CLAlerts

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authenticationActivityContext = context as CLAuthenticationActivity
        alerts = CLAlerts(authenticationActivityContext)
    }
}