package com.example.myapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class Account(

    @PrimaryKey
    val phone: String,

    @ColumnInfo(name = "avatar")
    val avatar: String?,

    @ColumnInfo(name = "nickname")
    val nickname: String?,

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
    val background: String?,

)

@Entity(tableName = "accountOtherInfo", foreignKeys = [ForeignKey(
    entity = Account::class,
    parentColumns = ["phone"],
    childColumns = ["_phone"],
    onDelete = ForeignKey.CASCADE
)]
)
data class AccountOtherInfo (

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "_phone")
    val phone: Int,

    @ColumnInfo(name = "focus_number")
    val fn: Int? = 0,

    @ColumnInfo(name = "like_collection_number")
    val lcn: Int? = 0,

    @ColumnInfo(name = "focus_list")
    var fl: String?,

    @ColumnInfo(name = "works_list")
    val wl: String?,

    @ColumnInfo(name = "likes_list")
    val ll: String?,

    @ColumnInfo(name = "collection_list")
    val cl: String?,
)
