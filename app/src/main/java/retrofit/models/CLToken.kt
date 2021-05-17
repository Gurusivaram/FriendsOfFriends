package retrofit.models

import com.google.gson.annotations.SerializedName

data class CLToken(
        @SerializedName("Auth_token")
        val authToken: String,
        @SerializedName("Refresh_token")
        val refreshToken: String
)
