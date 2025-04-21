package com.example.myapplication.home.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.account.service.UserService
import com.example.myapplication.common.adapter.MAdapter
import com.example.myapplication.databinding.HomePagerBinding
import com.example.myapplication.home.page.HomeFindFragment
import com.example.myapplication.home.page.HomeFocusFragment
import com.example.myapplication.home.page.HomeLocalFragment
import com.example.myapplication.search.page.Search
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: HomePagerBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userService: UserService

    private lateinit var fragments: List<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomePagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigationView()
        initViewPager()
        initRadioGroup()
    }

    private fun initNavigationView() {
        binding.mainPagerNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_backLogin -> {
                    showLogoutDialog()
                    true
                }

                else -> {
                    binding.layoutMain.closeDrawers()
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
        lifecycleScope.launch {
            userService.logout()
        }
    }

    private fun initViewPager() {
        binding.mainPagerViewPager.run {
            fragments = listOf(HomeFocusFragment(), HomeFindFragment(), HomeLocalFragment())
            adapter = MAdapter(childFragmentManager, lifecycle, fragments)
            isUserInputEnabled = true
            currentItem = 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> binding.mainPagerNavRadioGroup.check(binding.mainPagerFocus.id)
                        1 -> binding.mainPagerNavRadioGroup.check(binding.mainPagerFind.id)
                        2 -> binding.mainPagerNavRadioGroup.check(binding.mainPagerRegion.id)
                    }
                }
            })
        }
    }

    private fun initRadioGroup() {
        binding.mainPagerNavRadioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                binding.mainPagerFocus.id -> binding.mainPagerViewPager.currentItem = 0
                binding.mainPagerFind.id -> binding.mainPagerViewPager.currentItem = 1
                binding.mainPagerRegion.id -> binding.mainPagerViewPager.currentItem = 2
            }
        }
        binding.mainPagerNavButton.setOnClickListener {
            binding.layoutMain.openDrawer(GravityCompat.START)
        }
        binding.mainPagerSearch.setOnClickListener {
            val intent = Intent(requireActivity(), Search::class.java)
            requireActivity().startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}