package com.example.myapplication.mainFragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.adapter.MAdapter
import com.example.myapplication.databinding.MainPagerBinding
import com.example.myapplication.account.op.OP
import com.example.myapplication.search.Search

class MainFragment : Fragment() {

    private var _binding: MainPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragments: List<Fragment>

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainPagerBinding.inflate(inflater, container, false)
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
        with(sharedPreferences.edit()) {
            putBoolean("isLogin", false)
            putString("phone", null)
            apply()
        }
        startActivity(Intent(requireContext(), OP::class.java))
        requireActivity().finish()
    }

    private fun initViewPager() {
        binding.mainPagerViewPager.run {
            fragments = listOf(MainFocusFragment(), MainFindFragment(), MainLocalFragment())
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
                binding.mainPagerFind.id ->  binding.mainPagerViewPager.currentItem = 1
                binding.mainPagerRegion.id ->  binding.mainPagerViewPager.currentItem = 2
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