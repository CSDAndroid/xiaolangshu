package com.example.myapplication.lister

import com.example.myapplication.common.bean.VideoCardInfo

interface OnLikeLister {
    fun onLike(phone: String, videoInfo: VideoCardInfo)
    fun isLike(videoInfo: VideoCardInfo, phone: String): Boolean
}