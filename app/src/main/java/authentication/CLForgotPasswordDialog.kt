package authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import authentication.viewModel.CLForgotPasswordViewModel
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClFragmentForgotPasswordBinding

class CLForgotPasswordDialog : CLDialogFragment() {
    companion object {
        fun newInstance() = CLForgotPasswordDialog()
    }

    private lateinit var forgotPasswordBinding: ClFragmentForgotPasswordBinding
    private lateinit var forgotPasswordViewModel: CLForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordViewModel = ViewModelProvider(this).get(CLForgotPasswordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        forgotPasswordBinding = ClFragmentForgotPasswordBinding.inflate(layoutInflater)
        return forgotPasswordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initTextWatchers()
        initClickListeners()
    }

    private fun initObservers() {
        with(forgotPasswordViewModel) {
            isValidEmailErrorState.observe(viewLifecycleOwner, {
                if (it != 0) {
                    forgotPasswordBinding.tieOtp.error = getString(it)
                }
            })
            isOtpReceived.observe(viewLifecycleOwner, {
                if (it) {
                    authenticationActivityContext.supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<CLPasswordResetFragment>(R.id.root_fragment_container_auth)
                        addToBackStack(null)
                    }
                    dismiss()
                    alerts.showAlertDialog(getString(R.string.alert_check_mail_for_otp))
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

    private fun initTextWatchers() {
        forgotPasswordBinding.tieOtp.doAfterTextChanged {
            forgotPasswordViewModel.email = it.toString()
        }
    }

    private fun initClickListeners() {
        with(forgotPasswordBinding) {
            tvCancel.setOnClickListener {
                dismiss()
            }
            tvOk.setOnClickListener {
                forgotPasswordViewModel.getOtpPassword()
            }
        }
    }
}