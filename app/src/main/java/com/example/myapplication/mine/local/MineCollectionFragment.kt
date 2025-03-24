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
import com.example.myapplication.mine.bean.VideoCardInfo
import com.example.myapplication.mine.local.viewmodel.MViewModel
import com.example.myapplication.util.SpaceItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineCollectionFragment : Fragment() {

    private var _binding: MineCollectionPagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MViewModel>()

    private var isLoading: Boolean = false
    private val phone by lazy { viewModel.getPhone() ?: "" }
    private val videoLikeList: MutableList<VideoCardInfo> = mutableListOf()
    private val videoCollectList: MutableList<VideoCardInfo> = mutableListOf()

    private lateinit var mAdapter: PreVideoViewAdapter

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
        observeViewModel()
        initRecycleView()
    }

    private fun observeViewModel() {
        viewModel.apply {
            videoLikeList.observe(viewLifecycleOwner) {
                it?.let { updateLikeList(it) }
            }

            videoCollectList.observe(viewLifecycleOwner) {
                it?.let { updateCollectList(it) }
            }
        }

        viewModel.getVideoLikeList(phone)
        viewModel.getVideoCollectList(phone)
    }

    private fun updateLikeList(likeList: List<VideoCardInfo>) {
        videoLikeList.clear()
        videoLikeList.addAll(likeList)
    }

    private fun updateCollectList(list: List<VideoCardInfo>) {
        if (videoCollectList.isEmpty()) {
            videoCollectList.addAll(list)
        } else {
            val exists = videoCollectList.toSet()
            val newItems = list.filterNot { exists.contains(it) }

            if (newItems.isNotEmpty()) {
                videoCollectList.addAll(list)
            }
            isLoading = false
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}