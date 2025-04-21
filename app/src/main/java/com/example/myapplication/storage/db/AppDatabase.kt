package com.example.myapplication.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.storage.db.dao.AccountDao
import com.example.myapplication.storage.db.dao.AccountOtherInfoDao
import com.example.myapplication.storage.db.entity.Account
import com.example.myapplication.storage.db.entity.AccountOtherInfo

@Database(entities = [Account::class, AccountOtherInfo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun accountOtherDao(): AccountOtherInfoDao

//    companion object {
//        @Volatile
//        private var INSTANCE: AccountDatabase? = null
//
//        fun getDatabase(context: Context): AccountDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AccountDatabase::class.java,
//                    "app_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}