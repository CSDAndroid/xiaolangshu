package com.example.myapplication.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.PreVideoViewAdapter
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.databinding.ActivityAfterSearchBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.util.SpaceItem
import com.example.myapplication.viewModel.VideoInfoViewModel
import com.example.myapplication.viewModel.VideoInfoViewModelFactory

class AfterSearch : AppCompatActivity() ,OnLikeLister {

    private lateinit var binding: ActivityAfterSearchBinding

    private var isLoading: Boolean = false
    private val videoInfoListFromSearch: MutableList<VideoInfo> = mutableListOf()
    private val videoInfoLikeList: MutableList<VideoInfo> = mutableListOf()

    private val key: String by lazy {
        intent.getStringExtra("comment").toString()
    }

    private val mAdapter: PreVideoViewAdapter by lazy {
        PreVideoViewAdapter(this,videoInfoListFromSearch, phone,"search")
    }

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val videoInfoViewModel: VideoInfoViewModel by lazy {
        val database = AppDatabase.getDatabase(this)
        ViewModelProvider(
            this,
            VideoInfoViewModelFactory(this,database)
        )[VideoInfoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        back()
        search()
        initRecycleView()
        getVideoInfoListByKey()
    }

    private fun back() {
        binding.afterSearchBack.setOnClickListener {
            finish()
        }
    }

    private fun search() {
        binding.afterSearchComment.setText(key)

        binding.afterSearchSearch.setOnClickListener {
            videoInfoListFromSearch.clear()
            val key = binding.afterSearchComment.text.toString()
            videoInfoViewModel.getVideoListFromNetWorkByKey(key)
        }
    }

    private fun getVideoInfoListByKey() {
        videoInfoViewModel.apply {
            videoListFromSearch.observe(this@AfterSearch) { videoListFromSearch ->
                videoListFromSearch?.let { updateVideoInfoListFromSearch(videoListFromSearch) }
            }
            videoLikeList.observe(this@AfterSearch) {
                it?.let { updateVideoInfoLikeList(it) }
            }
        }
        videoInfoViewModel.getVideoListFromNetWorkByKey(key)
        videoInfoViewModel.getVideoLikeList(phone)
    }

    private fun updateVideoInfoListFromSearch(videoList: List<VideoInfo>) {
        val currentSize = videoInfoListFromSearch.size
        videoInfoListFromSearch.addAll(videoList)
        mAdapter.notifyItemRangeInserted(currentSize, videoList.size)
        isLoading = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateVideoInfoLikeList(likeList: List<VideoInfo>) {
        videoInfoLikeList.clear()
        videoInfoLikeList.addAll(likeList)
        mAdapter.notifyDataSetChanged()
    }

    private fun initRecycleView() {
        binding.afterSearchRecycleView.run {
            layoutManager = GridLayoutManager(this@AfterSearch, 2)
            addItemDecoration(SpaceItem(2))
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && !isLoading) {
                        isLoading = true
                        videoInfoViewModel.getVideoLikeList(phone)
                        videoInfoViewModel.getVideoListFromNetWorkByKey(key)
                    }
                }
            })
        }
    }

    override fun onLike(phone: String, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onLike(phone: String, videoInfo: VideoInfo) {
        videoInfoViewModel.setTheVideoToLike(videoInfo, phone)
    }

    override fun isLike(picture1: Picture1, phone: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLike(videoInfo: VideoInfo, phone: String): Boolean {
        return videoInfoLikeList.contains(videoInfo)
    }
}