package com.example.myapplication.service

import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoPlayerImpl @Inject constructor(
    private val exoPlayer: ExoPlayer
): VideoPlayer {
    override fun start() {
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }
}