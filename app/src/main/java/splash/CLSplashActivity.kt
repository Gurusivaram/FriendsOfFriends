package splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import authentication.CLAuthenticationActivity
import com.example.contactlistapp.databinding.ClActivitySplashBinding
import contactList.CLContactListActivity

class CLSplashActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_SHOW_TIME = 3000.toLong()
    }

    private lateinit var splashBinding: ClActivitySplashBinding
    private lateinit var splashViewModel: CLSplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ClActivitySplashBinding.inflate(layoutInflater)
        splashViewModel = ViewModelProvider(this).get(CLSplashViewModel::class.java)
        setContentView(splashBinding.root)
        initUI()
        initObservers()
        if (savedInstanceState == null) {
            Handler().postDelayed({
                splashViewModel.isUserAlreadyLoggedIn()
            }, SPLASH_SHOW_TIME)
        }
    }

    private fun initUI() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun initObservers() {
        splashViewModel.user.observe(this, {
            if (it != null) {
                Intent(this, CLContactListActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            } else {
                Intent(this, CLAuthenticationActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        })
    }
}