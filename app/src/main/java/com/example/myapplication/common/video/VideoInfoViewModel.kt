package com.example.myapplication.common.video

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.http.HttpInterface
import com.example.myapplication.http.HttpService
import com.example.myapplication.service.processor.collect.VideoCollectProcessor
import com.example.myapplication.service.processor.like.VideoLikeProcessor
import com.example.myapplication.util.DealDataInfo.dealVideoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideoInfoViewModel @Inject constructor(
    private val videoLikeProcessor: VideoLikeProcessor,
    private val videoCollectProcessor: VideoCollectProcessor,
) : ViewModel() {

    private val address = "https://api.bilibili.com/"
    private val service = HttpService.sendHttp(address, HttpInterface::class.java)

    private val _videoListLiveData = MutableLiveData<List<VideoCardInfo>>()
    val videoList: MutableLiveData<List<VideoCardInfo>> get() = _videoListLiveData

    private val _videoListFromSearchLiveData = MutableLiveData<List<VideoCardInfo>>()
    val videoListFromSearch: MutableLiveData<List<VideoCardInfo>> get() = _videoListFromSearchLiveData

    private val _videoLikeListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoLikeList: MutableLiveData<List<VideoCardInfo>?> get() = _videoLikeListLiveData

    private val _videoCollectionListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoCollectList: MutableLiveData<List<VideoCardInfo>?> get() = _videoCollectionListLiveData

    private val isLoading: MutableList<Boolean> = mutableListOf(false, false)
    private val isLastPage: MutableList<Boolean> = mutableListOf(false, false)
    private val currentPage: MutableList<Int> = mutableListOf(0, 0)

    fun fetchVideos() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getVideoData()
                }
                if (response.isSuccessful) {
                    val videoTempList = response.body()!!.data.item
                    _videoListLiveData.postValue(
                        videoList.value.orEmpty() + dealVideoInfo(videoTempList)
                    )
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("fetchVideos", "网络请求视频信息响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("fetchVideos", "Network视频信息请求错误---${e.message}", e)
            }
        }
    }

    fun fetchVideosBySearch(keyword: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getVideoDataByKey(keyword)
                }
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val resultList = response.body()!!.data.result
                        if (resultList.isNotEmpty() && resultList.size > 11 && resultList[11].data.isNotEmpty()) {
                            val videoTempListBySearch = resultList[11].data
                            _videoListFromSearchLiveData.postValue(
                                videoListFromSearch.value.orEmpty() + dealVideoInfo(videoTempListBySearch)
                            )
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("fetchVideosBySearch", "网络请求视频信息响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("fetchVideosBySearch", "Network视频信息请求错误---${e.message}", e)
            }
        }
    }

    fun getVideoLikeList(phone: String) {
        if (isLoading[0] || isLastPage[0]) return

        isLoading[0] = true
        viewModelScope.launch {
            val newVideo = videoLikeProcessor.init(phone, currentPage[0])
            if (newVideo.isEmpty()) {
                isLastPage[0] = true
            } else {
                _videoLikeListLiveData.postValue(videoLikeList.value.orEmpty() + newVideo)
                currentPage[0] += 1
            }
            isLoading[0] = false
        }
    }

    fun getVideoCollectList(phone: String) {
        if (isLoading[1] || isLastPage[1]) return

        isLoading[1] = true
        viewModelScope.launch {
            val newVideo = videoCollectProcessor.init(phone, currentPage)
            if (newVideo.isEmpty()) {
                isLastPage[1] = true
            } else {
                _videoCollectionListLiveData.postValue(videoCollectList.value.orEmpty() + newVideo)
                currentPage[1] += 1
            }
            isLoading[1] = false
        }
    }

    fun toggleLike(video: VideoCardInfo, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val status = videoLikeProcessor.toggleLike(video)
            callback(status)
        }
    }

    fun toggleCollect(video: VideoCardInfo, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val status = videoCollectProcessor.toggleCollect(video)
            callback(status)
        }
    }

    fun updateVideo(video: VideoCardInfo) {
        val currentVideoList = _videoListLiveData.value?.toMutableList() ?: return
        currentVideoList.indexOfFirst { it.aid == video.aid }.takeIf { it != -1 }?.let { index ->
            currentVideoList[index] = video
            _videoListLiveData.value = currentVideoList
        }
    }
}