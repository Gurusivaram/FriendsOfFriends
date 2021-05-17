package splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dataSource.CLUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers

class CLSplashViewModel(application: Application) : AndroidViewModel(application) {
    val user by lazy {
        MutableLiveData<CLUsers>()
    }

    fun isUserAlreadyLoggedIn() {
        viewModelScope.launch {
            val result: CLUsers? = CLRepository.getLoggedUser()
            launch(Dispatchers.Main) {
                user.value = result
            }
        }
    }
}