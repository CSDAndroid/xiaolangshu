package com.example.myapplication.mine.local.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.account.service.UserService
import com.example.myapplication.mine.bean.VideoCardInfo
import com.example.myapplication.service.video.collect.VideoCollectProcessor
import com.example.myapplication.service.video.like.VideoLikeProcessor
import com.example.myapplication.service.video.post.VideoPostProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class MViewModel(
    private val userService: UserService,
    private val videoLikeProcessor: VideoLikeProcessor,
    private val videoCollectProcessor: VideoCollectProcessor,
    private val videoPostProcessor: VideoPostProcessor
): ViewModel() {

    private val _videoLikeListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoLikeList: MutableLiveData<List<VideoCardInfo>?> get() = _videoLikeListLiveData

    private val _videoCollectionListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoCollectList: MutableLiveData<List<VideoCardInfo>?> get() = _videoCollectionListLiveData

    private val _videoPostListLiveData = MutableLiveData<List<VideoCardInfo>?>()
    val videoPostList: MutableLiveData<List<VideoCardInfo>?> get() = _videoPostListLiveData

    fun getPhone(): String? {
        return userService.getPhone()
    }

    fun getVideoLikeList(phone: String) {
        viewModelScope.launch {
            videoLikeProcessor.init()
        }
    }

    fun getVideoCollectList(phone: String) {
        viewModelScope.launch {
            videoCollectProcessor.init()
        }
    }

    fun getVideoPostList(phone: String) {
        viewModelScope.launch {
            videoPostProcessor.init()
        }
    }

}