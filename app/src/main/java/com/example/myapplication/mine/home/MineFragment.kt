package com.example.myapplication.mine.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.account.userProfile.UserProfileEdit
import com.example.myapplication.common.adapter.MAdapter
import com.example.myapplication.databinding.MinePagerBinding
import com.example.myapplication.mine.page.MineCollectionFragment
import com.example.myapplication.mine.page.MineLoveFragment
import com.example.myapplication.mine.page.MineWorkFragment
import com.example.myapplication.post.Load
import com.example.myapplication.storage.db.entity.Account
import com.example.myapplication.util.ImageDealHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MineFragment : Fragment() {

    private var _binding: MinePagerBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var imageDealHelper: ImageDealHelper

    private val viewModel by viewModels<MineViewModel>()
    private val phone: String by lazy { viewModel.getPhone() ?: "" }

    private lateinit var fragments: List<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MinePagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwipeRefreshLayout()
        initNavigationView()
        toEditUserProfile()
        toLoadWork()
        initRadioGroup()
        initViewPager()
        getUserProFile()
    }

    private fun initSwipeRefreshLayout() {
        binding.mineSwipeRefreshLayout.setOnRefreshListener {
            refreshUserInfoToView()
        }
    }

    private fun initNavigationView() {
        binding.mineNavButton.setOnClickListener {
            binding.layoutMine.openDrawer(GravityCompat.START)
        }
        binding.minePagerNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_backLogin -> {
                    showLogoutDialog()
                    true
                }

                else -> {
                    binding.layoutMine.closeDrawers()
                    true
                }
            }
        }
    }

    private fun toEditUserProfile() {
        binding.mineEditUserProfile.setOnClickListener {
            val intent = Intent(requireContext(), UserProfileEdit::class.java)
            startActivity(intent)
        }
    }

    private fun toLoadWork() {
        binding.mineMakeVideo.setOnClickListener {
            val intent = Intent(requireContext(), Load::class.java)
            startActivity(intent)
        }
    }

    private fun initRadioGroup() {
        binding.mineNavRadioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                binding.mineWork.id -> binding.mineViewPager.currentItem = 0
                binding.mineCollection.id -> binding.mineViewPager.currentItem = 1
                binding.mineLove.id -> binding.mineViewPager.currentItem = 2
            }
        }
    }

    private fun initViewPager() {
        binding.mineViewPager.run {
            fragments = listOf(MineWorkFragment(), MineCollectionFragment(), MineLoveFragment())
            adapter = MAdapter(childFragmentManager, lifecycle, fragments)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> binding.mineNavRadioGroup.check(binding.mineWork.id)
                        1 -> binding.mineNavRadioGroup.check(binding.mineCollection.id)
                        2 -> binding.mineNavRadioGroup.check(binding.mineLove.id)
                    }
                }
            })
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("尊敬的用户")
            .setMessage("您要退出登录吗？")
            .setPositiveButton("是") { _, _ -> logout() }
            .setNegativeButton("否", null)
            .create()
            .show()
    }

    private fun logout() {
        viewModel.logout()
    }

    private fun getUserProFile() {
        viewModel.userProfile.observe(viewLifecycleOwner) { it ->
            it?.let { setUI(it) }
        }
        viewModel.getUserProfile(phone)
    }

    private fun refreshUserInfoToView() {
        val currentItem = binding.mineViewPager.currentItem
        viewModel.getUserProfile(phone)
        binding.mineSwipeRefreshLayout.isRefreshing = false
        binding.mineViewPager.currentItem = currentItem
    }

    private fun setUI(account: Account) {
        account.background?.let {
            Glide.with(requireContext()).load(it).into(binding.mineBackgroundImage)
        }
        account.avatar?.let {
            Glide.with(requireContext()).load(it).into(binding.mineAvatar)
        }
        binding.mineIntroduction.text = account.introduction ?: ""
        binding.mineNickname.text = account.nickname

        setupImagePicker()
    }

    private fun setupImagePicker() {
        imageDealHelper.initImagePicker(
            this,
            onImageSelected = { _, _ -> },
            onImageCropped = { uri, selectedTag ->
                handleImageCrop(uri, selectedTag)
            }
        )

        binding.mineAvatar.setOnClickListener {
            imageDealHelper.openGallery("avatar")
        }

        binding.mineBackgroundImage.setOnClickListener {
            imageDealHelper.openGallery("backgroundImage")
        }
    }

    private fun handleImageCrop(uri: Uri, selectedTag: String) {
        when (selectedTag) {
            "avatar" -> {
                viewModel.updateUserAvatar(uri.toString(), phone)
                binding.mineAvatar.setImageURI(uri)
            }

            "backgroundImage" -> {
                viewModel.updateUserBackground(uri.toString(), phone)
                binding.mineBackgroundImage.setImageURI(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}