package com.example.myapplication.common.video

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import java.util.LinkedList

class ExoPlayerPool(
    private val maxSize: Int,
    private val context: Context,
    private val cache: SimpleCache
) {
    private val availablePlayers = LinkedList<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()
    // key使用url，确保每个url唯一对应播放器

    fun acquire(url: String): ExoPlayer {
        // 复用对应 url 播放器
        activePlayers[url]?.let {
            return it
        }

        // 从可用池获取播放器（无视 URL）
        val reusable = if (availablePlayers.isNotEmpty()) availablePlayers.removeFirst() else null

        val player = reusable ?: createNewPlayer()

        // 配置播放器播放目标url
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()

        activePlayers[url] = player

        managePoolSize()
        return player
    }

    @OptIn(UnstableApi::class)
    private fun createNewPlayer(): ExoPlayer {
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)

        return ExoPlayer.Builder(context)
            .setTrackSelector(DefaultTrackSelector(context))
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    private fun managePoolSize() {
        while (activePlayers.size > maxSize) {
            // 根据LRU策略释放最早激活的播放器
            val entry = activePlayers.entries.firstOrNull() ?: break
            release(entry.key)
        }
    }

    fun release(url: String) {
        val player = activePlayers.remove(url) ?: return

        player.pause()
        player.seekTo(0)
        player.clearMediaItems()

        if (availablePlayers.size < maxSize) {
            availablePlayers.addLast(player)
        } else {
            player.release()
        }
    }

    fun releaseAll() {
        activePlayers.values.forEach { it.release() }
        availablePlayers.forEach { it.release() }
        activePlayers.clear()
        availablePlayers.clear()
    }
}