package com.example.myapplication.common.bean

import java.io.Serializable

data class VideoInfo(
    val aid: Long,
    val cid: Long,
    var like: Int,
    val image: String,
    val avatar: String,
    var collection: Int,
    val nickname: String,
    val description: String,
) : Serializable
