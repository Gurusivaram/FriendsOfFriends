package authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.contactlistapp.databinding.ClFragmentSignUpBinding
import authentication.viewModel.CLSignUpViewModel
import com.example.contactlistapp.R

class CLSignUpFragment : CLFragment() {
    companion object {
        const val OTP_DIALOG_FRAGMENT_TAG = "OTP"
    }

    private lateinit var signUpBinding: ClFragmentSignUpBinding
    private lateinit var signUpViewModel: CLSignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpViewModel = ViewModelProvider(this).get(CLSignUpViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signUpBinding = ClFragmentSignUpBinding.inflate(layoutInflater)
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initTextWatchers()
        initClickListeners()
    }

    private fun initObservers() {
        with(signUpViewModel) {
            with(signUpBinding) {
                emptyInputErrorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it != 0) {
                            alerts.showAlertDialog(getString(it))
                        }
                    }
                })
                phoneErrorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it != 0) {
                            tiePhone.error = getString(it)
                        }
                    }
                })
                emailErrorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it != 0) {
                            tieEmail.error = getString(it)
                        }
                    }
                })
                userNameErrorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it != 0) {
                            tieEmail.error = getString(it)
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
                passwordConfirmErrorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it != 0) {
                            tieConfirmPassword.error = getString(it)
                        }
                    }
                })
                isRegisterSuccess.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it) {
                            fragmentManager?.let { it1 ->
                                CLOTPFragment.newInstance().show(it1, OTP_DIALOG_FRAGMENT_TAG)
                            }
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
        with(signUpBinding) {
            with(signUpViewModel) {
                tieFirstName.doAfterTextChanged {
                    firstName = it.toString()
                }
                tieLastName.doAfterTextChanged {
                    lastName = it.toString()
                }
                tieCompany.doAfterTextChanged {
                    company = it.toString()
                }
                tieEmail.doAfterTextChanged {
                    email = it.toString()
                }
                tiePhone.doAfterTextChanged {
                    phone = it.toString()
                }
                tiePassword.doAfterTextChanged {
                    password = it.toString()
                }
                tieConfirmPassword.doAfterTextChanged {
                    passwordConfirm = it.toString()
                }
                tieAddressStreet1.doAfterTextChanged {
                    addressStreet1 = it.toString()
                }
                tieAddressStreet2.doAfterTextChanged {
                    addressStreet2 = it.toString()
                }
                tieAddressCity.doAfterTextChanged {
                    addressCity = it.toString()
                }
                tieAddressState.doAfterTextChanged {
                    addressState = it.toString()
                }
                tieAddressPostalCode.doAfterTextChanged {
                    addressPostalCode = it.toString()
                }
            }
        }
    }

    private fun initClickListeners() {
        with(signUpBinding) {
            tvCancel.setOnClickListener {
                authenticationActivityContext.supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CLLoginFragment>(R.id.root_fragment_container_auth)
                    addToBackStack(null)
                }
            }
            tvSave.setOnClickListener {
                signUpViewModel.registerNewUser()
            }
        }
    }
}