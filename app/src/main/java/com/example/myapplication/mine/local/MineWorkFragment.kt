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
import com.example.myapplication.databinding.MineWorkPagerBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.util.SpaceItem
import com.example.myapplication.viewModel.PictureInfoViewModel
import com.example.myapplication.viewModel.PictureInfoViewModelFactory
import com.example.myapplication.viewModel.VideoInfoViewModel
import com.example.myapplication.viewModel.VideoInfoViewModelFactory

class MineWorkFragment : Fragment(), OnLikeLister {

    private var _binding: MineWorkPagerBinding? = null
    private val binding get() = _binding!!
    private var isLoading: Boolean = false

    private val pictureWorkInfoList: MutableList<Picture1> = mutableListOf()
    private val pictureLikeInfoList: MutableList<Picture1> = mutableListOf()
    private val videoWorkInfoList: MutableList<VideoInfo> = mutableListOf()
    private val videoLikeInfoList: MutableList<VideoInfo> = mutableListOf()
    private val itemList: MutableList<Item> = mutableListOf()

    private val mAdapter: PrePictureViewAdapter by lazy {
        PrePictureViewAdapter(this, pictureWorkInfoList, phone)
    }

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(requireActivity())
    }

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
        _binding = MineWorkPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView()
        getVideoWorkList()
        getPictureWorkList()
    }

    override fun onResume() {
        super.onResume()
        getVideoWorkList()
        getPictureWorkList()
    }

    private fun getPictureWorkList() {
        pictureInfoViewModel.apply {
            pictureWorkList.observe(viewLifecycleOwner) {
                it?.let { updateInfoList(it, pictureWorkInfoList) }
            }

            pictureLikeList.observe(viewLifecycleOwner) {
                it?.let { updateInfoLikeList(it, pictureLikeInfoList) }
            }
        }

        pictureInfoViewModel.getPictureLikeList(phone)
        pictureInfoViewModel.getPictureWorkList(phone)
    }

    private fun getVideoWorkList() {
        videoInfoViewModel.apply {
            videoWorkList.observe(viewLifecycleOwner) {
                it?.let { updateInfoList(it, videoWorkInfoList) }
            }

            videoLikeList.observe(viewLifecycleOwner) {
                it?.let { updateInfoLikeList(it, videoLikeInfoList) }
            }
        }

        videoInfoViewModel.getVideoLikeList(phone)
        videoInfoViewModel.getVideoWorkList(phone)
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
        if (videoWorkInfoList.isNotEmpty() || pictureWorkInfoList.isNotEmpty()) {
            videoWorkInfoList.forEach { itemList.add(Item.Video(it)) }
            pictureWorkInfoList.forEach { itemList.add(Item.Picture(it)) }
            mAdapter.notifyDataSetChanged()
        }
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
                        pictureInfoViewModel.getPictureWorkList(phone)
                        videoInfoViewModel.getVideoWorkList(phone)
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
        pictureInfoViewModel.setPictureLikeOrNo(phone, id)
    }

    override fun onLike(phone: String, videoInfo: VideoInfo) {
        videoInfoViewModel.setTheVideoToLike(videoInfo, phone)
    }

    override fun isLike(picture1: Picture1, phone: String): Boolean {
        return pictureLikeInfoList.contains(picture1)
    }

    override fun isLike(videoInfo: VideoInfo, phone: String): Boolean {
        return videoLikeInfoList.contains(videoInfo)
    }
}