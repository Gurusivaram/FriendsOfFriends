package profile.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import common.CLConstants
import common.CLViewModel
import common.CLErrors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.CLRepository
import retrofit.models.CLUsers
import java.io.File
import java.io.FileOutputStream

class CLProfileEditViewModel(application: Application) : CLViewModel(application) {
    companion object {
        private const val TAG = "CLProfileEditViewModel"
    }

    private val context: Application = application
    var userName = ""
    lateinit var profileImageURI: Uri
    private lateinit var image: String
    val user by lazy {
        MutableLiveData<CLUsers>()
    }
    val isProfileEditSuccess by lazy {
        MutableLiveData<Boolean>()
    }

    fun populateData(email: String) {
        viewModelScope.launch {
            val result = CLRepository.getUser(email)
            launch(Dispatchers.Main) {
                user.value = result
            }
        }
    }

    fun saveUser() {
        viewModelScope.launch {
            val result = user.value?.let { CLRepository.updateUser(it) }
            launch(Dispatchers.Main) {
                when {
                    result?.contains(CLConstants.SUCCESS) == true -> {
                        isProfileEditSuccess.value = true
                    }
                    else -> isErrorException.value = CLErrors.SOMETHING_WENT_WRONG
                }
            }
        }
    }

    fun saveImage(): Uri {
        val userImageName = "${userName}.jpg"
        try {
            val userImage = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                userImageName
            )
            val bitmap = MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                profileImageURI
            )
            FileOutputStream(userImage).apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
                flush()
                close()
            }
            image = userImage.path
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            isErrorException.value = e.message
        }
        user.value?.avatar = image
        return image.toUri()
    }
}