package com.example.myapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userInfo")
data class UserInfo(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @ColumnInfo(name = "avatar")
    val avatar: String?,

    @ColumnInfo(name = "nickname")
    val nickname: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "introduction")
    val introduction: String?,

    @ColumnInfo(name = "sex")
    val sex: String?,

    @ColumnInfo(name = "birthday")
    val birthday: String?,

    @ColumnInfo(name = "career")
    val career: String?,

    @ColumnInfo(name = "region")
    val region: String?,

    @ColumnInfo(name = "school")
    val school: String?,

    @ColumnInfo(name = "background")
    val backgroundImage: String?,

    @ColumnInfo(name = "focus_list")
    var focusList: String?,

    @ColumnInfo(name = "follower_count")
    val followerCount: Int? = 0,

    @ColumnInfo(name = "likes_and_collections")
    val likesAndCollections: Int? = 0,

    @ColumnInfo(name = "works_list")
    val worksList: String?,

    @ColumnInfo(name = "collection_list")
    val collectionList: String?,

    @ColumnInfo(name = "likes_list")
    val likesList: String?,

    @ColumnInfo(name = "comments_list")
    val commentsList: String?,

    @ColumnInfo(name = "search_history")
    val searchHistory: String?,

    @ColumnInfo(name = "video_like_list")
    val videoLikeList: String?,

    @ColumnInfo(name = "video_collection_list")
    val videoCollectionList: String?,

    @ColumnInfo(name = "video_work_list")
    val videoWorkList: String?,
)
