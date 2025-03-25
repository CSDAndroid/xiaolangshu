package com.example.myapplication.service.video.post

import com.example.myapplication.mine.bean.VideoCardInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoPostProcessorImpl @Inject constructor(): VideoPostProcessor {
    override suspend fun init(phone: String, currentPage: MutableList<Int>): List<VideoCardInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun post(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deletePost(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }

    override fun updatePostList() {
        TODO("Not yet implemented")
    }

}