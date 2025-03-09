package com.example.myapplication.mineFragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapter.MAdapter
import com.example.myapplication.storage.db.entity.Account
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.databinding.MinePagerBinding
import com.example.myapplication.account.op.OP
import com.example.myapplication.share.Load
import com.example.myapplication.util.ImageDealHelper
import com.example.myapplication.viewModel.UserInfoViewModel
import com.example.myapplication.viewModel.UserInfoViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MineFragment : Fragment() {

    private var _binding: MinePagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragments: List<Fragment>

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val userViewModel: UserInfoViewModel by lazy {
        val database = AppDatabase.getDatabase(requireActivity())
        ViewModelProvider(
            requireActivity(),
            UserInfoViewModelFactory(database)
        )[UserInfoViewModel::class.java]
    }

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
        toEditUserInfo()
        toLoadWork()
        initRadioGroup()
        initViewPager()
        getUserInfo()
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
        with(sharedPreferences.edit()) {
            putBoolean("isLogin", false)
            putString("phone", null)
            apply()
        }
        startActivity(Intent(requireContext(), OP::class.java))
        requireActivity().finish()
    }

    private fun getUserInfo() {
        userViewModel.apply {
            user.observe(viewLifecycleOwner) { user ->
                user?.let { setUserInfoToView(it) }
            }
        }.getUserInfoFromNetwork(phone)
    }

    private fun refreshUserInfoToView() {
        val currentItem = binding.mineViewPager.currentItem
        userViewModel.getUserInfoFromNetwork(phone)
        initViewPager()
        binding.mineSwipeRefreshLayout.isRefreshing = false
        binding.mineViewPager.currentItem = currentItem
    }

    private fun setUserInfoToView(account: Account) {
        account.background?.let {
            Glide.with(requireContext()).load(it).into(binding.mineBackgroundImage)
        }
        account.avatar?.let {
            Glide.with(requireContext()).load(it).into(binding.mineAvatar)
        }
        binding.mineIntroduction.text = account.introduction ?: ""
        binding.mineNickname.text = account.nickname

        setupImagePicker(account)
    }

    private fun setupImagePicker(account: Account) {
        ImageDealHelper.initImagePicker(
            this,
            onImageSelected = { _, _ -> },
            onImageCropped = { uri, selectedTag ->
                handleImageCrop(uri, selectedTag, account)
            }
        )

        binding.mineBackgroundImage.setOnClickListener {
            ImageDealHelper.openGallery("backgroundImage")
        }

        binding.mineAvatar.setOnClickListener {
            ImageDealHelper.openGallery("avatar")
        }
    }

    private fun handleImageCrop(uri: Uri, selectedTag: String, account: Account) {
        when (selectedTag) {
            "avatar" -> binding.mineAvatar.setImageURI(uri)
            "backgroundImage" -> binding.mineBackgroundImage.setImageURI(uri)
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userViewModel.uploadImageToNetwork(requireContext(), uri, selectedTag, phone, null)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            account.nickname?.let {
                userViewModel.updateUserInfoToRoom(
                    account.introduction,
                    account.birthday,
                    account.sex,
                    it,
                    account.career,
                    account.phone,
                    account.region,
                    account.school,
                    null
                )
            }
        }, 1000)
    }

    private fun toEditUserInfo() {
        binding.mineEditUserInfo.setOnClickListener {
            val intent = Intent(requireContext(), UserInfoEdit::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}