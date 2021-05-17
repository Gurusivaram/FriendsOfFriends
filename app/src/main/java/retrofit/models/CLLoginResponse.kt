package retrofit.models

import com.google.gson.annotations.SerializedName

data class CLLoginResponse(
    @SerializedName("Tokens")
    val tokens: CLToken,
    @SerializedName("User")
    val user: CLUser
)