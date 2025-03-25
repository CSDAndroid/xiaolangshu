package com.example.myapplication.service.video.like

import com.example.myapplication.mine.bean.VideoCardInfo
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

    override suspend fun like(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }
}