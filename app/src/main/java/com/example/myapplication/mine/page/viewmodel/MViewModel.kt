package com.example.myapplication.mine.page.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.account.service.UserService
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.service.processor.collect.VideoCollectProcessor
import com.example.myapplication.service.processor.like.VideoLikeProcessor
import com.example.myapplication.service.processor.post.VideoPostProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MViewModel @Inject constructor(
    private val userService: UserService,
    private val videoLikeProcessor: VideoLikeProcessor,
    private val videoCollectProcessor: VideoCollectProcessor,
    private val videoPostProcessor: VideoPostProcessor
) : ViewModel() {

    private val _videoLikeListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoLikeList: MutableLiveData<List<VideoCardInfo>?> get() = _videoLikeListLiveData

    private val _videoCollectionListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoCollectList: MutableLiveData<List<VideoCardInfo>?> get() = _videoCollectionListLiveData

    private val _videoPostListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoPostList: MutableLiveData<List<VideoCardInfo>?> get() = _videoPostListLiveData

    private val isLoading: MutableList<Boolean> = mutableListOf(false, false, false)
    private val isLastPage: MutableList<Boolean> = mutableListOf(false, false, false)
    private val currentPage: MutableList<Int> = mutableListOf(0, 0, 0)

    fun getPhone(): String? {
        return userService.getPhone()
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

    fun getVideoPostList(phone: String) {
        if (isLoading[2] || isLastPage[2]) return

        isLoading[2] = true
        viewModelScope.launch {
            val newVideos = videoPostProcessor.init(phone, currentPage)
            if (newVideos.isEmpty()) {
                isLastPage[2] = true
            } else {
                _videoPostListLiveData.postValue(videoPostList.value.orEmpty() + newVideos)
                currentPage[2] += 1
            }
            isLoading[2] = true
        }
    }

    fun toggleLike(video: VideoCardInfo, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val status = videoLikeProcessor.toggleLike(video)
            callback(status)
        }
    }

    fun updateCollectVideo(video: VideoCardInfo) {
        val currentVideoList = _videoCollectionListLiveData.value?.toMutableList() ?: return
        currentVideoList.indexOfFirst { it.aid == video.aid }.takeIf { it != -1 }?.let { index ->
            currentVideoList[index] = video
            _videoCollectionListLiveData.value = currentVideoList
        }
    }

    fun updatePostVideo(video: VideoCardInfo) {
        val currentVideoList = _videoPostListLiveData.value?.toMutableList() ?: return
        currentVideoList.indexOfFirst { it.aid == video.aid }.takeIf { it != -1 }?.let { index ->
            currentVideoList[index] = video
            _videoPostListLiveData.value = currentVideoList
        }
    }

    fun updateLikeVideo(video: VideoCardInfo) {
        val currentVideoList = _videoLikeListLiveData.value?.toMutableList() ?: return
        currentVideoList.indexOfFirst { it.aid == video.aid }.takeIf { it != -1 }?.let { index ->
            currentVideoList[index] = video
            _videoLikeListLiveData.value = currentVideoList
        }
    }

}