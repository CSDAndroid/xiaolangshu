package com.example.myapplication.lister

import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.mine.bean.VideoCardInfo

interface OnCollectionLister {
    fun onCollection(phone: String,id: Long)
    fun onCollection(phone: String,videoInfo: VideoCardInfo)
    fun isCollection(picture1: Picture1, phone: String): Boolean
    fun isCollection(videoInfo: VideoCardInfo,phone: String): Boolean
}