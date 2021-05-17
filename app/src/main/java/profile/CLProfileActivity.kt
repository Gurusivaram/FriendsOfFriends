package profile

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClActivityProfileBinding
import common.CLActivity
import common.CLInternetMonitor
import profile.viewModel.CLProfileViewModel

class CLProfileActivity : CLActivity() {
    companion object {
        const val USER_INTENT_KEY = "user_intent"
        const val LOGGED_USER_INTENT_KEY = "logged_user_intent"
    }

    private lateinit var profileBinding: ClActivityProfileBinding
    private lateinit var profileViewModel: CLProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ClActivityProfileBinding.inflate(layoutInflater)
        profileViewModel = ViewModelProvider(this).get(CLProfileViewModel::class.java)
        setContentView(profileBinding.root)
        CLInternetMonitor.connectivityReceiverListener = this
        registerReceiver(internetMonitor, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        if (savedInstanceState == null) {
            initData()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CLProfileViewFragment>(R.id.root_fragment_container_profile)
                addToBackStack(null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetMonitor)
    }

    private fun initData() {
        with(profileViewModel) {
            intent.apply {
                getStringExtra(USER_INTENT_KEY)?.let {
                    email = it
                }
                getStringExtra(LOGGED_USER_INTENT_KEY)?.let {
                    loggedUserEmail = it
                }
            }
        }
    }

    fun getProfileViewModel(): CLProfileViewModel = profileViewModel
}