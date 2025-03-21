package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.account.op.OP
import com.example.myapplication.account.service.UserService
import com.example.myapplication.adapter.MAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.mainFragment.MainFragment
import com.example.myapplication.messageFragment.MessageFragment
import com.example.myapplication.mine.home.MineFragment
import com.example.myapplication.pictureFragment.PictureFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragments: List<Fragment>
    private val PERMISSION_REQUEST_CODE = 1

    @Inject
    lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 检查并请求权限
        if (!hasPermission()) {
            requestPermissions()
        }

//        judgeIsLogin()
        initViewPager()
        initRadioGroup()
    }

    private fun judgeIsLogin() {
        if (!userService.isLogin()) {
            val intent = Intent(this, OP::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initViewPager() {
        binding.mainViewPager.run {
            fragments = listOf(MainFragment(), PictureFragment(), MessageFragment(), MineFragment())
            adapter = MAdapter(supportFragmentManager, lifecycle, fragments)
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 更新 RadioGroup 的选中状态
                    when (position) {
                        0 -> binding.mainNavRadioGroup.check(binding.main.id)
                        1 -> binding.mainNavRadioGroup.check(binding.picture.id)
                        2 -> binding.mainNavRadioGroup.check(binding.message.id)
                        3 -> binding.mainNavRadioGroup.check(binding.mine.id)
                    }
                }

            })
        }
    }

    private fun initRadioGroup() {
        binding.mainNavRadioGroup.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                binding.main.id -> {
                    binding.mainViewPager.currentItem = 0
                }

                binding.picture.id -> {
                    // 切换到焦点页面
                    binding.mainViewPager.currentItem = 1
                }

                binding.message.id -> {
                    // 切换到发现页面
                    binding.mainViewPager.currentItem = 2
                }

                binding.mine.id -> {
                    // 切换到本地页面
                    binding.mainViewPager.currentItem = 3
                }
            }
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被授予
                    Toast.makeText(this, "${permissions[i]} 授予", Toast.LENGTH_SHORT).show()
                } else {
                    // 权限被拒绝
                    Toast.makeText(this, "${permissions[i]} 拒绝", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}