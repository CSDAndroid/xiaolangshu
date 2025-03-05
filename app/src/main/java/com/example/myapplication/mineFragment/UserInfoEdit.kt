package com.example.myapplication.mineFragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.database.Account
import com.example.myapplication.database.AccountDatabase
import com.example.myapplication.databinding.ActivityUserInfoBinding
import com.example.myapplication.util.DialogUtil
import com.example.myapplication.util.ImageDealHelper
import com.example.myapplication.viewModel.UserInfoViewModel
import com.example.myapplication.viewModel.UserInfoViewModelFactory

class UserInfoEdit : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding

    private val dialogUtil: DialogUtil by lazy { DialogUtil() }

    private val phone: String by lazy {
        sharedPreferences.getString("phone", null).toString()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    private val userViewModel: UserInfoViewModel by lazy {
        val database = AccountDatabase.getDatabase(this)
        ViewModelProvider(
            this,
            UserInfoViewModelFactory(database)
        )[UserInfoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        back()
        getUserInfo()
    }

    private fun back() {
        setSupportActionBar(binding.userInfoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.userInfoToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun getUserInfo() {
        userViewModel.apply {
            user.observe(this@UserInfoEdit) { user ->
                user?.let { setUserInfoToView(it) }
            }
        }.getUserInfoFromRoom(phone)
    }

    private fun setUserInfoToView(account: Account) {
        binding.userInfoNickname.setText(account.nickname)
        binding.userInfoPhone.text = phone

        account.background?.let {
            Glide.with(this).load(it).into(binding.userInfoBackgroundImage)
        }
        account.avatar?.let {
            Glide.with(this).load(it).into(binding.userInfoAvatar)
        }
        account.introduction?.let {
            binding.userInfoIntroduction.setText(it)
        }
        account.birthday?.let {
            binding.userInfoBirthday.setText(it)
        }
        account.career?.let {
            binding.userInfoCareer.setText(it)
        }
        account.region?.let {
            binding.userInfoRegion.setText(it)
        }
        account.school?.let {
            binding.userInfoSchool.setText(it)
        }
        binding.userInfoAvatar.setOnClickListener {
            ImageDealHelper.openGallery("avatar")
        }

        binding.userInfoBackgroundImage.setOnClickListener {
            ImageDealHelper.openGallery("backgroundImage")
        }
        account.sex?.let {
            when (it) {
                "male" -> binding.userInfoSex.setText("男")
                "female" -> binding.userInfoSex.setText("女")
                else -> binding.userInfoSex.setText("未填")
            }
        }

        ImageDealHelper.initImagePicker(
            this,
            onImageSelected = { _, _ ->
            },
            onImageCropped = { uri, selectedTag ->
                when (selectedTag) {
                    "avatar" -> binding.userInfoAvatar.setImageURI(uri)
                    "backgroundImage" -> binding.userInfoBackgroundImage.setImageURI(uri)
                }
                dialogUtil.show(supportFragmentManager, "DialogShowing")
                userViewModel.uploadImageToNetwork(this, uri, selectedTag, phone) {
                    dialogUtil.dismiss()
                }
            }
        )

        binding.userInfoModify.setOnClickListener {
            var gender = "other"
            if (binding.userInfoSex.text.toString() == "男") {
                gender = "male"
            } else if (binding.userInfoSex.text.toString() == "女") {
                gender = "female"
            }
            dialogUtil.show(supportFragmentManager, "DialogShowing")
            userViewModel.updateUserInfoToRoom(
                binding.userInfoIntroduction.text.toString(),
                binding.userInfoBirthday.text.toString(),
                gender,
                binding.userInfoNickname.text.toString(),
                binding.userInfoCareer.text.toString(),
                phone,
                binding.userInfoRegion.text.toString(),
                binding.userInfoSchool.text.toString()
            ) {
                dialogUtil.dismiss()
            }
            Toast.makeText(this, "修改信息成功", Toast.LENGTH_SHORT).show()
        }
    }
}