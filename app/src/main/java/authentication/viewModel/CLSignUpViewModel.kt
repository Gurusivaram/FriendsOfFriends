package authentication.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactlistapp.R
import common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers

class CLSignUpViewModel(application: Application) : CLViewModel(application) {
    private val context = application
    var firstName = ""
    var lastName = ""
    var company = ""
    var email = ""
    var phone = ""
    var password = ""
    var passwordConfirm = ""
    var addressStreet1 = ""
    var addressStreet2 = ""
    var addressCity = ""
    var addressState = ""
    var addressPostalCode = ""
    val userNameErrorState by lazy {
        MutableLiveData<Int>()
    }
    val passwordErrorState by lazy {
        MutableLiveData<Int>()
    }
    val phoneErrorState by lazy {
        MutableLiveData<Int>()
    }
    val emailErrorState by lazy {
        MutableLiveData<Int>()
    }
    val passwordConfirmErrorState by lazy {
        MutableLiveData<Int>()
    }
    val emptyInputErrorState by lazy {
        MutableLiveData<Int>()
    }
    val isRegisterSuccess by lazy {
        MutableLiveData<Boolean>()
    }

    private fun validateInputs(): Boolean {
        when {
            checkIfEmptyField() -> {
                emptyInputErrorState.value = R.string.alert_empty_fields
                return false
            }
            !CLValidation.isEmailValid(this.email) -> {
                userNameErrorState.value = R.string.alert_email_is_invalid
                return false
            }
            phone.length < 10 || phone.length > 10 -> {
                phoneErrorState.value = R.string.alert_invalid_phone
                return false
            }
            !CLValidation.isPasswordValid(this.password) -> {
                passwordErrorState.value = R.string.alert_password
                return false
            }
            password != passwordConfirm -> {
                passwordConfirmErrorState.value = R.string.alert_password_mismatch
                return false
            }
        }
        //When any one of these below error is true, we will not allow signup, but after clearing call errors
        //we wil change those values to 0 to notify that no errors.
        userNameErrorState.value = 0
        phoneErrorState.value = 0
        passwordErrorState.value = 0
        passwordConfirmErrorState.value = 0
        return true
    }

    private fun checkIfEmptyField(): Boolean {
        if (firstName.isEmpty() ||
            lastName.isEmpty() ||
            company.isEmpty() ||
            email.isEmpty() ||
            phone.isEmpty() ||
            password.isEmpty() ||
            passwordConfirm.isEmpty() ||
            firstName.isEmpty() ||
            addressStreet1.isEmpty() ||
            addressStreet2.isEmpty() ||
            addressCity.isEmpty() ||
            addressState.isEmpty() ||
            addressPostalCode.isEmpty()
        ) return true
        emptyInputErrorState.value = 0
        return false
    }

    fun registerNewUser() {
        val newUser = createUserObject()
        if (validateInputs()) {
            viewModelScope.launch {
                val result = CLRepository.signUp(newUser, password)
                launch(Dispatchers.Main) {
                    when {
                        result?.contains(CLConstants.SUCCESS) == true -> {
                            isRegisterSuccess.value = true
                            CLRepository.savePassword(password)
                        }
                        result?.contains(CLErrors.PHONE_ALREADY_TAKEN) == true -> {
                            isErrorAPI.value = context.getString(R.string.alert_phone_already_taken)
                        }
                        result?.contains(CLErrors.MAIL_ALREADY_TAKEN) == true -> {
                            isErrorAPI.value = context.getString(R.string.alert_email_already_taken)
                        }
                        result?.contains(CLErrors.NETWORK_CONNECTION_ERROR) == true -> {
                            isErrorAPI.value = CLErrors.NETWORK_CONNECTION_ERROR
                        }
                        result?.contains(CLErrors.REQUEST_TIMEOUT) == true -> {
                            isErrorAPI.value = CLErrors.REQUEST_TIMEOUT
                        }
                        result?.contains(CLErrors.CANT_BE_BLANK) == true -> isErrorAPI.value =
                            context.getString(R.string.alert_empty_fields)
                        else -> isErrorException.value = CLErrors.SOMETHING_WENT_WRONG
                    }
                }
            }
        }
    }

    private fun createUserObject(): CLUsers {
        val firstName = CLUtil.getCapitalized(firstName)
        val lastName = CLUtil.getCapitalized(lastName)
        return CLUsers(
            null,
            email,
            null,
            null,
            firstName,
            lastName,
            null,
            phone,
            null,
            null,
            null,
            "$addressStreet1, $addressStreet2, $addressCity, $addressState - $addressPostalCode",
            null
        )
    }
}