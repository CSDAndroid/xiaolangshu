package com.example.myapplication.mine.local

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.PreVideoViewAdapter
import com.example.myapplication.databinding.MineCollectionPagerBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.mine.bean.VideoCardInfo
import com.example.myapplication.mine.local.viewmodel.MViewModel
import com.example.myapplication.util.SpaceItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineCollectionFragment : Fragment(), OnLikeLister {

    private var _binding: MineCollectionPagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MViewModel>()

    private var isLoading: Boolean = false
    private val phone by lazy { viewModel.getPhone() ?: "" }
    private val videoLikeList: MutableList<VideoCardInfo> = mutableListOf()
    private val videoCollectList: MutableList<VideoCardInfo> = mutableListOf()

    private val mAdapter: PreVideoViewAdapter = PreVideoViewAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MineCollectionPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
        observeViewModel()
    }

    private fun initRecycleView() {
        binding.mineCollectionRecycleView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItem(space = 2))
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && !isLoading) {
                        isLoading = true
                        viewModel.getVideoCollectList(phone)
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            videoLikeList.observe(viewLifecycleOwner) {
                it?.let { updateLikeList(it) }
            }

            videoCollectList.observe(viewLifecycleOwner) {
                it?.let {
                    mAdapter.updateVideoList(it)
                    isLoading = false
                    updateCollectList(it)
                }
            }
        }

        viewModel.getVideoLikeList(phone)
        viewModel.getVideoCollectList(phone)
    }

    private fun updateLikeList(likeList: List<VideoCardInfo>) {
        videoLikeList.clear()
        videoLikeList.addAll(likeList)
    }

    private fun updateCollectList(collectList: List<VideoCardInfo>) {
        if (videoCollectList.isEmpty()) {
            videoCollectList.addAll(collectList)
        } else {
            val exists = videoCollectList.toSet()
            val newItems = collectList.filterNot { exists.contains(it) }

            if (newItems.isNotEmpty()) {
                videoCollectList.addAll(collectList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onLike(phone: String, videoInfo: VideoCardInfo) {
        TODO("Not yet implemented")
    }

    override fun isLike(videoInfo: VideoCardInfo, phone: String): Boolean {
        TODO("Not yet implemented")
    }
}