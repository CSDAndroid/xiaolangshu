package com.example.myapplication.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.storage.db.entity.Account

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account): Long

    @Update(entity = Account::class)
    suspend fun update(account: Account)

    @Delete(entity = Account::class)
    suspend fun delete(account: Account)

    @Query("SELECT * FROM account WHERE phone = :phone LIMIT 1")
    suspend fun getAccount(phone: String): Account

    @Query("SELECT avatar FROM account WHERE phone = :phone LIMIT 1")
    suspend fun getAvatar(phone: String): String?

    @Query("SELECT background FROM account WHERE phone = :phone LIMIT 1")
    suspend fun getBackground(phone: String): String?

    @Query("UPDATE account SET nickname = :name WHERE phone = :phone")
    suspend fun updateNickname(name: String, phone: String)

    @Query("UPDATE account SET avatar = :avatar WHERE phone = :phone")
    suspend fun updateAvatar(avatar: String, phone: String)

    @Query("UPDATE account SET background = :background WHERE phone = :phone")
    suspend fun updateBackground(background: String, phone: String)

    @Query(
        """
        UPDATE account
        SET introduction = :introduction,
            birthday = :birthday,
            sex = :sex,
            nickname = :nickname,
            career = :career,
            region = :region,
            school = :school
        WHERE phone = :phone
    """
    )
    suspend fun updateAccountByPhone(
        introduction: String?,
        birthday: String?,
        sex: String?,
        nickname: String,
        career: String?,
        region: String?,
        school: String?,
        phone: String
    )
}