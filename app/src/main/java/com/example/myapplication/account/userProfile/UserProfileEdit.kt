package com.example.myapplication.account.userProfile

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityUserProfileEditBinding
import com.example.myapplication.storage.db.entity.Account
import com.example.myapplication.util.ImageDealHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileEdit : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileEditBinding
    private val viewModel by viewModels<UserProfileViewModel>()

    @Inject
    lateinit var imageDealHelper: ImageDealHelper

    private lateinit var avatarUri: String
    private lateinit var backgroundUri: String
    private var updateStatus: Boolean = false
    private val phone: String by lazy { viewModel.getPhone() ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.userInfoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.userInfoToolbar.setNavigationOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(this) { profile ->
            profile?.let { setUI(it) }
        }
        viewModel.updateStatus.observe(this) { updateStatus = it }
        viewModel.getUserProfile(phone)
    }

    private fun setUI(account: Account) {
        avatarUri = account.avatar.toString()
        backgroundUri = account.background.toString()

        binding.userInfoNickname.setText(account.nickname)
        binding.userInfoPhone.text = phone

        loadImage(account.background, binding.userInfoBackgroundImage)
        loadImage(account.avatar, binding.userInfoAvatar)

        binding.userInfoIntroduction.setText(account.introduction)
        binding.userInfoBirthday.setText(account.birthday)
        binding.userInfoCareer.setText(account.career)
        binding.userInfoRegion.setText(account.region)
        binding.userInfoSchool.setText(account.school)

        setupImageClickListeners()
        setupGenderText(account.sex)

        imageDealHelper.initImagePicker(this, onImageSelected = { _, _ -> }, onImageCropped = { uri, selectedTag ->
            handleImageCropped(uri, selectedTag)
        })

        binding.userInfoModify.setOnClickListener { handleProfileUpdate(account) }
    }

    private fun loadImage(imageUri: String?, imageView: ImageView) {
        imageUri?.let { Glide.with(this).load(it).into(imageView) }
    }

    private fun setupImageClickListeners() {
        binding.userInfoAvatar.setOnClickListener { imageDealHelper.openGallery("avatar") }
        binding.userInfoBackgroundImage.setOnClickListener { imageDealHelper.openGallery("backgroundImage") }
    }

    private fun setupGenderText(sex: String?) {
        binding.userInfoSex.setText(
            when (sex) {
                "male" -> "男"
                "female" -> "女"
                else -> "未填"
            }
        )
    }

    private fun handleImageCropped(uri: Uri, selectedTag: String) {
        when (selectedTag) {
            "avatar" -> {
                avatarUri = uri.toString()
                binding.userInfoAvatar.setImageURI(uri)
            }
            "backgroundImage" -> {
                backgroundUri = uri.toString()
                binding.userInfoBackgroundImage.setImageURI(uri)
            }
        }
    }

    private fun handleProfileUpdate(account: Account) {
        val gender = when (binding.userInfoSex.text.toString()) {
            "男" -> "male"
            "女" -> "female"
            else -> "other"
        }

        val updatedAccount = Account(
            phone = phone,
            avatar = avatarUri,
            nickname = binding.userInfoNickname.text.toString(),
            password = account.password,
            introduction = binding.userInfoIntroduction.text.toString(),
            sex = gender,
            birthday = binding.userInfoBirthday.text.toString(),
            career = binding.userInfoCareer.text.toString(),
            region = binding.userInfoRegion.text.toString(),
            school = binding.userInfoSchool.text.toString(),
            background = backgroundUri
        )

        viewModel.updateUserProfile(updatedAccount)

        val toastMessage = if (updateStatus) {
            "修改信息成功"
        } else {
            "修改信息失败"
        }
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }
}