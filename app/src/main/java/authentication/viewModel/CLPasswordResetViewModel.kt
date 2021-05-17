package authentication.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactlistapp.R
import common.CLConstants
import common.CLViewModel
import common.CLValidation
import common.CLErrors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository

class CLPasswordResetViewModel(application: Application) : CLViewModel(application) {
    var email = ""
    var newPassword = ""
    var currentPassword = ""
    var confirmPassword = ""
    val isPasswordResetSuccess by lazy {
        MutableLiveData<Boolean>()
    }
    val emptyInputErrorState by lazy {
        MutableLiveData<Int>()
    }
    val passwordErrorState by lazy {
        MutableLiveData<Int>()
    }
    val passwordMismatchErrorState by lazy {
        MutableLiveData<Int>()
    }

    private fun checkIfEmptyField(): Boolean {
        if (currentPassword.isEmpty() ||
            newPassword.isEmpty() ||
            confirmPassword.isEmpty()
        ) return true
        emptyInputErrorState.value = 0
        return false
    }

    private fun validateInputs(): Boolean {
        when {
            checkIfEmptyField() -> {
                emptyInputErrorState.value = R.string.alert_empty_fields
                return false
            }
            !CLValidation.isPasswordValid(newPassword) -> {
                passwordErrorState.value = R.string.alert_password
                return false
            }
            newPassword != confirmPassword -> {
                passwordMismatchErrorState.value = R.string.alert_password_mismatch
                return false
            }
        }
        passwordErrorState.value = 0
        passwordMismatchErrorState.value = 0
        return true
    }

    fun resetPassword() {
        if (validateInputs()) {
            viewModelScope.launch {
                val result = CLRepository.resetPassword(
                    currentPassword,
                    newPassword,
                    confirmPassword
                )
                launch(Dispatchers.Main) {
                    when {
                        result?.contains(CLConstants.SUCCESS) == true -> {
                            isPasswordResetSuccess.value = true
                            CLRepository.savePassword(newPassword)
                        }
                        result?.contains(CLErrors.RESET_PASSWORD_TOKEN_INVALID) == true -> {
                            isErrorAPI.value = CLErrors.RESET_PASSWORD_TOKEN_INVALID
                        }
                        else -> isErrorException.value = result
                    }
                }
            }
        }
    }
}