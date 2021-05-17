package group.requests

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import common.CLConstants
import common.CLErrors
import common.CLViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers

class CLRequestsViewModel(application: Application) : CLViewModel(application) {
    val requests by lazy {
        MutableLiveData<MutableList<CLUsers>>()
    }
    val isRequestSuccess by lazy {
        MutableLiveData<Int>()
    }

    fun getFollowRequestsList(isRefreshed: Boolean = false) {
        if(!isRefreshed){
            isLoading.value = true
        }
        viewModelScope.launch {
            val (list, errors) = CLRepository.getFollowRequestsList()
            launch(Dispatchers.Main) {
                when {
                    list != null -> requests.value = list
                    errors?.contains(CLErrors.SESSION_EXPIRED) == true -> {
                        isErrorAPI.value = CLErrors.SESSION_EXPIRED
                    }
                    else -> isErrorException.value = errors
                }
            }
            isLoading.value = false
        }
    }

    fun actionRequest(user: CLUsers, position: Int, requestType: String) {
        viewModelScope.launch {
            val result =
                user.id?.let { it1 -> CLRepository.actionFollowRequest(it1, requestType) }
            launch(Dispatchers.Main) {
                when {
                    result?.contains(CLConstants.SUCCESS) == true -> {
                        isRequestSuccess.value = position
                    }
                    else -> isErrorException.value = CLErrors.SOMETHING_WENT_WRONG
                }
            }
        }
    }
}