package authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClActivityAuthenticationBinding
import androidx.fragment.app.add
import androidx.fragment.app.commit

class CLAuthenticationActivity : AppCompatActivity() {
    private lateinit var authenticationBinding: ClActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationBinding = ClActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(authenticationBinding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CLLoginFragment>(R.id.root_fragment_container_auth)
                addToBackStack(null)
            }
        }
    }
}