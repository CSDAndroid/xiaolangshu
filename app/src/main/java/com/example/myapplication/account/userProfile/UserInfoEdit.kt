package com.example.myapplication.account.userProfile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityUserProfileEditBinding
import com.example.myapplication.storage.db.entity.Account
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileEditBinding
    private val viewModel by viewModels<UserProfileViewModel>()
    private var updateStatus: Boolean = false
    private val phone: String by lazy {
        viewModel.getPhone() ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.userInfoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.userInfoToolbar.setNavigationOnClickListener {
            finish()
        }

        getAccountProfile()
    }

    private fun getAccountProfile() {
        viewModel.userProfile.observe(this) { profile ->
            profile?.let { setUI(profile) }
        }
        viewModel.updateStatus.observe(this) {
            updateStatus = it
        }
        viewModel.getUserProfile(phone)
    }

    private fun setUI(account: Account) {
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
//        binding.userInfoAvatar.setOnClickListener {
//            ImageDealHelper.openGallery("avatar")
//        }
//
//        binding.userInfoBackgroundImage.setOnClickListener {
//            ImageDealHelper.openGallery("backgroundImage")
//        }
        account.sex?.let {
            when (it) {
                "male" -> binding.userInfoSex.setText("男")
                "female" -> binding.userInfoSex.setText("女")
                else -> binding.userInfoSex.setText("未填")
            }
        }

//        ImageDealHelper.initImagePicker(
//            this,
//            onImageSelected = { _, _ -> },
//            onImageCropped = { uri, selectedTag ->
//                when (selectedTag) {
//                    "avatar" -> binding.userInfoAvatar.setImageURI(uri)
//                    "backgroundImage" -> binding.userInfoBackgroundImage.setImageURI(uri)
//                }
//                dialogUtil.show(supportFragmentManager, "DialogShowing")
//                userViewModel.uploadImageToNetwork(this, uri, selectedTag, phone) {
//                    dialogUtil.dismiss()
//                }
//            }
//        )

        binding.userInfoModify.setOnClickListener {
            var gender = "other"
            if (binding.userInfoSex.text.toString() == "男") {
                gender = "male"
            } else if (binding.userInfoSex.text.toString() == "女") {
                gender = "female"
            }
            val account1 = Account(
                phone,
                null,
                binding.userInfoNickname.text.toString(),
                account.password,
                binding.userInfoIntroduction.text.toString(),
                gender,
                binding.userInfoSex.text.toString(),
                binding.userInfoCareer.text.toString(),
                binding.userInfoRegion.text.toString(),
                binding.userInfoSchool.text.toString(),
                null
            )
            viewModel.updateUserProfile(account1)
            if (updateStatus) {
                Toast.makeText(this, "修改信息成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "修改信息失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}