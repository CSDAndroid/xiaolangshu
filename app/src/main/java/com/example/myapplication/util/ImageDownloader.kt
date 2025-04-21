package com.example.myapplication.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageDownloader {
    interface ImageDownLoadCallBack {
        fun onDownLoadSuccess(text: String)
        fun onDownLoadFailed(text: String)
    }

    private var currentFile: File? = null

    fun startDownload(context: Context, url: String, callBack: ImageDownLoadCallBack) {
        if (!isNetworkAvailable(context)) {
            callBack.onDownLoadFailed("没有网络连接")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 使用 Glide 下载图片
                val bitmap = withContext(Dispatchers.IO) {
                    Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .submit()
                        .get()
                }

                // 切换到主线程以更新 UI
                withContext(Dispatchers.Main) {
                    bitmap?.let {
                        saveImageToGallery(context, it)
                        callBack.onDownLoadSuccess("下载图片成功")
                    } ?: run {
                        callBack.onDownLoadFailed("下载的图片为空")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callBack.onDownLoadFailed("下载失败: ${e.message}")
                }
            }
        }
    }

    private fun saveImageToGallery(context: Context, bmp: Bitmap) {
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val folderName = "新建文件夹"
        val appDir = File(picturesDir, folderName)

        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val fileName = "${System.currentTimeMillis()}.jpg"
        currentFile = File(appDir, fileName)

        FileOutputStream(currentFile).use { fos ->
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
        }

        // 通知图库更新
        MediaScannerConnection.scanFile(context, arrayOf(currentFile?.absolutePath), null, null)
        Log.d("ImageDownload", "图片已下载到: ${currentFile?.absolutePath}")
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}