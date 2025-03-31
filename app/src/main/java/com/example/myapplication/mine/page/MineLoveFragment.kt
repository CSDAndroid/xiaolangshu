package com.example.myapplication.mine.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.common.adapter.PreVideoViewAdapter
import com.example.myapplication.databinding.MineLovePagerBinding
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.mine.page.viewmodel.MViewModel
import com.example.myapplication.util.SpaceItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineLoveFragment : Fragment() {

    private var _binding: MineLovePagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MViewModel>()

    private var isLoading = false
    private val phone by lazy { viewModel.getPhone() ?: " " }
    private val videoLikeList: MutableList<VideoCardInfo> = mutableListOf()
    private val mAdapter = PreVideoViewAdapter { video, position ->
        toggleLike(video, position)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MineLovePagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
        observeViewModel()
    }

    private fun initRecycleView() {
        binding.mineLoveRecycleView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItem(space = 2))
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && !isLoading) {
                        isLoading = true
                        viewModel.getVideoLikeList(phone)
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            videoLikeList.observe(viewLifecycleOwner) {
                it?.let {
                    mAdapter.addVideoList(it)
                    isLoading = false
                    updateLikeList(it)
                }
            }
        }
    }

    private fun updateLikeList(likeList: List<VideoCardInfo>) {
        if (videoLikeList.isEmpty()) {
            videoLikeList.addAll(likeList)
        } else {
            val exits = videoLikeList.toSet()
            val items = likeList.filter { exits.contains(it) }

            if (items.isNotEmpty()) {
                videoLikeList.addAll(items)
            }
            isLoading = false
        }
    }

    private fun toggleLike(video: VideoCardInfo, position: Int) {
        val newStatus = !video.isLike
        val newLikeNumber = video.like + if (newStatus) 1 else -1
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