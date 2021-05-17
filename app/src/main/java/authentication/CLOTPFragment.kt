package authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import authentication.viewModel.CLOTPViewModel
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClFragmentOtpBinding

class CLOTPFragment : CLDialogFragment() {
    companion object {
        fun newInstance() = CLOTPFragment()
    }

    private lateinit var otpBinding: ClFragmentOtpBinding
    private lateinit var otpViewModel: CLOTPViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otpViewModel = ViewModelProvider(this).get(CLOTPViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        otpBinding = ClFragmentOtpBinding.inflate(layoutInflater)
        return otpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initTextWatchers()
        initClickListeners()
    }

    private fun initObserver() {
        with(otpViewModel) {
            isOTPVerified.observe(viewLifecycleOwner, {
                if (it) {
                    authenticationActivityContext.supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<CLLoginFragment>(R.id.root_fragment_container_auth)
                        addToBackStack(null)
                    }
                    dismiss()
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
        otpBinding.tieOtp.doAfterTextChanged {
            otpViewModel.otp = it.toString()
        }
    }

    private fun initClickListeners() {
        with(otpBinding) {
            tvCancel.setOnClickListener {
                dismiss()
            }
            tvOk.setOnClickListener {
                otpViewModel.verifyOtp()
            }
        }
    }
}