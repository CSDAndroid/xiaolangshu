package com.example.myapplication.service.processor.like

import com.example.myapplication.common.bean.VideoCardInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoLikeProcessorImpl @Inject constructor(): VideoLikeProcessor {
    override suspend fun init(phone: String, i: Int): List<VideoCardInfo> {
        TODO("Not yet implemented")
    }

    override fun isLike(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateLikeList() {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLike(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }
}