package com.example.myapplication.mine.bean

import java.io.Serializable

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
): Serializable