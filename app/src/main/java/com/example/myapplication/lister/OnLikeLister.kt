package com.example.myapplication.lister

import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1

interface OnLikeLister {
    fun onLike(phone: String, id: Long)
    fun onLike(phone: String, videoInfo: VideoInfo)
    fun isLike(picture1: Picture1, phone: String): Boolean
    fun isLike(videoInfo: VideoInfo,phone: String): Boolean
}