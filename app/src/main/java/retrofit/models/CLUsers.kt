package retrofit.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class CLUsers(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,
        var email: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("first_name")
        var firstName: String?,
        @SerializedName("last_name")
        var lastName: String?,
        val gender: String?,
        @SerializedName("phone_number")
        var phoneNumber: String?,
        @SerializedName("issued_at")
        val issuedAt: String?,
        val dob: String?,
        var avatar: String?,
        var address: String?,
        var isLoggedIn: Boolean? = false
)