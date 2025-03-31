package com.example.myapplication.home.page.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.http.HttpService
import com.example.myapplication.home.MainApi
import com.example.myapplication.service.processor.like.VideoLikeProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoLikeProcessor: VideoLikeProcessor
): ViewModel() {

    private val address = "https://api.bilibili.com/"
    private val service = HttpService.sendHttp(address, MainApi::class.java)

    private val _videoListLiveData = MutableLiveData<List<VideoCardInfo>>()
    val videoList: MutableLiveData<List<VideoCardInfo>> get() = _videoListLiveData

    fun fetchVideos() {
        viewModelScope.launch {
            try {
                val response = service.getVideoData()
                if (response.isSuccessful) {
                    val videoTempList = response.body()!!.data.item
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getVideoInfoFromNetwork", "网络请求视频信息响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("fetchVideos", "Network视频信息请求错误---${e.message}", e)
            }

        }
    }

    fun toggleLike(video: VideoCardInfo, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val status = videoLikeProcessor.toggleLike(video)
            callback(status)
        }
    }

    fun updateLikeVideo(video: VideoCardInfo) {
        val currentVideoList = _videoListLiveData.value?.toMutableList() ?: return
        currentVideoList.indexOfFirst { it.aid == video.aid }.takeIf { it != -1 }?.let { index ->
            currentVideoList[index] = video
            _videoListLiveData.value = currentVideoList
        }
    }

}