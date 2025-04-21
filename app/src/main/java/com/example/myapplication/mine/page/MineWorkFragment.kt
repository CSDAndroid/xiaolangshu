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
import com.example.myapplication.common.bean.VideoCardInfo
import com.example.myapplication.databinding.MineWorkPagerBinding
import com.example.myapplication.mine.page.viewmodel.MViewModel
import com.example.myapplication.util.SpaceItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineWorkFragment : Fragment() {

    private var _binding: MineWorkPagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MViewModel>()

    private var isLoading: Boolean = false
    private val phone by lazy { viewModel.getPhone() ?: " " }

    private val mAdapter: PreVideoViewAdapter = PreVideoViewAdapter { video -> toggleLike(video) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MineWorkPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
        observeViewModel()
    }

    private fun initRecycleView() {
        binding.mineWorkRecycleView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItem(space = 2))
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && !isLoading) {
                        isLoading = true
                        viewModel.getVideoPostList(phone)
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        viewModel.videoPostList.observe(viewLifecycleOwner) {
            it?.let {
                mAdapter.addVideoList(it)
                isLoading = false
            }
        }

        viewModel.getVideoPostList(phone)
    }

    private fun toggleLike(video: VideoCardInfo) {
        val newStatus = !video.isLike
        val newLikeNumber = video.like + if (newStatus) 1 else -1
        val newVideo = video.copy(isLike = newStatus, like = newLikeNumber)
        mAdapter.updateVideoList(newVideo)
        viewModel.updatePostVideo(newVideo)

        viewModel.toggleLike(video) { success ->
            if (!success) {
                mAdapter.updateVideoList(video)
                viewModel.updatePostVideo(video)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}