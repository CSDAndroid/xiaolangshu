package com.example.myapplication.common.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoCardInfo(
    val aid: Long,
    val cid: Long,
    var like: Int,
    val image: String,
    val avatar: String,
    var collection: Int,
    val nickname: String,
    val description: String,
    var isLike: Boolean,
    var isCollect: Boolean
): Parcelable