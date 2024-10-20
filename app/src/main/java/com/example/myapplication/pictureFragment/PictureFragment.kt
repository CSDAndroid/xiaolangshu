package com.example.myapplication.pictureFragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.database.UserInfoDatabase
import com.example.myapplication.databinding.PicturePagerBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.search.Search
import com.example.myapplication.util.SpaceItem
import com.example.myapplication.viewModel.PictureInfoViewModel
import com.example.myapplication.viewModel.PictureInfoViewModelFactory

class PictureFragment : Fragment(), OnLikeLister {

    private var _binding: PicturePagerBinding? = null
    private val binding get() = _binding!!

    private var isLoading = false
    private val pictureInfoList = mutableListOf<Picture1>()
    private val pictureInfoLikeList = mutableListOf<Picture1>()

    private val mAdapter: PrePictureViewAdapter by lazy {
        PrePictureViewAdapter(this, pictureInfoList, phone)
    }

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val pictureInfoViewModel: PictureInfoViewModel by lazy {
        val database = UserInfoDatabase.getDatabase(requireActivity())
        ViewModelProvider(
            requireActivity(),
            PictureInfoViewModelFactory(requireActivity(), database)
        )[PictureInfoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PicturePagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearch()
        initRecycleView()
        initSwipeRefreshLayout()
        getPictureList()
    }

    private fun initSearch() {
        binding.picturePagerSearch.setOnClickListener {
            val intent = Intent(requireContext(), Search::class.java)
            startActivity(intent)
        }
    }

    private fun initSwipeRefreshLayout() {
        binding.picturePagerSwipeRefreshLayout.setOnRefreshListener {
            refreshPictureList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshPictureList() {
        pictureInfoList.clear()
        getPictureList()
        binding.picturePagerSwipeRefreshLayout.isRefreshing = false
        mAdapter.notifyDataSetChanged()
    }

    private fun getPictureList() {
        pictureInfoViewModel.apply {
            pictureList.observe(viewLifecycleOwner) {
                it?.let { updatePictureInfoList(it) }
            }
            pictureLikeList.observe(viewLifecycleOwner) {
                it?.let { updatePictureLikeInfoList(it) }
            }
        }
        pictureInfoViewModel.getPictureListFromNetWork()
        pictureInfoViewModel.getPictureLikeList(phone)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updatePictureLikeInfoList(pictureLikeList: List<Picture1>) {
        pictureInfoLikeList.clear()
        pictureInfoLikeList.addAll(pictureLikeList)
        mAdapter.notifyDataSetChanged()
    }

    private fun updatePictureInfoList(pictureList: List<Picture1>) {
        if (pictureInfoList.isEmpty()) {
            pictureInfoList.addAll(pictureList)
            mAdapter.notifyItemRangeInserted(0, pictureInfoList.size)
        } else {
            val pictures = pictureInfoList.toSet()
            val picture = pictureList.filterNot { pictures.contains(it) }
            if (picture.isNotEmpty()) {
                val currentSize = pictureInfoList.size
                pictureInfoList.addAll(picture)
                mAdapter.notifyItemRangeInserted(currentSize, picture.size)
            }
            isLoading = false
        }
    }

    private fun initRecycleView() {
        binding.picturePagerRecycleView.run {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItem(2))
            adapter = mAdapter
            isLoading = false
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && !isLoading) {
                        isLoading = true
                        pictureInfoViewModel.getPictureListFromNetWork()
                    }
                }
            })
        }
    }

    override fun onLike(phone: String, id: Long) {
        pictureInfoViewModel.setPictureLikeOrNo(phone, id)
    }

    override fun onLike(phone: String, videoInfo: VideoInfo) {
        TODO("Not yet implemented")
    }

    override fun isLike(picture1: Picture1, phone: String): Boolean {
        return pictureInfoLikeList.contains(picture1)
    }

    override fun isLike(videoInfo: VideoInfo, phone: String): Boolean {
        TODO("Not yet implemented")
    }
}