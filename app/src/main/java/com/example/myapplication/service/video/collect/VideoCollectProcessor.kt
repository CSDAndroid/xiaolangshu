package com.example.myapplication.service.video.collect

import com.example.myapplication.mine.bean.VideoCardInfo

interface VideoCollectProcessor {

    suspend fun init(phone: String, currentPage: MutableList<Int>): List<VideoCardInfo>

    fun isCollection(videoCardInfo: VideoCardInfo): Boolean

    fun updateCollectionList()

    suspend fun collect(videoCardInfo: VideoCardInfo): Boolean
}