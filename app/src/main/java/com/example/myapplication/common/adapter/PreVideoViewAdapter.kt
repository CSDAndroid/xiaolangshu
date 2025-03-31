package com.example.myapplication.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.PreVideoItemViewBinding
import com.example.myapplication.common.bean.VideoCardInfo

class PreVideoViewAdapter(
    private val onclickLike: (VideoCardInfo, Int) -> Unit
) : RecyclerView.Adapter<PreVideoViewAdapter.ViewHolder>() {

    private val videoList: MutableList<VideoCardInfo> = mutableListOf()

    inner class ViewHolder(val binding: PreVideoItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PreVideoItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoInfo = videoList[position]

        Glide.with(holder.itemView.context)
            .load(videoInfo.image)
            .into(holder.binding.PreVideoItemImg)

        Glide.with(holder.itemView.context)
            .load(videoInfo.avatar)
            .into(holder.binding.PreVideoItemAvatar)

        holder.binding.PreVideoItemNickname.text = videoInfo.nickname
        holder.binding.PreVideoItemDescription.text = videoInfo.description
        holder.binding.PreVideoItemIsLoveNumber.text = formatLikes(videoInfo.like)
        holder.binding.PreVideoItemIsLove.setImageResource(if (videoInfo.isLike) R.drawable.love4 else R.drawable.love3)
        holder.binding.PreVideoItemIsLove.setOnClickListener {
            onclickLike(videoInfo, position)
        }
    }

    override fun getItemCount(): Int = videoList.size

    private fun formatLikes(likes: Int): String {
        return if (likes > 10000) {
            String.format("%.1fw", likes / 10000.0)
        } else {
            likes.toString()
        }
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

    fun clearVideoList() {
        videoList.clear()
    }
}