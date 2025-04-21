package com.example.myapplication.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val phone: String,

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
