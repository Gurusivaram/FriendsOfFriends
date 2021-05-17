package profile.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import common.CLConstants
import common.CLViewModel
import common.CLErrors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers

class CLProfileViewViewModel(application: Application) : CLViewModel(application) {
    val user by lazy {
        MutableLiveData<CLUsers>()
    }
    val isLogoutSuccess by lazy {
        MutableLiveData<Boolean>()
    }

    fun getUserDetails(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = CLRepository.getUser(email)
            launch(Dispatchers.Main) {
                user.value = result
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            val result =
                user.value?.email?.let { CLRepository.logout(it, CLRepository.getPassword()) }
            launch(Dispatchers.Main) {
                when {
                    result?.contains(CLConstants.SUCCESS) == true || result?.contains(CLErrors.SESSION_EXPIRED_ERROR) == true -> {
                        isLogoutSuccess.value = true
                    }
                    else -> isErrorException.value = result
                }
            }
        }
    }
}