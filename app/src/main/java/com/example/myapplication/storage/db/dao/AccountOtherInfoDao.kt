package com.example.myapplication.storage.db.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AccountOtherInfoDao {
    @Query("SELECT works_list FROM accountOtherInfo WHERE _phone = :phone LIMIT 1")
    suspend fun getWorkList(phone: String): String?

    @Query("SELECT collection_list FROM accountOtherInfo WHERE _phone = :phone LIMIT 1")
    suspend fun getCollectionList(phone: String): String?

    @Query("SELECT likes_list FROM accountOtherInfo WHERE _phone = :phone LIMIT 1")
    suspend fun getLikeList(phone: String): String?

    @Query("UPDATE accountOtherInfo SET likes_list = :likesList WHERE _phone = :phone")
    suspend fun updateLikeList(phone: String, likesList: String)

    @Query("UPDATE accountOtherInfo SET collection_list = :collectionsList WHERE _phone = :phone")
    suspend fun updateCollectionList(phone: String, collectionsList: String)

    @Query("UPDATE accountOtherInfo SET works_list = :worksList WHERE _phone = :phone")
    suspend fun updateWorkList(phone: String, worksList: String)

    @Query("UPDATE accountOtherInfo SET focus_list = :focusList WHERE _phone = :phone")
    suspend fun updateFocusList(phone: String, focusList: String)
}