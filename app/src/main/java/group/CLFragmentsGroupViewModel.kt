package group

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import common.CLErrors
import common.CLViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers

class CLFragmentsGroupViewModel(application: Application) : CLViewModel(application) {
    val followings by lazy {
        MutableLiveData<MutableList<CLUsers>>()
    }

    fun getFollowingsList(isRefreshed: Boolean =  false) {
        if(!isRefreshed){
            isLoading.value = true
        }
        viewModelScope.launch {
            val (list, errors) = CLRepository.getFollowingsList()
            launch(Dispatchers.Main) {
                when {
                    list != null -> followings.value = list
                    errors?.contains(CLErrors.SESSION_EXPIRED) == true -> {
                        isErrorAPI.value = CLErrors.SESSION_EXPIRED
                    }
                    else -> isErrorException.value = errors
                }
                isLoading.value = false
            }
        }
    }
}