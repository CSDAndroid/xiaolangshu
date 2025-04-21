package com.example.myapplication.service.processor.collect

import com.example.myapplication.common.bean.VideoCardInfo

interface VideoCollectProcessor {

    suspend fun init(phone: String, currentPage: MutableList<Int>): List<VideoCardInfo>

    fun isCollection(videoCardInfo: VideoCardInfo): Boolean

    fun updateCollectionList()

    suspend fun toggleCollect(videoCardInfo: VideoCardInfo): Boolean
}