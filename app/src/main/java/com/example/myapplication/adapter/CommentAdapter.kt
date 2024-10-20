package com.example.myapplication.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.Item
import com.example.myapplication.databinding.PrePictureItemBinding
import com.example.myapplication.databinding.PreVideoItemViewBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.picture.PictureLoad
import com.example.myapplication.util.ImageDealHelper
import com.example.myapplication.video.Video

class CommentAdapter(
    private val onLikeLister: OnLikeLister,
    private val itemList: List<Item>,
    private val phone: String,
    private val tag: String
) :
    RecyclerView.Adapter<ViewHolder>() {

    class VideoViewHolder(val binding: PreVideoItemViewBinding) : ViewHolder(binding.root)
    class PictureViewHolder(val binding: PrePictureItemBinding) : ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is Item.Video -> 0
            is Item.Picture -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 0) {
            val binding =
                PreVideoItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VideoViewHolder(binding)
        } else {
            val binding =
                PrePictureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PictureViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = itemList[position]) {
            is Item.Video -> {
                Log.d("item",item.toString())
                val videoHolder = holder as VideoViewHolder
                val imageTempUrl = item.video.image
                val image = ImageDealHelper.convertToHttps(imageTempUrl)

                Glide.with(videoHolder.itemView.context).load(image)
                    .into(videoHolder.binding.PreVideoItemImg)
                Glide.with(videoHolder.itemView.context).load(item.video.avatar)
                    .into(videoHolder.binding.PreVideoItemAvatar)
                videoHolder.binding.PreVideoItemDescription.text = item.video.description
                videoHolder.binding.PreVideoItemNickname.text = item.video.nickname
                videoHolder.binding.PreVideoItemIsLoveNumber.text = item.video.like.toString()

                val isLiked = onLikeLister.isLike(item.video, phone)
                videoHolder.binding.PreVideoItemIsLove.setImageResource(if (isLiked) R.drawable.love4 else R.drawable.love3)

                videoHolder.binding.PreVideoItemIsLove.setOnClickListener {
                    onLikeLister.onLike(phone, item.video)
                    if (isLiked) {
                        videoHolder.binding.PreVideoItemIsLove.setImageResource(R.drawable.love3)
                        item.video.like -= 1
                    } else {
                        videoHolder.binding.PreVideoItemIsLove.setImageResource(R.drawable.love4)
                        item.video.like += 1
                    }
                    notifyItemChanged(position)
                }

                videoHolder.binding.PreVideoItemImg.setOnClickListener {
                    val intent = Intent(videoHolder.itemView.context, Video::class.java)
                    intent.putExtra("videoInfo", item.video)
                    intent.putExtra("tag", tag)
                    videoHolder.itemView.context.startActivities(arrayOf(intent))
                }
            }

            is Item.Picture -> {
                Log.d("item",item.toString())
                val pictureHolder = holder as PictureViewHolder

                Glide.with(pictureHolder.itemView.context).load(item.picture.picture.trim())
                    .into(pictureHolder.binding.PrePictureItemImg)
                Glide.with(pictureHolder.itemView.context).load(item.picture.avatar?.trim())
                    .into(pictureHolder.binding.PrePictureItemAvatar)
                pictureHolder.binding.PrePictureItemNickname.text = item.picture.author
                pictureHolder.binding.PrePictureItemIsLoveNumber.text =
                    item.picture.likes.toString()

                val isLiked = onLikeLister.isLike(item.picture, phone)
                pictureHolder.binding.PrePictureItemIsLove.setImageResource(if (isLiked) R.drawable.love4 else R.drawable.love3)

                pictureHolder.binding.PrePictureItemIsLove.setOnClickListener {
                    onLikeLister.onLike(phone, item.picture.id)
                    if (isLiked) {
                        pictureHolder.binding.PrePictureItemIsLove.setImageResource(R.drawable.love3)
                        item.picture.likes -= 1
                    } else {
                        pictureHolder.binding.PrePictureItemIsLove.setImageResource(R.drawable.love4)
                        item.picture.likes += 1
                    }
                    notifyItemChanged(position)
                }

                pictureHolder.binding.PrePictureItemImg.setOnClickListener {
                    val intent = Intent(pictureHolder.itemView.context, PictureLoad::class.java)
                    intent.putExtra("pictureInfo", item.picture)
                    pictureHolder.itemView.context.startActivities(arrayOf(intent))
                }
            }
        }
    }

    override fun getItemCount(): Int = itemList.size
}