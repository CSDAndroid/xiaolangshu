package com.example.myapplication.lister

import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1

interface OnCollectionLister {
    fun onCollection(phone: String,id: Long)
    fun onCollection(phone: String,videoInfo: VideoInfo)
    fun isCollection(picture1: Picture1, phone: String): Boolean
    fun isCollection(videoInfo: VideoInfo,phone: String): Boolean
}