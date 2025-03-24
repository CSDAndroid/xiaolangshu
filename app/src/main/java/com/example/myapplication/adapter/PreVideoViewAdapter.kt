package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.databinding.PreVideoItemViewBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.util.ImageDealHelper
import com.example.myapplication.video.Video

class PreVideoViewAdapter(
    private val onLikeLister: OnLikeLister,
    private val videoList: List<VideoInfo>,
    private val phone: String,
    private val tag: String
) :
    RecyclerView.Adapter<PreVideoViewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PreVideoItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            PreVideoItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoInfo = videoList[position]
        val imageTempUrl = videoInfo.image
//        val image = ImageDealHelper.convertToHttps(imageTempUrl)

        Glide.with(holder.itemView.context)
            .load(imageTempUrl)
            .into(holder.binding.PreVideoItemImg)

        Glide.with(holder.itemView.context)
            .load(videoInfo.avatar)
            .into(holder.binding.PreVideoItemAvatar)

        holder.binding.PreVideoItemNickname.text = videoInfo.nickname
        holder.binding.PreVideoItemDescription.text = videoInfo.description
        holder.binding.PreVideoItemIsLoveNumber.text = formatLikes(videoInfo.like)

        val isLiked = onLikeLister.isLike(videoInfo, phone)
        holder.binding.PreVideoItemIsLove.setImageResource(if (isLiked) R.drawable.love4 else R.drawable.love3)

        holder.binding.PreVideoItemIsLove.setOnClickListener {
            onLikeLister.onLike(phone, videoInfo)
            if (isLiked) {
                holder.binding.PreVideoItemIsLove.setImageResource(R.drawable.love3)
                videoInfo.like -= 1
            } else {
                holder.binding.PreVideoItemIsLove.setImageResource(R.drawable.love4)
                videoInfo.like += 1
            }
            notifyItemChanged(position)
        }

        holder.binding.PreVideoItemImg.setOnClickListener {
            val intent = Intent(holder.itemView.context, Video::class.java)
            intent.putExtra("videoInfo", videoList[position])
            intent.putExtra("tag", tag)
            holder.itemView.context.startActivities(arrayOf(intent))
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
}