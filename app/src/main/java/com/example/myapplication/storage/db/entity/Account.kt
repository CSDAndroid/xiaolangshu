package com.example.myapplication.storage.db.entity

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

