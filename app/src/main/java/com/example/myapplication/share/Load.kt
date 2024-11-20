package com.example.myapplication.share

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.database.UserInfoDatabase
import com.example.myapplication.util.DialogUtil
import com.example.myapplication.util.ImageDealHelper
import com.example.myapplication.viewModel.PictureInfoViewModel
import com.example.myapplication.viewModel.PictureInfoViewModelFactory

class Load : AppCompatActivity() {

    private lateinit var back: ImageButton
    private lateinit var share: Button
    private lateinit var title: EditText
    private lateinit var tagView: EditText
    private lateinit var tag1: TextView
    private lateinit var tag2: TextView
    private lateinit var tag3: TextView
    private lateinit var tag4: TextView
    private lateinit var tag5: TextView
    private lateinit var tag6: TextView
    private lateinit var tag7: TextView
    private lateinit var tag8: TextView
    private lateinit var addImage: ImageView
    private lateinit var linearLayout1: LinearLayout
    private lateinit var image: ImageView
    private lateinit var linearLayout2: LinearLayout
    private lateinit var uri: Uri
    private lateinit var description: String
    private lateinit var database: UserInfoDatabase
    private lateinit var pictureInfoViewModel: PictureInfoViewModel
    private lateinit var registerForResult: ActivityResultLauncher<Intent>
    private lateinit var phone: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        bind()
        initData()
        initViewModel()
        onClick()
    }

    private fun bind() {
        back = findViewById(R.id.load_back)
        share = findViewById(R.id.load_share)
        title = findViewById(R.id.load_title)
        tagView = findViewById(R.id.load_label)
        tag1 = findViewById(R.id.tag1)
        tag2 = findViewById(R.id.tag2)
        tag3 = findViewById(R.id.tag3)
        tag4 = findViewById(R.id.tag4)
        tag5 = findViewById(R.id.tag5)
        tag6 = findViewById(R.id.tag6)
        tag7 = findViewById(R.id.tag7)
        tag8 = findViewById(R.id.tag8)
        addImage = findViewById(R.id.load_addImage)
        linearLayout1 = findViewById(R.id.load_linearLayout1)
        image = findViewById(R.id.load_image)
        linearLayout2 = findViewById(R.id.load_linearLayout2)
    }

    private fun initData() {
        uri = Uri.parse("")
        description = ""
        sharedPreferences = this.getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
        phone = sharedPreferences.getString("phone", null).toString()
    }

    private fun initViewModel() {
        database = UserInfoDatabase.getDatabase(this)

        pictureInfoViewModel = ViewModelProvider(
            this,
            PictureInfoViewModelFactory(this, database)
        )[PictureInfoViewModel::class.java]
    }

    private fun onClick() {
        back.setOnClickListener {
            finish()
        }

        share.setOnClickListener {
            val dialog = DialogUtil()
            val tagText = tagView.text.toString()
            when {
                uri.toString().isEmpty() -> {
                    Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show()
                }

                title.text.toString().isEmpty() -> {
                    Toast.makeText(this, "请输入描述", Toast.LENGTH_SHORT).show()
                }

                tagText.isEmpty() -> {
                    Toast.makeText(this, "请输入或选择标签", Toast.LENGTH_SHORT).show()
                }

                !tagText.startsWith("#") -> {
                    Toast.makeText(this, "标签必须以#开头", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    dialog.show(supportFragmentManager, "LoadingDialog")
                    description = "$tagText//:${title.text}"
                    pictureInfoViewModel.uploadImageToNetwork(uri, description, phone) {
                        dialog.dismiss()
                        finish()
                    }
                }
            }
        }

        tagView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentTags = s.toString().split(" ")
                manageTagVisibility(currentTags)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        tag1.setOnClickListener { addTagToTagView(tagView, tag1.text.toString(), tag1) }
        tag2.setOnClickListener { addTagToTagView(tagView, tag2.text.toString(), tag2) }
        tag3.setOnClickListener { addTagToTagView(tagView, tag3.text.toString(), tag3) }
        tag4.setOnClickListener { addTagToTagView(tagView, tag4.text.toString(), tag4) }
        tag5.setOnClickListener { addTagToTagView(tagView, tag5.text.toString(), tag5) }
        tag6.setOnClickListener { addTagToTagView(tagView, tag6.text.toString(), tag6) }
        tag7.setOnClickListener { addTagToTagView(tagView, tag7.text.toString(), tag7) }
        tag8.setOnClickListener { addTagToTagView(tagView, tag8.text.toString(), tag8) }

        ImageDealHelper.initImagePicker(
            this,
            onImageSelected = { uri, _ ->
                this.uri = uri
                linearLayout1.visibility = View.VISIBLE
                linearLayout2.visibility = View.GONE
                image.setImageURI(uri)
            },
            onImageCropped = { _, _ ->
            }
        )

        addImage.setOnClickListener {
            ImageDealHelper.openGallery(null)
        }

        image.setOnClickListener {
            val intent = Intent(this, DeleteLoad::class.java)
            intent.putExtra("uri", uri.toString())
            registerForResult.launch(intent)
        }

        registerForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        val message = intent.getStringExtra("isDelete")
                        if (message == "delete") {
                            linearLayout2.visibility = View.VISIBLE
                            linearLayout1.visibility = View.GONE
                            image.setImageURI(null)
                        }
                    }
                }
            }
    }

    private fun addTagToTagView(tagView: EditText, tagText: String, tag: TextView) {
        if (tagView.text == null) {
            tagView.setText(tagText)
        } else {
            val text = "${tagView.text}$tagText"
            tagView.setText(text)
        }
        tag.visibility = View.GONE
    }

    private fun manageTagVisibility(currentTags: List<String>) {
        val allTags = listOf(tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8)
        allTags.forEach { tag ->
            if (currentTags.contains(tag.text.toString())) {
                tag.visibility = View.GONE
            } else {
                tag.visibility = View.VISIBLE
            }
        }
    }
}