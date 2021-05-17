package retrofit.models

import com.google.gson.annotations.SerializedName

data class CLRefreshToken(
        @SerializedName("auth_token")
        val authToken: String?
)