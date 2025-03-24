package com.example.myapplication.service.video.post

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoPostProcessorModule {
    @Binds
    abstract fun bindVideoPostProcessor(videoPostProcessorImpl: VideoPostProcessorImpl): VideoPostProcessor
}