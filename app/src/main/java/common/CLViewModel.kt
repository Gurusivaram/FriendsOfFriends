package common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

open class CLViewModel(application: Application) : AndroidViewModel(application) {
    val isErrorException by lazy {
        MutableLiveData<String>()
    }
    val isErrorAPI by lazy {
        MutableLiveData<String>()
    }
    val isLoading by lazy {
        MutableLiveData<Boolean>()
    }
}