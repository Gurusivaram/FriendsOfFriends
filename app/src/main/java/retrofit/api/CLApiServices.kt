package retrofit.api

import com.google.gson.JsonObject
import retrofit.models.*
import retrofit2.Response
import retrofit2.http.*

interface CLApiServices {
    @POST("users")
    suspend fun signUp(
        @Body body: CLSignupRequest
    ): Response<JsonObject>

    @GET("users/confirmation")
    suspend fun verifyOTPOnSignUp(
        @Query("confirmation_token") otpToken: String
    ): Response<JsonObject>

    @POST("users/sign_in")
    @FormUrlEncoded
    suspend fun login(
        @Field("user[login]") userName: String,
        @Field("user[password]") password: String
    ): Response<CLLoginResponse>

    @HTTP(method = "DELETE", path = "users/sign_out", hasBody = true)
    @FormUrlEncoded
    suspend fun logout(
        @Field("user[email]") email: String,
        @Field("user[password]") password: String
    ): Response<JsonObject>

    @PUT("users/password")
    @FormUrlEncoded
    suspend fun resetPassword(
        @Field("user[reset_password_token]") otpPassword: String,
        @Field("user[password]") password: String,
        @Field("user[password_confirmation]") confirmPassword: String
    ): Response<JsonObject>

    @POST("users/password")
    @FormUrlEncoded
    suspend fun getOtpPassword(
        @Field("user[email]") email: String
    ): Response<JsonObject>

    @GET("get_auth_token")
    suspend fun getAutToken(
        @Header("Refresh") refreshToken: String
    ): Response<CLRefreshToken>

    @GET("home")
    suspend fun getUserList(
        @Query("page") pageNumber: Int
    ): Response<CLUserList>

    @POST("relationships")
    suspend fun followRequest(
        @Query("user_id") userId: Int
    ): Response<JsonObject>

    @PUT("relationships")
    suspend fun actionFollowRequest(
        @Query("following_id") followingId: Int,
        @Query("status") status: String
    ): Response<JsonObject>

    @GET("requests?view_info=Followers")
    suspend fun getFollowersList(): Response<ArrayList<CLUsers>>

    @GET("requests?view_info=Followings")
    suspend fun getFollowingsList(): Response<ArrayList<CLUsers>>

    @GET("requests?view_info=requests")
    suspend fun getFollowRequestsList(): Response<ArrayList<CLUsers>>

    @GET("requests?view_info=Followings")
    suspend fun searchFollowers(
        @Query("search") searchTerm: String,
        @Query("view_info") viewInfo: String = "Followers"
    ): Response<List<CLUsers>>

    @HTTP(method = "PATCH", path = "users", hasBody = true)
    suspend fun updateUser(
        @Body body: JsonObject
    ): Response<JsonObject>
}