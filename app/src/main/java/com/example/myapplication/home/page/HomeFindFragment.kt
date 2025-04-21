package com.example.myapplication.home.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.common.adapter.PreVideoViewAdapter
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.databinding.HomeFindPagerBinding
import com.example.myapplication.home.page.viewmodel.HomeViewModel
import com.example.myapplication.util.SpaceItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFindFragment : Fragment() {

    private var _binding: HomeFindPagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private var isLoading = false

    private val mAdapter = PreVideoViewAdapter { video -> toggleLike(video) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFindPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.md_theme_surfaceContainer)

        initSwipeRefreshLayout()
        initRecycleView()
        observeViewModel()
    }

    private fun initSwipeRefreshLayout() {
        binding.mainFindPagerSwipeRefreshLayout.setOnRefreshListener {
            freshVideoInfoList()
        }
    }

    private fun freshVideoInfoList() {
        viewModel.clearVideo()
        mAdapter.clearVideoList()
        viewModel.fetchVideos()
        binding.mainFindPagerSwipeRefreshLayout.isRefreshing = false
    }

    private fun observeViewModel() {
        viewModel.videoList.observe(viewLifecycleOwner) {
            it?.let {
                mAdapter.addVideoList(it)
                isLoading = false
                Log.d("videoList", it.toString())
            }
        }
        viewModel.fetchVideos()
    }

    private fun initRecycleView() {
        binding.mainFindRecycleView.run {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItem(16))
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && !isLoading) {
                        isLoading = true
                        viewModel.fetchVideos()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}