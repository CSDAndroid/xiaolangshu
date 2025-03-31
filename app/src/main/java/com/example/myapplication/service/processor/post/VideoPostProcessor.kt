package com.example.myapplication.service.processor.post

import com.example.myapplication.common.bean.VideoCardInfo

interface VideoPostProcessor {

    suspend fun init(phone: String, currentPage: MutableList<Int>): List<VideoCardInfo>

    suspend fun post(videoCardInfo: VideoCardInfo): Boolean

    suspend fun deletePost(videoCardInfo: VideoCardInfo): Boolean

    fun updatePostList()
}