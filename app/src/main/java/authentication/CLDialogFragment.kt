package authentication

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.contactlistapp.R
import common.CLAlerts

abstract class CLDialogFragment : DialogFragment() {
    companion object {
        private const val WINDOW_SIZE = 0.80
    }

    protected lateinit var authenticationActivityContext: CLAuthenticationActivity
    protected lateinit var alerts: CLAlerts

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authenticationActivityContext = context as CLAuthenticationActivity
        alerts = CLAlerts(authenticationActivityContext)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * WINDOW_SIZE).toInt()
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.bg_otp_dialog)
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}