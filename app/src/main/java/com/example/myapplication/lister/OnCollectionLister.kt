package com.example.myapplication.lister

import com.example.myapplication.common.bean.VideoCardInfo

interface OnCollectionLister {
    fun onCollection(phone: String,videoInfo: VideoCardInfo)
    fun isCollection(videoInfo: VideoCardInfo, phone: String): Boolean
}