package com.example.myapplication.service.video.post

import com.example.myapplication.mine.bean.VideoCardInfo

interface VideoPostProcessor {

    suspend fun init(phone: String, currentPage: MutableList<Int>): List<VideoCardInfo>

    suspend fun post(videoCardInfo: VideoCardInfo): Boolean

    suspend fun deletePost(videoCardInfo: VideoCardInfo): Boolean

    fun updatePostList()
}