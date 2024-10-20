package com.example.myapplication.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

object ImageDealHelper {
    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>
    private var selectedTag: String? = null

    fun initImagePicker(
        lifecycleOwner: LifecycleOwner,
        onImageSelected: (Uri, String) -> Unit?,
        onImageCropped: (Uri, String) -> Unit
    ) {
        val context = when (lifecycleOwner) {
            is FragmentActivity -> lifecycleOwner
            is Fragment -> lifecycleOwner.requireContext()
            else -> throw IllegalArgumentException("LifecycleOwner must be either FragmentActivity or Fragment")
        }

        val activityResultRegistry = when (lifecycleOwner) {
            is FragmentActivity -> lifecycleOwner.activityResultRegistry
            is Fragment -> lifecycleOwner.requireActivity().activityResultRegistry
            else -> throw IllegalArgumentException("LifecycleOwner must be either FragmentActivity or Fragment")
        }

        getContentLauncher = activityResultRegistry.register(
            "get_content",
            ActivityResultContracts.GetContent()
        ) { result: Uri? ->
            result?.let {
                onImageSelected(it, selectedTag.toString())
                startCrop(context, it)
            }
        }

        cropImageLauncher = activityResultRegistry.register(
            "crop_image",
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val resultUri: Uri? = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    onImageCropped(it, selectedTag.toString())
                }
                selectedTag = null // 重置标识符
            }
        }
    }

    fun openGallery(tag: String?) {
        selectedTag = tag
        getContentLauncher.launch("image/*")
    }

    private fun startCrop(context: Context, sourceUri: Uri) {
        val timestamp = System.currentTimeMillis()
        val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_image_$timestamp.jpg"))
        when (selectedTag) {
            "avatar" -> {
                val uCrop = UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1f, 1f) // 设定裁剪比例
                    .withMaxResultSize(500, 500) // 最后图片的大小
                    .withOptions(UCrop.Options().apply {
                        setFreeStyleCropEnabled(true) // 启用自由裁剪
                        setCircleDimmedLayer(true) // 设置圆角裁剪
                    })
                cropImageLauncher.launch(uCrop.getIntent(context)) // 启动裁剪活动
            }

            "backgroundImage" -> {
                val uCrop = UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(4f, 3f) // 设定裁剪比例
                    .withMaxResultSize(2200, 1900) // 最后图片的大小
                cropImageLauncher.launch(uCrop.getIntent(context)) // 启动裁剪活动
            }
        }
    }

    fun getRealPathFromUri(context: Context, uri: Uri): File? {
        var file: File? = null
        try {
            // 创建一个临时文件
            val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
            Log.d("getRealPathFromUri", tempFile.toString())
            // 使用ContentResolver和InputStream读取Uri指向的文件内容
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file = tempFile
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun getMimeType(file: File): String {
        val extension = file.name.substringAfterLast('.')
        return when (extension.lowercase(Locale.ROOT)) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/octet-stream"
        }
    }

    fun convertToHttps(url: String?): String? {
        return if (url != null && url.startsWith("http://")) {
            url.replace("http://", "https://")
        } else url
    }
}