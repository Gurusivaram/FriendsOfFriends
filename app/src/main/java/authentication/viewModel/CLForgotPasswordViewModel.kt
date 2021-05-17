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

class CLForgotPasswordViewModel(application: Application) : CLViewModel(application) {
    var email = ""
    val isOtpReceived by lazy {
        MutableLiveData<Boolean>()
    }
    val isValidEmailErrorState by lazy {
        MutableLiveData<Int>()
    }

    private fun checkValidEmail(): Boolean {
        if (!CLValidation.isEmailValid(email)) {
            isValidEmailErrorState.value = R.string.alert_email_is_invalid
            return false
        }
        isValidEmailErrorState.value = 0
        return true
    }

    fun getOtpPassword() {
        if (checkValidEmail()) {
            viewModelScope.launch {
                val result = CLRepository.getOtpPassword(email)
                launch(Dispatchers.Main) {
                    when {
                        result?.contains(CLConstants.SUCCESS) == true -> {
                            isOtpReceived.value = true
                        }
                        result?.contains(CLErrors.EMAIL_NOT_FOUND_ERROR) == true -> {
                            isErrorAPI.value = CLErrors.EMAIL_NOT_FOUND_ERROR
                        }
                        else -> isErrorException.value = CLErrors.SOMETHING_WENT_WRONG
                    }
                }
            }
        }
    }
}