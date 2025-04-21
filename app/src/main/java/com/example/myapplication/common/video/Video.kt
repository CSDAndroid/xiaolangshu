package com.example.myapplication.common.video

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.common.video.adapter.VideoAdapter
import com.example.myapplication.databinding.ActivityVideoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class Video : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private val viewModel by viewModels<VideoInfoViewModel>()

    private lateinit var cache: SimpleCache
    private lateinit var videoAdapter: VideoAdapter

    private val tag = intent.getStringExtra("TAG_SHOW")
    private val keyword = intent.getStringExtra("keyword").toString()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化缓存（100MB）
        val cacheDir = File(cacheDir, "exo_cache")
        cache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024))

        videoAdapter = VideoAdapter(
            context = this,
            cache = cache,
            onClickLike = { video -> toggleLike(video) },
            onClickCollect = { video -> toggleCollect(video) }
        )
        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_scrim)

        getFirstData()
        observeViewModel()
        initViewPager()
    }

    private fun getFirstData() {
        intent.getParcelableExtra<VideoCardInfo>("EXTRA_VIDEO")?.let {
            videoAdapter.addFirstVideo(it)
        }
    }

    private fun observeViewModel() {
        if (tag == "random") {
            viewModel.videoList.observe(this) { it ->
                it?.let { videoAdapter.addVideoList(it) }
            }
            viewModel.fetchVideos()
        } else if (tag == "search") {
            viewModel.videoListFromSearch.observe(this) { it ->
                it?.let { videoAdapter.addVideoList(it) }
            }
            viewModel.fetchVideosBySearch(keyword)
        }
    }

    private fun initViewPager() {
        binding.videoViewPager.apply {
            adapter = videoAdapter
            offscreenPageLimit = 1
            orientation = ViewPager2.ORIENTATION_VERTICAL

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                private var currentPosition = 0

                override fun onPageSelected(position: Int) {
                    videoAdapter.pauseAtPosition(currentPosition)
                    videoAdapter.playAtPosition(position)

                    videoAdapter.preloadPosition(position + 1)
                    videoAdapter.preloadPosition(position - 1)

                    currentPosition = position

                    if (position == videoAdapter.itemCount - 1) {
                        when(tag) {
                            "random" -> viewModel.fetchVideos()
                            "search" -> viewModel.fetchVideosBySearch(keyword)
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        videoAdapter.pauseAtPosition(currentPosition)
                    }
                }
            })
        }
    }

    private fun toggleLike(video: VideoCardInfo) {
        val newStatus = !video.isLike
        val newLikeNumber = video.like + if (newStatus) +1 else -1
        val newVideo = video.copy(isLike = newStatus, like = newLikeNumber)
        videoAdapter.updateVideoList(newVideo)
        viewModel.updateVideo(newVideo)

        viewModel.toggleLike(video) { success ->
            if (!success) {
                videoAdapter.updateVideoList(video)
                viewModel.updateVideo(video)
            }
        }
    }

    private fun toggleCollect(video: VideoCardInfo) {
        val newStatus = !video.isCollect
        val newLikeNumber = video.collection + if (newStatus) +1 else -1
        val newVideo = video.copy(isCollect = newStatus, collection = newLikeNumber)

        videoAdapter.updateVideoList(newVideo)
        viewModel.updateVideo(newVideo)

        viewModel.toggleCollect(video) { success ->
            if (!success) {
                videoAdapter.updateVideoList(video)
                viewModel.updateVideo(video)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoAdapter.pauseAll()
    }

    override fun onResume() {
        super.onResume()
        binding.videoViewPager.currentItem.let {
            videoAdapter.resumeCurrent(it)
        }
    }

    @UnstableApi
    override fun onDestroy() {
        super.onDestroy()
        videoAdapter.release()
        cache.release()
    }
}