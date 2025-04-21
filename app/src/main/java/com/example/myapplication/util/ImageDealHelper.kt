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
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@ActivityScoped
class ImageDealHelper @Inject constructor(@ActivityContext private val context: Context) {
    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>
    private var selectedTag: String? = null

    fun initImagePicker(
        lifecycleOwner: LifecycleOwner,
        onImageSelected: (Uri, String) -> Unit?,
        onImageCropped: (Uri, String) -> Unit
    ) {
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
                startCrop(it)
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

    fun openGallery(tag: String) {
        selectedTag = tag
        getContentLauncher.launch("image/*")
    }

    private fun startCrop(sourceUri: Uri) {
        val timestamp = System.currentTimeMillis()
        val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_image_$timestamp.jpg"))

        val uCrop = when (selectedTag) {
            "avatar" -> {
                UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1f, 1f) // 设置裁剪比例
                    .withMaxResultSize(500, 500) // 设置最大结果大小
                    .withOptions(UCrop.Options().apply {
                        setFreeStyleCropEnabled(true) // 启用自由裁剪
                        setCircleDimmedLayer(true) // 设置圆角裁剪
                    })
            }

            "backgroundImage" -> {
                UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(4f, 3f) // 设置裁剪比例
                    .withMaxResultSize(2200, 1900) // 设置最大结果大小
            }

            null -> return // 如果没有选择的标签，直接返回
            else -> return // 处理未定义的标签
        }

        cropImageLauncher.launch(uCrop.getIntent(context)) // 启动裁剪活动
    }

    fun getRealPathFromUri(uri: Uri): File? {
        var file: File? = null
        try {
            // 创建一个临时文件
            val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
            Log.d("getRealPathFromUri", tempFile.toString())
            // 使用 ContentResolver 和 InputStream 读取 Uri 指向的文件内容
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
}