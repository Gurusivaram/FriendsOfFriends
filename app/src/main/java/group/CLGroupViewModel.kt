package group

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CLGroupViewModel(application: Application) : AndroidViewModel(application) {
    var loggedUser = ""
}