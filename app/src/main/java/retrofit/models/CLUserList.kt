package retrofit.models

import com.google.gson.annotations.SerializedName

data class CLUserList(
        val count: Int?,
        @SerializedName("all_users")
        val allUsers: List<CLUsers>?
)
