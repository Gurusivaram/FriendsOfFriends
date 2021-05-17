package repository

import android.util.Log
import com.google.gson.JsonObject
import common.CLApplication
import common.CLConstants
import common.CLErrors
import dataSource.CLUserDatabase
import retrofit.api.CLApiClient
import retrofit.models.*
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

object CLRepository {
    private const val TAG = "CLRepository Retrofit"
    private val db by lazy {
        CLApplication.getDB()
    }
    private val dao by lazy {
        db.userDao()
    }
    private val sharedPref by lazy {
        CLApplication.getPref()
    }

    suspend fun signUp(user: CLUsers, password: String): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
            response = CLApiClient.apiServicesWithoutAuthentication.signUp(
                createSignupRequest(
                    user,
                    password
                )
            )
            if (response.isSuccessful) {
                result = CLConstants.SUCCESS
                Log.v(TAG, "User Registration Success")
            } else {
                result = response.errorBody()?.string()
                Log.v(TAG, "User Registration Failed")
            }

        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    private fun createSignupRequest(user: CLUsers, password: String): CLSignupRequest {
        return CLSignupRequest(
            user = with(user) {
                return@with User(
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    address = address,
                    password = password
                )
            })
    }

    suspend fun login(userName: String, password: String): String? {
        var result: String? = null
        val response: Response<CLLoginResponse>
        try {
            response = CLApiClient.apiServicesWithoutAuthentication.login(userName, password)
            if (response.isSuccessful) {
                result = CLConstants.SUCCESS
                response.body()?.let {
                    sharedPref.saveTokens(it.tokens)
                    dao?.setUser(createLoginRequest(it.user))
                }
                Log.v(TAG, "User Login Success")
            } else {
                result = response.errorBody()?.string()
                Log.e(TAG, "User Login Failed  -> ${response.body().toString()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    private fun createLoginRequest(user: CLUser): CLUsers = CLUsers(
        user.id,
        user.email,
        null,
        null,
        user.firstName,
        user.lastName,
        user.gender,
        user.phoneNumber,
        null,
        user.dob,
        user.avatar,
        user.address,
        true
    )

    suspend fun logout(userName: String, password: String): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
            response = CLApiClient.apiServices.logout(userName, password)
            if (response.isSuccessful) {
                dao?.destroyDB()
                sharedPref.destroySharedPreferences()
                result = CLConstants.SUCCESS
                Log.v(TAG, "User Logout Success")
            } else {
                if (response.errorBody()?.string()
                        ?.contains(CLErrors.SESSION_EXPIRED_ERROR) == true
                ) {
                    result = CLConstants.SUCCESS
                    dao?.destroyDB()
                    sharedPref.destroySharedPreferences()
                } else {
                    result = response.errorBody()?.string()
                    Log.e(TAG, "User Logout Failed  -> ${response.body().toString()}")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    suspend fun getLoggedUser(): CLUsers? {
        return dao?.getLoggedUser()
    }

    suspend fun getUser(email: String): CLUsers? = dao?.getUser(email)

    suspend fun getUserList(pageNumber: Int = 1): Pair<MutableList<CLUsers>?, String?> {
        var list: MutableList<CLUsers>? = null
        var result: String? = null
        val response: Response<CLUserList>
        try {
            response = CLApiClient.apiServices.getUserList(pageNumber)
            if (response.isSuccessful) {
                list = response.body()?.allUsers?.toMutableList()
                response.body()?.allUsers?.forEach {
                    dao?.setUser(it)
                }
                Log.v(TAG, "Users List Received")
            } else {
                Log.v(TAG, "Users List Not Received or Failed to Receive")
                result = CLErrors.SESSION_EXPIRED
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return Pair(list, result)
    }

    suspend fun resetPassword(
        otpPassword: String,
        password: String,
        confirmPassword: String
    ): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
            response = CLApiClient.apiServices.resetPassword(otpPassword, password, confirmPassword)
            if (response.isSuccessful) {
                result = CLConstants.SUCCESS
                Log.v(TAG, "Password Reset Successful")
            } else {
                result = response.errorBody()?.string()
                Log.v(TAG, "Password Reset Failed")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    suspend fun getOtpPassword(email: String): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
            response = CLApiClient.apiServices.getOtpPassword(email)
            if (response.isSuccessful && !response.body().toString().contains("Email not found")) {
                result = CLConstants.SUCCESS
                Log.v(TAG, "OTP Password Sent")
            } else {
                result = response.body().toString()
                Log.v(TAG, "OTP Password Not Sent")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    suspend fun isOTPVerified(otp: String): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
            response = CLApiClient.apiServices.verifyOTPOnSignUp(otp)
            if (response.isSuccessful) {
                result = CLConstants.SUCCESS
                Log.v(TAG, "OTP Password Verified")
            } else {
                result = response.errorBody()?.string()
                Log.v(TAG, "OTP Password Verification Failed")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    suspend fun refreshAuthToken(): Pair<String, String> {
        var token = ""
        var result = ""
        val response: Response<CLRefreshToken>
        try {
            response = CLApiClient.apiServices.getAutToken(sharedPref.getRefreshToken())
            if (response.isSuccessful) {
                token = response.body()?.authToken.toString()
                sharedPref.saveAuthToken(token)
                Log.v(TAG, "Auth Token Refresh Successful")
            } else {
                Log.v(TAG, "Auth Token Refresh Failed")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message.toString()
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return Pair(token, result)
    }

    suspend fun searchFollowers(searchTerm: String): Pair<MutableList<CLUsers>?, String?> {
        var list: MutableList<CLUsers>? = null
        var result: String? = null
        val response: Response<List<CLUsers>>
        try {
            response = CLApiClient.apiServices.searchFollowers(searchTerm)
            if (response.isSuccessful) {
                list = response.body()?.toMutableList()
                list?.forEach {
                    dao?.setUser(it)
                }
                Log.v(TAG, "Search Result List Received")
            } else {
                Log.v(TAG, "Search Result List Not Received or Failed to Receive")
                result = CLErrors.SESSION_EXPIRED
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return Pair(list, result)
    }

    suspend fun followUser(userId: Int): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
            response = CLApiClient.apiServices.followRequest(userId)
            if (response.isSuccessful) {
                result = response.body().toString()
                Log.v(TAG, "Follow User SuccessFul")
            } else {
                result = CLErrors.SOMETHING_WENT_WRONG
                Log.v(TAG, "Follow User Failed")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    suspend fun actionFollowRequest(followingId: Int, action: String): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
            response = CLApiClient.apiServices.actionFollowRequest(followingId, action)
            if (response.isSuccessful) {
                result = CLConstants.SUCCESS
                Log.v(TAG, "Request SuccessFul")
            } else {
                result = response.errorBody()?.string()
                Log.v(TAG, "Request Failed")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    suspend fun getFollowersList(): Pair<MutableList<CLUsers>?, String?> {
        var list: MutableList<CLUsers>? = null
        var result: String? = null
        val response: Response<ArrayList<CLUsers>>
        try {
            response = CLApiClient.apiServices.getFollowersList()
            if (response.isSuccessful) {
                list = response.body()?.toMutableList()
                Log.v(TAG, "Followers List Retrieval Success")
            } else {
                Log.v(TAG, "Followers List Retrieval Failed")
                throw Exception(CLErrors.SOMETHING_WENT_WRONG)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return Pair(list, result)
    }

    suspend fun getFollowingsList(): Pair<MutableList<CLUsers>?, String?> {
        var list: MutableList<CLUsers>? = null
        var result: String? = null
        val response: Response<ArrayList<CLUsers>>
        try {
            response = CLApiClient.apiServices.getFollowingsList()
            if (response.isSuccessful) {
                list = response.body()?.toMutableList()
                Log.v(TAG, "Followings List Retrieval Success")
            } else {
                Log.v(TAG, "Followings List Retrieval Failed")
                throw Exception(CLErrors.SOMETHING_WENT_WRONG)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return Pair(list, result)
    }

    suspend fun getFollowRequestsList(): Pair<MutableList<CLUsers>?, String?> {
        var list: MutableList<CLUsers>? = null
        var result: String? = null
        val response: Response<ArrayList<CLUsers>>
        try {
            response = CLApiClient.apiServices.getFollowRequestsList()
            if (response.isSuccessful) {
                list = response.body()?.toMutableList()
                Log.v(TAG, "Follow Requests List Retrieval Success")
            } else {
                Log.v(TAG, "Follow Requests List Retrieval Failed")
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return Pair(list, result)
    }

    suspend fun updateUser(user: CLUsers): String? {
        var result: String? = null
        val response: Response<JsonObject>
        try {
//            Since Update API is not working, we may use only offline update using database.
            result = CLConstants.SUCCESS
            val oldUser = dao?.getLoggedUser()
            if (oldUser != null) {
                user.id = oldUser.id
                dao?.setUser(user)
            }
            /*
                response = CLApiClient.apiServices.updateUser(createUpdateRequest(user))
                if (response.isSuccessful) {
                    result = SUCCESS
                    val oldUser = db?.getLoggedUser()
                    if (oldUser != null) {
                        user.id = oldUser.id
                        db?.setUser(user)
                    }
                    Log.v(TAG, "Update Profile Data Success")
                } else {
                    Log.v(TAG, UPDATE_ERROR)
                    result = Errors.SOMETHING_WENT_WRONG
                }
             */
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            result = CLErrors.NETWORK_CONNECTION_ERROR
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            result = e.message
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.toString())
            result = CLErrors.REQUEST_TIMEOUT
        }
        return result
    }

    private fun createUpdateRequest(user: CLUsers): JsonObject {
        val json = JsonObject()
        val userJson = JsonObject()
        user.apply {
            userJson.apply {
                addProperty("first_name", firstName)
                addProperty("last_name", lastName)
                addProperty(
                    "phone_number",
                    phoneNumber?.length?.let { it1 ->
                        phoneNumber?.substring(
                            phoneNumber?.length!! - 11,
                            it1
                        )
                    })
                addProperty("address", address)
                addProperty("avatar", avatar.toString())
            }
            json.add("user", userJson)
        }
        return json
    }

    fun savePassword(password: String) {
        sharedPref.savePassword(password)
    }

    fun getPassword(): String = sharedPref.getPassword()
}