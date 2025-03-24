package com.example.myapplication.service.video.like

interface VideoLikeProcessor {

    fun init()

    fun isLike()

    fun updateLikeList()

    suspend fun like()
}