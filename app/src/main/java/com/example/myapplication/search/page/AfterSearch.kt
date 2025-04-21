package com.example.myapplication.search.page

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.common.adapter.PreVideoViewAdapter
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.databinding.ActivityAfterSearchBinding
import com.example.myapplication.search.SearchViewModel
import com.example.myapplication.util.SpaceItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AfterSearch : AppCompatActivity() {

    private lateinit var binding: ActivityAfterSearchBinding
    private val viewModel by viewModels<SearchViewModel>()

    private var isLoading: Boolean = false
    private var keyword: String =  intent.getStringExtra("keyword").toString()

    private val mAdapter = PreVideoViewAdapter { video ->
        toggleLike(video)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        back()
        search()
        initRecycleView()
        observeViewModel()
    }

    private fun back() {
        binding.afterSearchBack.setOnClickListener { finish() }
    }

    private fun search() {
        binding.afterSearchComment.setText(keyword)
        binding.afterSearchSearch.setOnClickListener {
            mAdapter.clearVideoList()
            keyword = binding.afterSearchComment.text.toString()
            viewModel.fetchSearchVideos(keyword)
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            videoListFromSearch.observe(this@AfterSearch) {
                mAdapter.addVideoList(it)
                isLoading = false
            }
        }
        viewModel.fetchSearchVideos(keyword)
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
                        viewModel.fetchSearchVideos(keyword)
                    }
                }
            })
        }
    }

    private fun toggleLike(video: VideoCardInfo) {
        val newStatus = !video.isLike
        val newLikeNumber = video.like + if (newStatus) +1 else -1
        val newVideo = video.copy(isLike = newStatus, like = newLikeNumber)
        mAdapter.updateVideoList(newVideo)
        viewModel.updateLikeVideo(newVideo)


        viewModel.toggleLike(video) { success ->
            if (!success) {
                mAdapter.updateVideoList(video)
                viewModel.updateLikeVideo(video)
            }
        }
    }
}