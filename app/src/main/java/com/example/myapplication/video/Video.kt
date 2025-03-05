package com.example.myapplication.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.adapter.VideoAdapter
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.database.AccountDatabase
import com.example.myapplication.databinding.ActivityVideoBinding
import com.example.myapplication.lister.OnBackClickListener
import com.example.myapplication.lister.OnCollectionLister
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.viewModel.VideoInfoViewModel
import com.example.myapplication.viewModel.VideoInfoViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Video : AppCompatActivity(), OnBackClickListener, OnLikeLister, OnCollectionLister {

    private lateinit var binding: ActivityVideoBinding
    private val videoPlayList: MutableList<VideoInfo> = mutableListOf()
    private val videoLikeInfoList: MutableList<VideoInfo> = mutableListOf()
    private val videoCollectionInfoList: MutableList<VideoInfo> = mutableListOf()

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val tag: String by lazy {
        intent.getStringExtra("tag").toString()
    }

    private val mAdapter: VideoAdapter by lazy {
        VideoAdapter(this, this, this, videoPlayList, phone)
    }

    private val database: AccountDatabase by lazy {
        AccountDatabase.getDatabase(this)
    }

    private val videoInfoViewModel: VideoInfoViewModel by lazy {
        ViewModelProvider(
            this,
            VideoInfoViewModelFactory(this, database)
        )[VideoInfoViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initViewPager()
        getVideoList()
        getVideoPlayList()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initData() {
        val videoInfo = intent.getSerializableExtra("videoInfo", VideoInfo::class.java)
        if (videoInfo != null) {
            videoPlayList.add(videoInfo)
        }
    }

    private fun getVideoPlayList() {
        if (tag == "random") {
            videoInfoViewModel.videoList.observe(this) {
                it?.let { filterVideoPlayList(it) }
            }
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    videoInfoViewModel.getVideoListFromNetwork()
                }
            }
        } else if (tag == "search") {
            videoInfoViewModel.videoListFromSearch.observe(this) {
                it?.let { filterVideoPlayList(it) }
            }
            val key = intent.getStringExtra("comment").toString()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    videoInfoViewModel.getVideoListFromNetWorkByKey(key)
                }
            }
        }
    }

    private fun getVideoList() {
        videoInfoViewModel.apply {
            videoLikeList.observe(this@Video) {
                Log.d("videoLikeList",it.toString())
                it?.let { updateManyList(it, videoLikeInfoList) }
            }
            videoCollectionList.observe(this@Video) {
                Log.d("videoCollectionList",it.toString())
                it?.let { updateManyList(it, videoCollectionInfoList) }
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                videoInfoViewModel.getVideoLikeList(phone)
                videoInfoViewModel.getVideoCollectionList(phone)
            }
        }
    }

    private fun updateManyList(list: List<VideoInfo>, toList: MutableList<VideoInfo>) {
        toList.clear()
        toList.addAll(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterVideoPlayList(playList: List<VideoInfo>) {
        val firstVideo = videoPlayList.firstOrNull()
        val filteredVideoList = if (firstVideo != null) {
            playList.filter { it != firstVideo }
        } else {
            playList
        }
        videoPlayList.addAll(filteredVideoList)
        mAdapter.notifyDataSetChanged()
    }

    private fun initViewPager() {
        binding.videoViewPager.run {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            adapter = mAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position + 1 == videoPlayList.size && videoPlayList.size != 1) {
                        when (tag) {
                            "random" -> {
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        videoInfoViewModel.getVideoListFromNetwork()
                                    }
                                }
                            }
                            "search" -> {
                                val key = intent.getStringExtra("comment").toString()
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        videoInfoViewModel.getVideoListFromNetWorkByKey(key)
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onBackClicked() {
        finish()
    }

    override fun onLike(phone: String, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onLike(phone: String, videoInfo: VideoInfo) {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                videoInfoViewModel.setTheVideoToLike(videoInfo, phone)
            }
        }
    }

    override fun isLike(picture1: Picture1, phone: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLike(videoInfo: VideoInfo, phone: String): Boolean {
        return videoLikeInfoList.contains(videoInfo)
    }

    override fun onCollection(phone: String, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onCollection(phone: String, videoInfo: VideoInfo) {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                videoInfoViewModel.setTheVideoToCollection(videoInfo, phone)
            }
        }
    }

    override fun isCollection(picture1: Picture1, phone: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCollection(videoInfo: VideoInfo, phone: String): Boolean {
        return videoCollectionInfoList.contains(videoInfo)
    }
}
