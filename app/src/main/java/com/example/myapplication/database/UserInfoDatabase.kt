package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserInfo::class], version = 1, exportSchema = true)
abstract class UserInfoDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao

    companion object {
        @Volatile
        private var INSTANCE: UserInfoDatabase? = null

        fun getDatabase(context: Context): UserInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserInfoDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}