package com.example.myapplication.service.video.collect

interface VideoCollectProcessor {

    fun init()

    fun isCollection()

    fun updateCollectionList()

    suspend fun collect()
}