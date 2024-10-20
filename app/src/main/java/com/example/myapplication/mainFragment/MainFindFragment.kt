package com.example.myapplication.mainFragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.PreVideoViewAdapter
import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.database.UserInfoDatabase
import com.example.myapplication.databinding.MainFindPagerBinding
import com.example.myapplication.lister.OnLikeLister
import com.example.myapplication.util.SpaceItem
import com.example.myapplication.viewModel.VideoInfoViewModel
import com.example.myapplication.viewModel.VideoInfoViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFindFragment : Fragment(), OnLikeLister {

    private var _binding: MainFindPagerBinding? = null
    private val binding get() = _binding!!

    private var isLoading = false
    private val videoInfoList = mutableListOf<VideoInfo>()
    private val videoInfoLikeList = mutableListOf<VideoInfo>()

    private val mAdapter: PreVideoViewAdapter by lazy {
        PreVideoViewAdapter(this, videoInfoList, phone, "random")
    }

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null) ?:""
    }

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val videoInfoViewModel: VideoInfoViewModel by lazy {
        val database = UserInfoDatabase.getDatabase(requireActivity())
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
        _binding = MainFindPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView()
        initSwipeRefreshLayout()
        observeVideoInfoList()
        getVideoInfoList()
    }

    private fun initSwipeRefreshLayout() {
        binding.mainFindPagerSwipeRefreshLayout.setOnRefreshListener {
            freshVideoInfoList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun freshVideoInfoList() {
        videoInfoList.clear()
        getVideoInfoList()
        binding.mainFindPagerSwipeRefreshLayout.isRefreshing = false
        mAdapter.notifyDataSetChanged()
    }

    private fun observeVideoInfoList() {
        videoInfoViewModel.apply {
            videoList.observe(viewLifecycleOwner) {
                it?.let { updateVideoInfoList(it) }
            }
            videoLikeList.observe(viewLifecycleOwner) {
                it?.let { updateVideoInfoLikeList(it) }
            }
        }
    }

    private fun getVideoInfoList() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                videoInfoViewModel.getVideoListFromNetwork()
                videoInfoViewModel.getVideoLikeList(phone)
            }
        }
    }

    private fun updateVideoInfoList(videoList: List<VideoInfo>) {
        val currentSize = videoInfoList.size
        videoInfoList.addAll(videoList)
        mAdapter.notifyItemRangeInserted(currentSize, videoList.size)
        isLoading = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateVideoInfoLikeList(likeList: List<VideoInfo>) {
        videoInfoLikeList.clear()
        videoInfoLikeList.addAll(likeList)
        mAdapter.notifyDataSetChanged()
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
                        lifecycleScope.launch() {
                            withContext(Dispatchers.IO) {
                                videoInfoViewModel.getVideoListFromNetwork()
                            }
                        }
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
        TODO("Not yet implemented")
    }

    override fun onLike(phone: String, videoInfo: VideoInfo) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                videoInfoViewModel.setTheVideoToLike(videoInfo, phone)
            }
        }
    }

    override fun isLike(picture1: Picture1, phone: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLike(videoInfo: VideoInfo, phone: String): Boolean {
        return videoInfoLikeList.contains(videoInfo)
    }
}