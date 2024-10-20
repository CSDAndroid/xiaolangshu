package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.databinding.PrePictureItemBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.picture.PictureLoad

class PrePictureViewAdapter(
    private val onLikeLister: OnLikeLister,
    private val pictureList: List<Picture1>,
    private val phone: String
) :
    RecyclerView.Adapter<PrePictureViewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PrePictureItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            PrePictureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pictureInfo = pictureList[position]
        val imageUrl = pictureInfo.picture.trim()
        val avatar = pictureInfo.avatar?.trim()
        val author = pictureInfo.author
        val like = pictureInfo.likes

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.PrePictureItemImg)

        Glide.with(holder.itemView.context)
            .load(avatar)
            .into(holder.binding.PrePictureItemAvatar)

        holder.binding.PrePictureItemNickname.text = author
        holder.binding.PrePictureItemIsLoveNumber.text = like.toString()

        val isLiked = onLikeLister.isLike(pictureInfo, phone)
        holder.binding.PrePictureItemIsLove.setImageResource(if (isLiked) R.drawable.love4 else R.drawable.love3)

        holder.binding.PrePictureItemIsLove.setOnClickListener {
            onLikeLister.onLike(phone, pictureInfo.id)
            if (isLiked) {
                holder.binding.PrePictureItemIsLove.setImageResource(R.drawable.love3)
                pictureInfo.likes -= 1
            } else {
                holder.binding.PrePictureItemIsLove.setImageResource(R.drawable.love4)
                pictureInfo.likes += 1
            }
            notifyItemChanged(position)
        }

        holder.binding.PrePictureItemImg.setOnClickListener {
            val intent = Intent(holder.itemView.context, PictureLoad::class.java)
            intent.putExtra("pictureInfo", pictureList[position])
            holder.itemView.context.startActivities(arrayOf(intent))
        }
    }

    override fun getItemCount() = pictureList.size
}