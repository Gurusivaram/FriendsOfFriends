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

class CLLoginViewModel(application: Application) : CLViewModel(application) {
    private val context = application
    var userName = ""
    var password = ""
    val userNameErrorState by lazy {
        MutableLiveData(0)
    }
    val passwordErrorState by lazy {
        MutableLiveData(0)
    }
    val isLoginSuccess by lazy {
        MutableLiveData<Boolean>()
    }

    private fun isInputsValid(): Boolean {
        when {
            !CLValidation.isEmailValid(this.userName) -> {
                userNameErrorState.value = R.string.alert_email_is_invalid
                return false
            }
            !CLValidation.isPasswordValid(this.password) -> {
                passwordErrorState.value = R.string.alert_password
                return false
            }
        }
        userNameErrorState.value = 0
        passwordErrorState.value = 0
        return true
    }

    fun loginUser() {
        if (isInputsValid()) {
            viewModelScope.launch {
                val result = CLRepository.login(userName, password)
                launch(Dispatchers.Main) {
                    when {
                        result?.contains(CLConstants.SUCCESS) == true -> {
                            isLoginSuccess.value = true
                            CLRepository.savePassword(password)
                        }
                        result?.contains(CLErrors.LOGIN_ERROR) == true -> {
                            isErrorAPI.value = context.getString(R.string.alert_invalid_login)
                        }
                        else -> isErrorException.value = result
                    }
                }
            }
        }
    }
}