package com.example.myapplication.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.databinding.PictureItemViewBinding
import com.example.myapplication.lister.ImageDownLoadCallBack
import com.example.myapplication.lister.OnBackClickListener
import com.example.myapplication.lister.OnCollectionLister
import com.example.myapplication.lister.OnFocusLister
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.util.ImageDownloader

class PictureAdapter(
    private val onBackClickListener: OnBackClickListener,
    private val onLikeLister: OnLikeLister,
    private val onCollectionLister: OnCollectionLister,
    private val onFocusLister: OnFocusLister,
    private val pictureList: List<Picture1>,
    private val phone: String
) :
    RecyclerView.Adapter<PictureAdapter.ViewHolder>() {

    private val imageDownloader = ImageDownloader()

    inner class ViewHolder(val binding: PictureItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PictureItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (pictureList.isNotEmpty()) {
            val pictureInfo = pictureList[position]
            var isFocus: Boolean
            var isLike: Boolean
            var isCollection: Boolean

            Glide.with(holder.itemView.context).load(pictureInfo.picture.trim())
                .into(holder.binding.pictureItemImageView)
            Glide.with(holder.itemView.context).load(pictureInfo.avatar?.trim())
                .into(holder.binding.pictureItemAvatar)

            holder.binding.pictureItemNickname.text = pictureInfo.author
            holder.binding.pictureItemKeyWord.text = pictureInfo.description
            holder.binding.pictureItemLoveNumber.text = pictureInfo.likes.toString()
            holder.binding.pictureItemCollectionNumber.text = pictureInfo.collections.toString()

            holder.binding.pictureItemImageView.setOnLongClickListener {
                val imageUrl = pictureInfo.picture.trim()
                val builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("确认下载")
                builder.setMessage("您确定要下载这张图片吗？")

                builder.setPositiveButton("是") { dialog, _ ->
                    downloadImage(holder.itemView.context, imageUrl)
                    dialog.dismiss()
                }
                builder.setNegativeButton("否") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
                true
            }

            if (phone == pictureInfo.phone) {
                holder.binding.pictureItemFocus.visibility = View.INVISIBLE
            }

            isFocus = if (!onFocusLister.isFocus(pictureInfo.phone, phone)) {
                holder.binding.pictureItemFocus.text = "关注"
                holder.binding.pictureItemFocus.setBackgroundResource(R.drawable.shape4)
                false
            } else {
                holder.binding.pictureItemFocus.text = "已关注"
                holder.binding.pictureItemFocus.setBackgroundResource(R.drawable.shape6)
                true
            }

            holder.binding.pictureItemFocus.setOnClickListener {
                onFocusLister.onFocus(pictureInfo.phone, phone)
                if (isFocus) {
                    holder.binding.pictureItemFocus.text = "关注"
                    holder.binding.pictureItemFocus.setBackgroundResource(R.drawable.shape4)
                } else {
                    holder.binding.pictureItemFocus.text = "已关注"
                    holder.binding.pictureItemFocus.setBackgroundResource(R.drawable.shape6)
                }
                isFocus = !isFocus
            }

            isLike = if (!onLikeLister.isLike(pictureInfo, phone)) {
                holder.binding.pictureItemIconLove.setImageResource(R.drawable.love2)
                false
            } else {
                holder.binding.pictureItemIconLove.setImageResource(R.drawable.love4)
                true
            }

            holder.binding.pictureItemIconLove.setOnClickListener {
                onLikeLister.onLike(phone, pictureInfo.id)
                if (isLike) {
                    holder.binding.pictureItemIconLove.setImageResource(R.drawable.love2)
                    pictureInfo.likes -= 1
                    holder.binding.pictureItemLoveNumber.text = pictureInfo.likes.toString()
                } else {
                    holder.binding.pictureItemIconLove.setImageResource(R.drawable.love4)
                    pictureInfo.likes += 1
                    holder.binding.pictureItemLoveNumber.text = pictureInfo.likes.toString()
                }
                isLike = !isLike
            }

            isCollection = if (!onCollectionLister.isCollection(pictureInfo, phone)) {
                holder.binding.pictureItemIconCollection.setImageResource(R.drawable.collection)
                false
            } else {
                holder.binding.pictureItemIconCollection.setImageResource(R.drawable.collection1)
                true
            }

            holder.binding.pictureItemIconCollection.setOnClickListener {
                onCollectionLister.onCollection(phone, pictureInfo.id)
                if (isCollection) {
                    holder.binding.pictureItemIconCollection.setImageResource(R.drawable.collection)
                    pictureInfo.collections -= 1
                    holder.binding.pictureItemCollectionNumber.text =
                        pictureInfo.collections.toString()
                } else {
                    holder.binding.pictureItemIconCollection.setImageResource(R.drawable.collection1)
                    pictureInfo.collections += 1
                    holder.binding.pictureItemCollectionNumber.text =
                        pictureInfo.collections.toString()
                }
                isCollection = !isCollection
            }
        }

        holder.binding.pictureItemBack.setOnClickListener {
            onBackClickListener.onBackClicked()
        }
    }

    override fun getItemCount(): Int = pictureList.size

    private fun downloadImage(context: Context, url: String) {
        imageDownloader.startDownload(context, url, object : ImageDownLoadCallBack {
            override fun onDownLoadSuccess(text: String) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }

            override fun onDownLoadFailed(text: String) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
