package common

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.contactlistapp.R

class CLAlerts(private val context: Context) {
    fun showAlertDialog(alertText: String) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog.apply {
            setMessage(alertText)
            setPositiveButton(
                context.getString(R.string.okay)
            ) { _, _ -> }
            val alert: AlertDialog = create()
            alert.show()
        }
    }

    fun getAlertDialog(): AlertDialog.Builder = AlertDialog.Builder(context)

    fun showToast(alertText: String) {
        Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show()
    }
}