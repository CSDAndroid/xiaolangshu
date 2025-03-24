package com.example.myapplication.service.video.like

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoLikeProcessorModule {

    @Binds
    abstract fun bindVideoLikeProcessor(videoLikeProcessorImpl: VideoLikeProcessorImpl): VideoLikeProcessor
}