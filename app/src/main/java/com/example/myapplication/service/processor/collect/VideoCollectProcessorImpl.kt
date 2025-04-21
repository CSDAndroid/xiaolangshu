package com.example.myapplication.service.processor.collect

import com.example.myapplication.common.bean.VideoCardInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoCollectProcessorImpl @Inject constructor(): VideoCollectProcessor {

    override suspend fun init(phone: String, currentPage: MutableList<Int>): List<VideoCardInfo> {
        return withContext(Dispatchers.IO) {
            TODO("Not yet implemented")
        }
    }

    override fun isCollection(videoCardInfo: VideoCardInfo): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateCollectionList() {
        TODO("Not yet implemented")
    }

    override suspend fun toggleCollect(videoCardInfo: VideoCardInfo): Boolean {
        return withContext(Dispatchers.IO) {
            TODO("Not yet implemented")
        }
    }
}