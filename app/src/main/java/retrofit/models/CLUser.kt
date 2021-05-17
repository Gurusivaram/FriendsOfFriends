package retrofit.models

import com.google.gson.annotations.SerializedName

data class CLUser(
        val id: Int?,
        val email: String?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("last_name")
        val lastName: String?,
        val gender: String?,
        @SerializedName("phone_number")
        val phoneNumber: String?,
        val dob: String?,
        val avatar: String?,
        val address: String?
)