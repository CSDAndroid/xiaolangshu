package com.example.myapplication.common.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.VideoItemViewBinding
import com.example.myapplication.lister.OnBackClickListener
import com.example.myapplication.lister.OnCollectionLister
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.common.bean.VideoCardInfo

open class VideoAdapter(
    private val onLikeLister: OnLikeLister,
    private val onCollectionLister: OnCollectionLister,
    private val onBackClickListener: OnBackClickListener,
    private val videoList: MutableList<VideoCardInfo>,
    private val phone: String
) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: VideoItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            VideoItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoInfo = videoList[position]
        var isLike: Boolean
        var isCollection: Boolean
        val aid = videoInfo.aid
        val cid = videoInfo.cid
        val videoUrl = "https://player.bilibili.com/player.html?aid=$aid&cid=$cid&page=1"

        holder.binding.videoItemBack.setOnClickListener {
            onBackClickListener.onBackClicked()
        }

        Glide.with(holder.itemView.context).load(videoInfo.avatar).into(holder.binding.videoItemAvatar)

        holder.binding.videoItemNickname.text = videoInfo.nickname
        holder.binding.videoItemDescription.text = videoInfo.description
        holder.binding.videoItemLoveNumber.text = formatLikes(videoInfo.like)
        holder.binding.videoItemCollectionNumber.text = formatLikes(videoInfo.collection)

        holder.binding.videoItemVideoView.apply {
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val url = request.url.toString()
                    view.loadUrl(url)
                    return true
                }
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                }
            }
            loadUrl(videoUrl)
        }

        isLike = if (!onLikeLister.isLike(videoInfo, phone)) {
            holder.binding.videoItemIconLove.setImageResource(R.drawable.love2)
            false
        } else {
            holder.binding.videoItemIconLove.setImageResource(R.drawable.love4)
            true
        }

        holder.binding.videoItemIconLove.setOnClickListener {
            onLikeLister.onLike(phone, videoInfo)
            if (isLike) {
                holder.binding.videoItemIconLove.setImageResource(R.drawable.love2)
                videoInfo.like -= 1
                holder.binding.videoItemLoveNumber.text = formatLikes(videoInfo.like)
            } else {
                holder.binding.videoItemIconLove.setImageResource(R.drawable.love4)
                videoInfo.like += 1
                holder.binding.videoItemLoveNumber.text = formatLikes(videoInfo.like)
            }
            isLike = !isLike
        }

        isCollection = if (!onCollectionLister.isCollection(videoInfo, phone)) {
            holder.binding.videoItemIconCollection.setImageResource(R.drawable.collection)
            false
        } else {
            holder.binding.videoItemIconCollection.setImageResource(R.drawable.collection1)
            true
        }

        holder.binding.videoItemIconCollection.setOnClickListener {
            onCollectionLister.onCollection(phone, videoInfo)
            if (isCollection) {
                holder.binding.videoItemIconCollection.setImageResource(R.drawable.collection)
                videoInfo.collection -= 1
                holder.binding.videoItemCollectionNumber.text = formatLikes(videoInfo.collection)
            } else {
                holder.binding.videoItemIconCollection.setImageResource(R.drawable.collection1)
                videoInfo.collection += 1
                holder.binding.videoItemCollectionNumber.text = formatLikes(videoInfo.collection)
            }
            isCollection = !isCollection
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
