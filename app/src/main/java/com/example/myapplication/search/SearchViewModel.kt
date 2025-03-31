package com.example.myapplication.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.account.service.UserService
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.http.HttpService
import com.example.myapplication.service.processor.like.VideoLikeProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userService: UserService,
    private val videoLikeProcessor: VideoLikeProcessor
) : ViewModel() {

    private val address = "https://api.bilibili.com/"
    private val service = HttpService.sendHttp(address, SearchApi::class.java)

    private val _videoListFromSearchLiveData = MutableLiveData<List<VideoCardInfo>>()
    val videoListFromSearch: MutableLiveData<List<VideoCardInfo>> get() = _videoListFromSearchLiveData

    fun getPhone(): String? {
        return userService.getPhone()
    }

    fun getVideoListFromNetWorkByKey(key: String) {
        viewModelScope.launch {
            try {
                val response = service.getVideoDataByKey(key)
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val resultList = response.body()!!.data.result
                        if (resultList.isNotEmpty() && resultList.size > 11 && resultList[11].data.isNotEmpty()) {
                            val videoTempListBySearch = resultList[11].data
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getVideoListFromNetWorkByKey", "网络请求关键词视频信息响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getVideoListFromNetWorkByKey", "Network请求错误---${e.message}", e)
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
        val currentVideoList = _videoListFromSearchLiveData.value?.toMutableList() ?: return
        currentVideoList.indexOfFirst { it.aid == video.aid }.takeIf { it != -1 }?.let { index ->
            currentVideoList[index] = video
            _videoListFromSearchLiveData.value = currentVideoList
        }
    }

}