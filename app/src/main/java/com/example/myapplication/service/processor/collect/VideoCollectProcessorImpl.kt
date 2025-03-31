package com.example.myapplication.service.processor.collect

import com.example.myapplication.common.bean.VideoCardInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoCollectProcessorImpl @Inject constructor(): VideoCollectProcessor {

    override suspend fun init(phone: String, currentPage: MutableList<Int>): List<VideoCardInfo> {
        TODO("Not yet implemented")
    }

    override fun isCollection(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateCollectionList() {
        TODO("Not yet implemented")
    }

    override suspend fun collect(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }
}