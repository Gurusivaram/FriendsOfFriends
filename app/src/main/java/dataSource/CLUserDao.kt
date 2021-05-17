package dataSource

import androidx.room.*
import retrofit.models.CLUsers

@Dao
interface CLUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUser(user: CLUsers)

    @Query("SELECT * FROM user WHERE email == :email")
    suspend fun getUser(email: String): CLUsers?

    @Query("SELECT * FROM user WHERE isLoggedIn == :isLoggedIn")
    suspend fun getLoggedUser(isLoggedIn: Boolean = true): CLUsers?

    @Query("DELETE FROM user")
    suspend fun destroyDB()
}