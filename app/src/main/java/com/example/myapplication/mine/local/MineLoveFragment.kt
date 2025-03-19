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
import com.example.myapplication.adapter.CommentAdapter
import com.example.myapplication.data.Item
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.databinding.MineLovePagerBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.util.SpaceItem
import com.example.myapplication.viewModel.PictureInfoViewModel
import com.example.myapplication.viewModel.PictureInfoViewModelFactory
import com.example.myapplication.viewModel.VideoInfoViewModel
import com.example.myapplication.viewModel.VideoInfoViewModelFactory

class MineLoveFragment : Fragment(), OnLikeLister {

    private var _binding: MineLovePagerBinding? = null
    private val binding get() = _binding!!

    private var isLoading = false
    private val itemList: MutableList<Item> = mutableListOf()
    private val pictureLikeInfoList: MutableList<Picture1> = mutableListOf()
    private val videoLikeInfoList: MutableList<VideoInfo> = mutableListOf()

    private val mAdapter: CommentAdapter by lazy {
        CommentAdapter(this, itemList, phone, "")
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
        _binding = MineLovePagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPictureLikeList()
        getVideoLikeList()
        initRecycleView()
    }

    override fun onResume() {
        super.onResume()
        getPictureLikeList()
        getVideoLikeList()
        delayInit()
    }

    private fun getPictureLikeList() {
        pictureInfoViewModel.apply {
            pictureLikeList.observe(viewLifecycleOwner) {
                it?.let { updateInfoLikeList(it, pictureLikeInfoList) }
            }
        }.getPictureLikeList(phone)
    }

    private fun getVideoLikeList() {
        videoInfoViewModel.apply {
            videoLikeList.observe(viewLifecycleOwner) {
                it?.let { updateInfoLikeList(it, videoLikeInfoList) }
            }
        }.getVideoLikeList(phone)
    }

    private fun <T : Any> updateInfoLikeList(likeList: List<T>, likeInfoList: MutableList<T>) {
        if (likeInfoList.isEmpty()) {
            likeInfoList.addAll(likeList)
            delayInit()
        } else {
            val exits = likeInfoList.toSet()
            val items = likeList.filter { exits.contains(it) }

            if (items.isNotEmpty()) {
                likeInfoList.addAll(items)
                delayInit()
            }
            isLoading = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun delayInit() {
        if (itemList.isEmpty()) {
            videoLikeInfoList.forEach { itemList.add(Item.Video(it)) }
            pictureLikeInfoList.forEach { itemList.add(Item.Picture(it)) }
            mAdapter.notifyItemRangeInserted(0, itemList.size)
        }
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
                        videoInfoViewModel.getVideoLikeList(phone)
                        pictureInfoViewModel.getPictureLikeList(phone)
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