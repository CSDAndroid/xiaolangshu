package com.example.myapplication.service.processor.like

import com.example.myapplication.common.bean.VideoCardInfo

interface VideoLikeProcessor {

    suspend fun init(phone: String, i: Int): List<VideoCardInfo>

    fun isLike(videoCardInfo: VideoCardInfo): Boolean

    fun updateLikeList()

    suspend fun toggleLike(videoCardInfo: VideoCardInfo): Boolean
}