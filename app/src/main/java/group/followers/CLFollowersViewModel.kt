package group.followers

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import common.CLErrors
import common.CLViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers

class CLFollowersViewModel(application: Application) : CLViewModel(application) {
    val followers by lazy {
        MutableLiveData<MutableList<CLUsers>>()
    }
    val isUserFollowSuccess by lazy {
        MutableLiveData<String>()
    }
    val followRequestState by lazy {
        MutableLiveData<Boolean>()
    }

    fun getFollowersList(isRefreshed: Boolean = false) {
        if(!isRefreshed){
            isLoading.value = true
        }
        viewModelScope.launch {
            val (list, errors) = CLRepository.getFollowersList()
            launch(Dispatchers.Main) {
                when {
                    list != null -> followers.value = list
                    errors?.contains(CLErrors.SESSION_EXPIRED) == true -> {
                        isErrorAPI.value = CLErrors.SESSION_EXPIRED
                    }
                    else -> isErrorException.value = errors
                }
            }
            isLoading.value = false
        }
    }

    fun followUser(user: CLUsers) {
        followRequestState.value = true
        viewModelScope.launch {
            val result = user.id?.let { CLRepository.followUser(it) }
            launch(Dispatchers.Main) {
                when {
                    result?.contains(CLErrors.REQUEST_SENT) == true -> {
                        isUserFollowSuccess.value = CLErrors.REQUEST_SENT
                    }
                    result?.contains(CLErrors.REQUEST_ALREADY_SENT) == true -> {
                        isErrorAPI.value = CLErrors.REQUEST_ALREADY_SENT
                    }
                    result?.contains(CLErrors.SESSION_EXPIRED_ERROR) == true -> {
                        isErrorAPI.value = CLErrors.SESSION_EXPIRED
                    }
                    result?.contains(CLErrors.INVALID_USER_ERROR) == true -> {
                        isErrorAPI.value = CLErrors.INVALID_USER_ERROR
                    }
                    else -> isErrorException.value = CLErrors.SOMETHING_WENT_WRONG
                }
                followRequestState.value = false
            }
        }
    }
}