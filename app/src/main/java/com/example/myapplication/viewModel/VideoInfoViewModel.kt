package com.example.myapplication.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.database.UserInfoDatabase
import com.example.myapplication.http.HttpInterface
import com.example.myapplication.http.HttpUtil
import com.example.myapplication.util.DealDataInfo.dealVideoInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
class VideoInfoViewModel(private val context: Context, private val database: UserInfoDatabase) :
    ViewModel() {

    private val address = "https://api.bilibili.com/"
    private val service = HttpUtil.sendHttp(address, HttpInterface::class.java)

    private val _videoListLiveData = MutableLiveData<List<VideoInfo>>()
    val videoList: MutableLiveData<List<VideoInfo>> get() = _videoListLiveData

    private val _videoListFromSearchLiveData = MutableLiveData<List<VideoInfo>>()
    val videoListFromSearch: MutableLiveData<List<VideoInfo>> get() = _videoListFromSearchLiveData

    private val _videoLikeListLiveData = MutableLiveData<List<VideoInfo>>()
    val videoLikeList: MutableLiveData<List<VideoInfo>> get() = _videoLikeListLiveData

    private val _videoCollectionListLiveData = MutableLiveData<List<VideoInfo>>()
    val videoCollectionList: MutableLiveData<List<VideoInfo>> get() = _videoCollectionListLiveData

    private val _videoWorkListLiveData = MutableLiveData<List<VideoInfo>>()
    val videoWorkList: MutableLiveData<List<VideoInfo>> get() = _videoWorkListLiveData

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getVideoListFromNetwork() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getVideoData()
                }
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val videoTempList = response.body()!!.data.item
                        val videoInfoList = dealVideoInfo(videoTempList)
                        _videoListLiveData.postValue(videoInfoList)
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getVideoInfoFromNetwork", "网络请求视频信息响应失败---${errorString}")
                    showToast("网络请求视频信息响应失败：${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getVideoInfoFromNetwork", "Network视频信息请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun getVideoListFromNetWorkByKey(key: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getVideoDataByKey(key)
                }
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val resultList = response.body()!!.data.result
                        if (resultList.isNotEmpty() && resultList.size > 11 && resultList[11].data.isNotEmpty()) {
                            val videoTempListBySearch = resultList[11].data
                            val videoInfoList = dealVideoInfo(videoTempListBySearch)
                            _videoListFromSearchLiveData.postValue(videoInfoList)
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e(
                        "getVideoListFromNetWorkByKey",
                        "网络请求关键词视频信息响应失败---${errorString}"
                    )
                    showToast("网络请求关键词视频信息响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getVideoListFromNetWorkByKey", "Network请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun getVideoLikeList(phone: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                database.userInfoDao().getUserVideoLikeListByPhone(phone)
            }

            if (response != null) {
                val videoLikeList =
                    Gson().fromJson<List<VideoInfo>>(response,object : TypeToken<List<VideoInfo>>() {}.type)
                _videoLikeListLiveData.postValue(videoLikeList)
            }
        }
    }

    fun setTheVideoToLike(videoInfo: VideoInfo, phone: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                database.userInfoDao().getUserVideoLikeListByPhone(phone)
            }

            if (response != null) {
                val videoLikeList =
                    Gson().fromJson<List<VideoInfo>>(response,object : TypeToken<List<VideoInfo>>() {}.type).toMutableList()

                if (!videoLikeList.contains(videoInfo)) {
                    videoLikeList.add(videoInfo)
                } else {
                    videoLikeList.remove(videoInfo)
                }

                val updatedVideoLikeListJson = Gson().toJson(videoLikeList)

                withContext(Dispatchers.IO) {
                    database.userInfoDao().updateUserVideoLikeList(phone, updatedVideoLikeListJson)
                    getVideoLikeList(phone)
                }
            } else {
                val videoLikeList = listOf(videoInfo)
                val updatedVideoLikeListJson = Gson().toJson(videoLikeList)

                withContext(Dispatchers.IO) {
                    database.userInfoDao().updateUserVideoLikeList(phone, updatedVideoLikeListJson)
                    getVideoLikeList(phone)
                }
            }
        }
    }

    fun getVideoCollectionList(phone: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                database.userInfoDao().getUserVideoCollectionListByPhone(phone)
            }

            if (response != null) {
                val videoCollectionList =
                    Gson().fromJson<List<VideoInfo>>(response,object : TypeToken<List<VideoInfo>>() {}.type)
                _videoCollectionListLiveData.postValue(videoCollectionList)
            }
        }
    }

    fun setTheVideoToCollection(videoInfo: VideoInfo,phone: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                database.userInfoDao().getUserVideoCollectionListByPhone(phone)
            }

            if (response != null) {
                val videoCollectionList =
                    Gson().fromJson<List<VideoInfo>>(response,object : TypeToken<List<VideoInfo>>() {}.type).toMutableList()

                if (!videoCollectionList.contains(videoInfo)) {
                    videoCollectionList.add(videoInfo)
                } else {
                    videoCollectionList.remove(videoInfo)
                }

                val updatedVideoCollectionList = Gson().toJson(videoCollectionList)

                withContext(Dispatchers.IO) {
                    database.userInfoDao().updateUserVideoCollectionList(phone,updatedVideoCollectionList)
                    getVideoCollectionList(phone)
                }
            } else {
                val videoCollectionList = listOf(videoInfo)
                val updatedVideoCollectionList = Gson().toJson(videoCollectionList)

                withContext(Dispatchers.IO) {
                    database.userInfoDao().updateUserVideoCollectionList(phone,updatedVideoCollectionList)
                }
            }
        }
    }

    fun getVideoWorkList(phone: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                database.userInfoDao().getUserVideoWorkListByPhone(phone)
            }

            if (response != null) {
                val videoWorkList =
                    Gson().fromJson<List<VideoInfo>>(response,object : TypeToken<List<VideoInfo>>() {}.type)
                _videoWorkListLiveData.postValue(videoWorkList)
            }
        }
    }

    fun setTheVideoToWork(videoInfo: VideoInfo,phone: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                database.userInfoDao().getUserVideoWorkListByPhone(phone)
            }

            if (response != null) {
                val videoWorkList =
                    Gson().fromJson<List<VideoInfo>>(response,object : TypeToken<List<VideoInfo>>() {}.type).toMutableList()

                if (!videoWorkList.contains(videoInfo)) {
                    videoWorkList.add(videoInfo)
                } else {
                    videoWorkList.remove(videoInfo)
                }

                val updatedVideoWorkList = Gson().toJson(videoCollectionList)

                withContext(Dispatchers.IO) {
                    database.userInfoDao().updateUserVideoWorkList(phone,updatedVideoWorkList)
                    getVideoWorkList(phone)
                }
            }  else {
                val videoWorkList = listOf(videoInfo)
                val updatedVideoWorkList = Gson().toJson(videoWorkList)

                withContext(Dispatchers.IO) {
                    database.userInfoDao().updateUserVideoWorkList(phone,updatedVideoWorkList)
                }
            }
        }
    }
}