package dataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import retrofit.models.CLUsers

@Database(entities = [CLUsers::class], version = 1, exportSchema = false)
abstract class CLUserDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: CLUserDatabase? = null
        private const val DATABASE_NAME = "USER"

        fun getDatabase(context: Context): CLUserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    CLUserDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun userDao(): CLUserDao?
}