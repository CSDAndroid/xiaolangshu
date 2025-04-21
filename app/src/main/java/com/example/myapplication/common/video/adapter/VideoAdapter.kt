package com.example.myapplication.common.video.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.common.video.ExoPlayerPool
import com.example.myapplication.databinding.VideoItemViewBinding

class VideoAdapter(
    context: Context,
    cache: SimpleCache,
    private val onClickLike: (VideoCardInfo) -> Unit,
    private val onClickCollect: (VideoCardInfo) -> Unit
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private val videoList: MutableList<VideoCardInfo> = mutableListOf()
    private val viewHolderMap = mutableMapOf<Int, ViewHolder>()
    private val playerPool = ExoPlayerPool(maxSize = 3, context = context, cache = cache)

    inner class ViewHolder(val binding: VideoItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentUrl: String? = null
        private var exoPlayer: ExoPlayer? = null

        fun bind(videoInfo: VideoCardInfo) {
            val url = "https://example.com/video/${videoInfo.aid}/${videoInfo.cid}.mp4"
            currentUrl = url

            Glide.with(itemView.context).load(videoInfo.avatar).into(binding.videoItemAvatar)
            binding.videoItemNickname.text = videoInfo.nickname
            binding.videoItemDescription.text = videoInfo.description
            binding.videoItemLoveNumber.text = formatLikes(videoInfo.like)
            binding.videoItemCollectionNumber.text = formatLikes(videoInfo.collection)

            // 释放旧播放器
            exoPlayer?.let {
                playerPool.release(currentUrl!!)
            }

            // 从播放器池拿播放器
            exoPlayer = playerPool.acquire(url).apply {
                binding.videoItemVideoPlayer.player = this
                pause()
                seekTo(0)
            }
        }

        fun play() = exoPlayer?.play()
        fun pause() = exoPlayer?.pause()

        fun release() {
            exoPlayer?.let {
                binding.videoItemVideoPlayer.player = null
                currentUrl?.let(playerPool::release)
            }
            exoPlayer = null
            currentUrl = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            VideoItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewHolderMap[position] = holder
        holder.bind(videoList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        viewHolderMap.entries.removeIf { it.value == holder }
        holder.release()
    }

    override fun getItemCount(): Int = videoList.size

    fun addFirstVideo(video: VideoCardInfo) {
        videoList.add(0, video)
    }

    fun addVideoList(newVideos: List<VideoCardInfo>) {
        val uniqueNewVideos = newVideos.filterNot { newVideo ->
            videoList.any { existingVideo -> existingVideo.aid == newVideo.aid }
        }

        if (uniqueNewVideos.isNotEmpty()) {
            val startPosition = videoList.size
            videoList.addAll(uniqueNewVideos)
            notifyItemRangeInserted(startPosition, uniqueNewVideos.size)
        }
    }

    fun updateVideoList(video: VideoCardInfo) {
        videoList.indexOfFirst { it.aid == video.aid }.takeIf { it != -1 }?.let { index ->
            videoList[index] = video
            notifyItemChanged(index)
        }
    }

    fun preloadPosition(position: Int) {
        if (position in videoList.indices) {
            val videoInfo = videoList[position]
            val url = "https://example.com/video/${videoInfo.aid}/${videoInfo.cid}.mp4"
            playerPool.acquire(url) // 只是缓存播放器，不绑定UI
        }
    }

    fun playAtPosition(position: Int) {
        viewHolderMap[position]?.play()
    }

    fun pauseAtPosition(position: Int) {
        viewHolderMap[position]?.pause()
    }

    fun pauseAll() {
        viewHolderMap.values.forEach { it.pause() }
    }

    fun resumeCurrent(position: Int) {
        viewHolderMap[position]?.play()
    }

    fun release() {
        playerPool.releaseAll()
    }

    private fun formatLikes(likes: Int): String {
        return if (likes > 10000) "%.1fw".format(likes / 10000.0) else likes.toString()
    }

}