package com.example.myapplication.service.video.post

interface VideoPostProcessor {

    fun init()

    fun updatePostList()

    suspend fun post()

    suspend fun deletePost()
}