package com.example.myapplication.service.video.collect

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoCollectProcessorModule {

    @Binds
    abstract fun bindVideoCollectProcessor(videoCollectProcessorImpl: VideoCollectProcessorImpl): VideoCollectProcessor
}