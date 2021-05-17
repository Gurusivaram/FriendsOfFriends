package retrofit.models

import com.google.gson.annotations.SerializedName

data class CLSignupRequest(
    var user: User?
)

data class User(
    var email: String?,
    @SerializedName("first_name")
    var firstName: String?,
    @SerializedName("last_name")
    var lastName: String?,
    @SerializedName("phone_number")
    var phoneNumber: String?,
    var address: String?,
    var password: String?
)