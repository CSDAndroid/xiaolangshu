package com.example.myapplication.lister

import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.mine.bean.VideoCardInfo

interface OnLikeLister {
    fun onLike(phone: String, videoInfo: VideoCardInfo)
    fun isLike(videoInfo: VideoCardInfo,phone: String): Boolean
}