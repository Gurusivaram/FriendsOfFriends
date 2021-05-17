package authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.contactlistapp.R
import authentication.viewModel.CLPasswordResetViewModel
import com.example.contactlistapp.databinding.ClFragmentPasswordResetBinding

class CLPasswordResetFragment : CLFragment() {
    private lateinit var passwordResetBinding: ClFragmentPasswordResetBinding
    private lateinit var passwordResetViewModel: CLPasswordResetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passwordResetViewModel = ViewModelProvider(this).get(CLPasswordResetViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        passwordResetBinding = ClFragmentPasswordResetBinding.inflate(layoutInflater)
        return passwordResetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intiTextWatchers()
        intiObservers()
        setClickListeners()
    }

    private fun intiTextWatchers() {
        with(passwordResetViewModel) {
            with(passwordResetBinding) {
                tieCurrentPassword.doAfterTextChanged {
                    currentPassword = it.toString()
                }
                tieNewPassword.doAfterTextChanged {
                    newPassword = it.toString()
                }
                tieConfirmPassword.doAfterTextChanged {
                    confirmPassword = it.toString()
                }
            }
        }
    }

    private fun intiObservers() {
        with(passwordResetViewModel) {
            emptyInputErrorState.observe(viewLifecycleOwner, {
                it?.let {
                    if (it != 0) {
                        alerts.showAlertDialog(getString(it))
                    }
                }
            })
            passwordErrorState.observe(viewLifecycleOwner, {
                it?.let {
                    if (it != 0) {
                        passwordResetBinding.tieNewPassword.error = getString(it)
                    }
                }
            })
            passwordMismatchErrorState.observe(viewLifecycleOwner, {
                it?.let {
                    if (it != 0) {
                        passwordResetBinding.tieConfirmPassword.error = getString(it)
                    }
                }
            })
            isPasswordResetSuccess.observe(viewLifecycleOwner, {
                if (it) {
                    alerts.getAlertDialog().apply {
                        setMessage(getString(R.string.alert_password_reset_success))
                        setPositiveButton(
                            context.getString(R.string.okay)
                        ) { _, _ ->
                            navigateToLoginScreen()
                        }
                        val alert: AlertDialog = create()
                        alert.show()
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

    private fun setClickListeners() {
        with(passwordResetBinding) {
            tvBack.setOnClickListener {
                navigateToLoginScreen()
            }
            ivBackContactDetails.setOnClickListener {
                navigateToLoginScreen()
            }
            tvDone.setOnClickListener {
                passwordResetViewModel.resetPassword()
            }
        }
    }

    private fun navigateToLoginScreen() {
        authenticationActivityContext.supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CLLoginFragment>(R.id.root_fragment_container_auth)
            addToBackStack(null)
        }
    }
}