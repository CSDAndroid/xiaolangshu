package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInfoDao {

    // 插入一个用户信息
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userInfo: UserInfo)

    // 根据 userId 获取特定用户信息
    @Query("SELECT * FROM userInfo WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserInfo?

    // 根据 phone 获取 userId
    @Query("SELECT userId FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserIdByPhone(phone: String): Int

    // 根据 telephone 获取特定用户信息
    @Query("SELECT * FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserInfo?

    // 查询 avatar 根据 phone
    @Query("SELECT avatar FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserAvatarByPhone(phone: String): String?

    // 查询 backgroundImage 根据 phone
    @Query("SELECT background FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserBackgroundByPhone(phone: String): String?

    // 更新用户信息
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(userInfo: UserInfo)

    // 更新用户昵称
    @Query("UPDATE userInfo SET nickname = :name WHERE userId = :id")
    suspend fun updateUserName(id: Int, name: String)

    // 更新用户头像
    @Query("UPDATE userInfo SET avatar = :avatar WHERE phone = :phone")
    suspend fun updateUserAvatar(phone: String, avatar: String)

    // 更新用户背景
    @Query("UPDATE userInfo SET background = :backgroundImage WHERE phone = :phone")
    suspend fun updateUserBackground(phone: String, backgroundImage: String)

    @Query(
        """  
        UPDATE userInfo   
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
    suspend fun updateUserByPhone(
        introduction: String?,
        birthday: String?,
        sex: String?,
        nickname: String,
        career: String?,
        region: String?,
        school: String?,
        phone: String
    )

    // 删除用户信息
    @Delete
    suspend fun deleteUser(userInfo: UserInfo)

    // 删除所有用户信息
    @Query("DELETE FROM userInfo")
    suspend fun deleteAllUsers()


//    处理图片点赞收藏
    @Query("UPDATE userInfo SET likes_list = :likesList WHERE phone = :phone")
    suspend fun setLikesList(phone: String, likesList: String)

    @Query("SELECT likes_list FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserLikeListByPhone(phone: String): String?

    @Query("UPDATE userInfo SET collection_list = :collectionsList WHERE phone = :phone")
    suspend fun setCollectionsList(phone: String, collectionsList: String)

    @Query("SELECT collection_list FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserCollectionListByPhone(phone: String): String?

    @Query("UPDATE userInfo SET focus_list = :focusList WHERE phone = :phone")
    suspend fun setFocusList(phone: String, focusList: String)

    @Query("SELECT focus_list FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserFocusListByPhone(phone: String): String?


//    处理视频点赞收藏
    @Query("UPDATE userInfo SET video_like_list = :videoLikesList WHERE phone = :phone")
    suspend fun updateUserVideoLikeList(phone: String, videoLikesList: String)

    @Query("SELECT video_like_list FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserVideoLikeListByPhone(phone: String): String?

    @Query("UPDATE userInfo SET video_collection_list = :videoCollectionsList WHERE phone = :phone")
    suspend fun updateUserVideoCollectionList(phone: String, videoCollectionsList: String)

    @Query("SELECT video_collection_list FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserVideoCollectionListByPhone(phone: String): String?

    @Query("UPDATE userInfo SET video_work_list = :videoWorksList WHERE phone = :phone")
    suspend fun updateUserVideoWorkList(phone: String, videoWorksList: String)

    @Query("SELECT video_work_list FROM userInfo WHERE phone = :phone LIMIT 1")
    suspend fun getUserVideoWorkListByPhone(phone: String): String?
}