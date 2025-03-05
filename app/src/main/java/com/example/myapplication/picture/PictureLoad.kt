package com.example.myapplication.picture

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.adapter.PictureAdapter
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.database.AccountDatabase
import com.example.myapplication.databinding.ActivityPictureBinding
import com.example.myapplication.lister.ImageDownLoadCallBack
import com.example.myapplication.lister.OnBackClickListener
import com.example.myapplication.lister.OnCollectionLister
import com.example.myapplication.lister.OnFocusLister
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.viewModel.PictureInfoViewModel
import com.example.myapplication.viewModel.PictureInfoViewModelFactory

class PictureLoad : AppCompatActivity(), OnBackClickListener, OnLikeLister, OnCollectionLister,
    OnFocusLister, ImageDownLoadCallBack {

    private lateinit var binding: ActivityPictureBinding
    private val focusPhoneInfoList = mutableListOf<String>()
    private val picturePlayList = mutableListOf<Picture1>()
    private val pictureLikeInfoList = mutableListOf<Picture1>()
    private val pictureCollectionInfoList = mutableListOf<Picture1>()

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val database: AccountDatabase by lazy {
        AccountDatabase.getDatabase(this)
    }

    private val mAdapter: PictureAdapter by lazy {
        PictureAdapter(
            this, this, this, this,
            picturePlayList, phone
        )
    }

    private val pictureInfoViewModel: PictureInfoViewModel by lazy {
        ViewModelProvider(
            this,
            PictureInfoViewModelFactory(this, database)
        )[PictureInfoViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initViewPager()
        getPicturePlayList()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initData() {
        val pictureInfo = intent.getSerializableExtra("pictureInfo", Picture1::class.java)
        if (pictureInfo != null) {
            picturePlayList.add(pictureInfo)
        }
    }

    private fun initViewPager() {
        binding.pictureViewPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            adapter = mAdapter
        }
    }

    private fun getPicturePlayList() {
        pictureInfoViewModel.apply {
            pictureList.observe(this@PictureLoad) {
                it?.let { filterPicturePlayList(it) }
            }
            pictureLikeList.observe(this@PictureLoad) {
                it?.let { updateManyList(it, pictureLikeInfoList) }
            }
            pictureCollectionList.observe(this@PictureLoad) {
                it?.let { updateManyList(it, pictureCollectionInfoList) }
            }
            focusPhoneList.observe(this@PictureLoad) {
                it?.let { updateManyList(it, focusPhoneInfoList) }
            }
        }

        pictureInfoViewModel.getFocusList(phone)
        pictureInfoViewModel.getPictureLikeList(phone)
        pictureInfoViewModel.getPictureCollectionList(phone)
        pictureInfoViewModel.getPictureListFromNetWork()
    }

    private fun <T : Any> updateManyList(list: List<T>, toList: MutableList<T>) {
        toList.clear()
        toList.addAll(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterPicturePlayList(playList: List<Picture1>) {
        val firstVideo = picturePlayList.firstOrNull()
        val filteredVideoList = if (firstVideo != null) {
            playList.filter { it != firstVideo }
        } else {
            playList
        }
        picturePlayList.addAll(filteredVideoList)
        mAdapter.notifyDataSetChanged()
    }

    override fun onBackClicked() {
        finish()
    }

    override fun onLike(phone: String, id: Long) {
        pictureInfoViewModel.setPictureLikeOrNo(phone, id)
    }

    override fun onLike(phone: String, videoInfo: VideoInfo) {
        TODO("Not yet implemented")
    }

    override fun onCollection(phone: String, id: Long) {
        pictureInfoViewModel.setPictureCollectionOrNo(phone, id)
    }

    override fun onCollection(phone: String, videoInfo: VideoInfo) {
        TODO("Not yet implemented")
    }

    override fun isLike(picture1: Picture1, phone: String): Boolean {
        return pictureLikeInfoList.contains(picture1)
    }

    override fun isLike(videoInfo: VideoInfo, phone: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCollection(picture1: Picture1, phone: String): Boolean {
        return pictureCollectionInfoList.contains(picture1)
    }

    override fun isCollection(videoInfo: VideoInfo, phone: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun onFocus(followPhone: String, phone: String) {
        pictureInfoViewModel.setAuthorFocusOrNo(followPhone, phone)
    }

    override fun isFocus(followPhone: String, phone: String): Boolean {
        return focusPhoneInfoList.contains(followPhone)
    }

    override fun onDownLoadSuccess(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDownLoadFailed(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
