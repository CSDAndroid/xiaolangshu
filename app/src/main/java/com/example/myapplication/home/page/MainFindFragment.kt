package com.example.myapplication.home.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.common.adapter.PreVideoViewAdapter
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.databinding.MainFindPagerBinding
import com.example.myapplication.home.page.viewmodel.HomeViewModel
import com.example.myapplication.util.SpaceItem

class MainFindFragment : Fragment() {

    private var _binding: MainFindPagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private var isLoading = false

    private val mAdapter = PreVideoViewAdapter { video, position ->
        toggleLike(video, position)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFindPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        mAdapter.clearVideoList()
        viewModel.fetchVideos()
        binding.mainFindPagerSwipeRefreshLayout.isRefreshing = false
    }

    private fun observeViewModel() {
        viewModel.videoList.observe(viewLifecycleOwner) {
            mAdapter.addVideoList(it)
            isLoading = false
        }
        viewModel.fetchVideos()
    }

    private fun initRecycleView() {
        binding.mainFindRecycleView.run {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItem(2))
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

    private fun toggleLike(video: VideoCardInfo, position: Int) {
        val newStatus = !video.isLike
        val newLikeNumber = video.like + if (newStatus) +1 else -1
        val newVideo = video.copy(isLike = newStatus, like = newLikeNumber)
        viewModel.updateLikeVideo(newVideo)
        mAdapter.notifyItemChanged(position)

        viewModel.toggleLike(video) { success ->
            if (!success) {
                viewModel.updateLikeVideo(video)
                mAdapter.notifyItemChanged(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}