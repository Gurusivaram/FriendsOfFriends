package profile

import android.content.Context
import androidx.fragment.app.Fragment
import common.CLAlerts

abstract class CLProfileFragment : Fragment() {
    protected lateinit var profileActivityContext: CLProfileActivity
    protected lateinit var alerts: CLAlerts

    override fun onAttach(context: Context) {
        super.onAttach(context)
        profileActivityContext = context as CLProfileActivity
        alerts = CLAlerts(profileActivityContext)
    }
}