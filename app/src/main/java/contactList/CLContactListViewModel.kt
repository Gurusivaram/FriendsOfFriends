package contactList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import common.CLErrors
import common.CLViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers

class CLContactListViewModel(application: Application) : CLViewModel(application) {
    private var pageNumber: Int = 0
    var loggedUserEmail = ""
    var loggedUserName = ""
    val contacts by lazy {
        MutableLiveData<MutableList<CLUsers>>()
    }
    val followings by lazy {
        MutableLiveData<MutableMap<String, CLUsers>>()
    }
    val searchResult by lazy {
        MutableLiveData<List<CLUsers>>()
    }
    val isUserFollowSuccess by lazy {
        MutableLiveData<String>()
    }
    val isUserUpdated by lazy {
        MutableLiveData<Boolean>()
    }
    val followRequestState by lazy {
        MutableLiveData<Boolean>()
    }

    fun getFollowingsList() {
        viewModelScope.launch {
            val (list, errors) = CLRepository.getFollowingsList()
            launch(Dispatchers.Main) {
                when {
                    list != null -> {
                        list.forEach {
                            it.email?.let { it1 ->
                                followings.value?.set(it1, it)
                            }
                        }
                    }
                    errors?.contains(CLErrors.SESSION_EXPIRED) == true -> {
                        isErrorAPI.value = CLErrors.SESSION_EXPIRED
                    }
                    else -> isErrorException.value = errors
                }
            }
        }
    }

    fun getList(isRefreshed: Boolean = false) {
        if (isRefreshed) {
            pageNumber = 0
            contacts.value = null
        }else{
            isLoading.value = true
        }
        viewModelScope.launch {
            val (list, errors) = CLRepository.getUserList(++pageNumber)
            launch(Dispatchers.Main) {
                when {
                    list != null -> {
                        list.let {
                            if (contacts.value != null) {
                                val oldResult = contacts.value
                                oldResult?.addAll(it)
                                contacts.value = oldResult
                            } else {
                                getLoggedUser(it)
                            }
                        }
                    }
                    errors?.contains(CLErrors.SESSION_EXPIRED) == true -> {
                        isErrorAPI.value = CLErrors.SESSION_EXPIRED
                    }
                    else -> isErrorException.value = errors
                }
            }
            isLoading.value = false
        }
    }

    private fun getLoggedUser(mutableList: MutableList<CLUsers>) {
        isLoading.value = true
        viewModelScope.launch {
            viewModelScope.launch {
                val result = CLRepository.getLoggedUser()
                launch(Dispatchers.Main) {
                    result?.let {
                        loggedUserEmail = it.email.toString()
                        loggedUserName = "${it.firstName} ${it.lastName}"
                        mutableList.add(0, it)
                        contacts.value = mutableList
                    }
                    isLoading.value = false
                }
            }
        }
    }

    fun getUpdatedLoggedUser() {
        viewModelScope.launch {
            viewModelScope.launch {
                val result = CLRepository.getLoggedUser()
                launch(Dispatchers.Main) {
                    result?.let {
                        contacts.value?.set(0, it)
                        isUserUpdated.value = true
                    }
                }
            }
        }
    }

    fun searchFollowers(key: String) {
        isLoading.value = true
        viewModelScope.launch {
            val (list, errors) = CLRepository.searchFollowers(key)
            launch(Dispatchers.Main) {
                when {
                    list != null -> {
                        searchResult.value = list
                    }
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