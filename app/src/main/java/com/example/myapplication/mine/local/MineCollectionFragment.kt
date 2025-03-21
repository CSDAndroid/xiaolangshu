package com.example.myapplication.mine.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.PrePictureViewAdapter
import com.example.myapplication.data.Item
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.databinding.MineCollectionPagerBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.util.SpaceItem
import com.example.myapplication.viewModel.PictureInfoViewModel
import com.example.myapplication.viewModel.PictureInfoViewModelFactory
import com.example.myapplication.viewModel.VideoInfoViewModel
import com.example.myapplication.viewModel.VideoInfoViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MineCollectionFragment : Fragment(), OnLikeLister {

    private var _binding: MineCollectionPagerBinding? = null
    private val binding get() = _binding!!
    private var isLoading: Boolean = false

    private val videoCollectionInfoList: MutableList<VideoInfo> = mutableListOf()
    private val videoLikeInfoList: MutableList<VideoInfo> = mutableListOf()
    private val itemList: MutableList<Item> = mutableListOf()

    private lateinit var mAdapter: PrePictureViewAdapter

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    @Inject
    lateinit var database: AppDatabase

    private val pictureInfoViewModel: PictureInfoViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            PictureInfoViewModelFactory(requireActivity(), database)
        )[PictureInfoViewModel::class.java]
    }

    private val videoInfoViewModel: VideoInfoViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            VideoInfoViewModelFactory(requireActivity(), database)
        )[VideoInfoViewModel::class.java]
    }

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
    }

    private fun getVideoCollectionList() {
        videoInfoViewModel.apply {
            videoCollectionList.observe(viewLifecycleOwner) {
                it?.let { updateInfoList(it, videoCollectionInfoList) }
            }

            videoLikeList.observe(viewLifecycleOwner) {
                it?.let { updateInfoLikeList(it, videoLikeInfoList) }
            }
        }

        videoInfoViewModel.getVideoLikeList(phone)
        videoInfoViewModel.getVideoCollectionList(phone)
    }

    private fun <T : Any> updateInfoLikeList(likeList: List<T>, likeInfoList: MutableList<T>) {
        likeInfoList.clear()
        likeInfoList.addAll(likeList)
    }

    private fun <T : Any> updateInfoList(list: List<T>, infoList: MutableList<T>) {
        if (infoList.isEmpty()) {
            infoList.addAll(list)
            delayInit()
        } else {
            val exists = infoList.toSet()
            val newItems = list.filterNot { exists.contains(it) }

            if (newItems.isNotEmpty()) {
                infoList.addAll(list)
                delayInit()
            }
            isLoading = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun delayInit() {
        if (videoCollectionInfoList.isNotEmpty()) {
            videoCollectionInfoList.forEach { itemList.add(Item.Video(it)) }
            mAdapter.notifyDataSetChanged()
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
                        videoInfoViewModel.getVideoCollectionList(phone)
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onLike(phone: String, id: Long) {
        TODO()
    }

    override fun onLike(phone: String, videoInfo: VideoInfo) {
        videoInfoViewModel.setTheVideoToLike(videoInfo, phone)
    }

    override fun isLike(picture1: Picture1, phone: String): Boolean {
        TODO()
    }

    override fun isLike(videoInfo: VideoInfo, phone: String): Boolean {
        return videoLikeInfoList.contains(videoInfo)
    }
}