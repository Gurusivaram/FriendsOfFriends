package authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import authentication.viewModel.CLLoginViewModel
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClFragmentLoginBinding
import contactList.CLContactListActivity

class CLLoginFragment : CLFragment() {
    companion object {
        const val FORGOT_PASSWORD_FRAGMENT_TAG = "forgotPassword"
    }

    private lateinit var loginBinding: ClFragmentLoginBinding
    private lateinit var loginViewModel: CLLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this).get(CLLoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginBinding = ClFragmentLoginBinding.inflate(layoutInflater)
        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initTextWatchers()
        initClickListeners()
    }

    private fun initObservers() {
        with(loginViewModel) {
            with(loginBinding) {
                userNameErrorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it != 0) {
                            tieUsername.error = getString(it)
                        }
                    }
                })
                passwordErrorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it != 0) {
                            tiePassword.error = getString(it)
                        }
                    }
                })
                isLoginSuccess.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it) {
                            navigateToContactListActivity()
                        }
                    }
                })
                isErrorException.observe(viewLifecycleOwner, {
                    it?.let {
                        alerts.showToast(it)
                    }
                })
                isErrorAPI.observe(viewLifecycleOwner, {
                    it?.let {
                        alerts.showToast(it)
                    }
                })
            }
        }
    }

    private fun initTextWatchers() {
        with(loginBinding) {
            tieUsername.doAfterTextChanged {
                loginViewModel.userName = it.toString()
            }
            tiePassword.doAfterTextChanged {
                loginViewModel.password = it.toString()
            }
        }
    }

    private fun initClickListeners() {
        with(loginBinding) {
            btnLogin.setOnClickListener {
                loginViewModel.loginUser()
            }
            tvForgotPassword.setOnClickListener {
                CLForgotPasswordDialog.newInstance().show(
                    authenticationActivityContext.supportFragmentManager,
                    FORGOT_PASSWORD_FRAGMENT_TAG
                )
            }
            tvSignup.setOnClickListener {
                navigateToSignUpActivity()
            }
        }
    }

    private fun navigateToContactListActivity() {
        Intent(authenticationActivityContext, CLContactListActivity::class.java).apply {
            startActivity(this)
            authenticationActivityContext.finish()
        }
    }

    private fun navigateToSignUpActivity() {
        authenticationActivityContext.supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CLSignUpFragment>(R.id.root_fragment_container_auth)
            addToBackStack(null)
        }
    }
}