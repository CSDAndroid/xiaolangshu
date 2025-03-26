package com.example.myapplication.service.video.like

import com.example.myapplication.mine.bean.VideoCardInfo

interface VideoLikeProcessor {

    suspend fun init(phone: String, i: Int): List<VideoCardInfo>

    fun isLike(videoCardInfo: VideoCardInfo): Boolean

    fun updateLikeList()

    suspend fun toggleLike(videoCardInfo: VideoCardInfo): Boolean
}