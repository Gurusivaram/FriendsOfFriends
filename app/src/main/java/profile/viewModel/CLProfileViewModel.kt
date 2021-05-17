package profile.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CLProfileViewModel(application: Application) : AndroidViewModel(application) {
    var email = ""
    var loggedUserEmail = ""
    var isEdited = false
}