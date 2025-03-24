package com.example.myapplication.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoPlayerService {

    @Binds
    abstract fun bindVideoPlayerService(videoPlayerImpl: VideoPlayerImpl): VideoPlayer
}