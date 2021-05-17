package authentication.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import common.CLConstants
import common.CLViewModel
import common.CLErrors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository

class CLOTPViewModel(application: Application) : CLViewModel(application) {
    var otp = ""
    val isOTPVerified by lazy {
        MutableLiveData<Boolean>()
    }

    fun verifyOtp() {
        viewModelScope.launch {
            val result = CLRepository.isOTPVerified(otp)
            launch(Dispatchers.Main) {
                when {
                    result?.contains(CLConstants.SUCCESS) == true -> {
                        isOTPVerified.value = true
                    }
                    result?.contains(CLErrors.OTP_VERIFICATION_FILED) == true -> {
                        isErrorAPI.value = CLErrors.OTP_VERIFICATION_FILED
                    }
                    else -> isErrorException.value = CLErrors.SOMETHING_WENT_WRONG
                }
            }
        }
    }
}