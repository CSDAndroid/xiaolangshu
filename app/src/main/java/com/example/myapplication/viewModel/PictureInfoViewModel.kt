package com.example.myapplication.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.pictureData.Picture
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.http.HttpInterface
import com.example.myapplication.http.HttpService
import com.example.myapplication.util.ImageDealHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@SuppressLint("StaticFieldLeak")
class PictureInfoViewModel(private val context: Context, private val database: AppDatabase) :
    ViewModel() {

    private val address = "http://8.138.41.189:8085/"
    private val service = HttpService.sendHttp(address, HttpInterface::class.java)

    private val _pictureListLiveData = MutableLiveData<List<Picture1>?>()
    val pictureList: MutableLiveData<List<Picture1>?> get() = _pictureListLiveData

    private val _pictureWorkListLiveData = MutableLiveData<List<Picture1>?>()
    val pictureWorkList: MutableLiveData<List<Picture1>?> get() = _pictureWorkListLiveData

    private val _pictureLikeListLiveData = MutableLiveData<List<Picture1>?>()
    val pictureLikeList: MutableLiveData<List<Picture1>?> get() = _pictureLikeListLiveData

    private val _pictureCollectionListLiveData = MutableLiveData<List<Picture1>?>()
    val pictureCollectionList: MutableLiveData<List<Picture1>?> get() = _pictureCollectionListLiveData

    private val _focusPhoneListLiveData = MutableLiveData<List<String>?>()
    val focusPhoneList: MutableLiveData<List<String>?> get() = _focusPhoneListLiveData

    private val _pictureListByFocusLiveData = MutableLiveData<List<Picture1>?>()
    val pictureListByFocus: MutableLiveData<List<Picture1>?> get() = _pictureListByFocusLiveData

    private val _pictureListByKeywordLiveData = MutableLiveData<List<Picture1>?>()
    val pictureListByKeyword: MutableLiveData<List<Picture1>?> get() = _pictureListByKeywordLiveData

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getPictureListFromNetWork() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getPictureList()
                }
                if (response.isSuccessful) {
                    val pictureList = response.body()?.data
                    _pictureListLiveData.postValue(pictureList)
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getPictureListFromNetWork", "网络请求图片信息响应失败---${errorString}")
                    showToast("网络请求图片信息响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getPictureListFromNetWork", "Network图片信息请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun uploadImageToNetwork(
        imageUri: Uri,
        description: String,
        phone: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                //设置临时文件
                val imageTemFile = ImageDealHelper.getRealPathFromUri(context, imageUri)
                val imageFile = File(imageTemFile.toString())

                //构造MultipartBody.Part
                val mimeType = ImageDealHelper.getMimeType(imageFile)
                val requestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val requestImageFilePart = MultipartBody.Part.createFormData(
                    "file",
                    imageFile.name, requestBody
                )

                //上传图片请求
                val response = withContext(Dispatchers.IO) {
                    service.uploadImage(requestImageFilePart)
                }

                //成功响应处理
                if (response.isSuccessful) {
                    response.body()?.let { responseData ->
                        val userInfo = database.accountDao().getAccount(phone)
                        if (userInfo != null) {
                            val picture = Picture(
                                userInfo.nickname,
                                userInfo.avatar,
                                0,
                                description,
                                0,
                                phone,
                                responseData.data
                            )
                            sharePicture(picture, onComplete)
                        }
                    }
                }
            } catch (e: Exception) {
                //错误处理
                Log.e("uploadImageToNetwork", "Network添加图片错误---${e.message}", e)
            }
        }
    }

    private fun sharePicture(picture: Picture, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.sharePicture(picture)
                }
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data == "分享成功") {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "分享图片成功", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "分享图片失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("sharePicture", "网络请求分享响应失败---${errorString}")
                    showToast("网络请求分享响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("sharePicture", "Network分享请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            } finally {
                onComplete()
            }
        }
    }

    fun getPictureWorkList(phone: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getPictureWorkList(phone)
                }
                if (response.isSuccessful) {
                    val pictureWorkList = response.body()?.data
                    if (pictureWorkList != null) {
                        _pictureWorkListLiveData.postValue(pictureWorkList)
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getPictureWorkList", "网络请求作品列表响应失败---${errorString}")
                    showToast("网络请求作品列表响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getPictureWorkList", "Network作品列表请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun setPictureLikeOrNo(phone: String, id: Long) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.setPictureLikeOrNo(phone, id)
                }
                if (response.isSuccessful) {
                    val data = response.body()!!.data
                    if (data == "点赞成功") {
                        getPictureLikeList(phone)
                        Log.d("setPictureLikeOrNo", "点赞成功")
                    } else if (data == "取消成功") {
                        getPictureLikeList(phone)
                        Log.d("setPictureLikeOrNo", "取消成功")
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("setPictureLikeOrNo", "网络请求点赞响应失败---${errorString}")
                    showToast("网络请求点赞响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("setPictureLikeOrNo", "Network点赞请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun getPictureLikeList(phone: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getPictureLikeList(phone)
                }
                if (response.isSuccessful) {
                    val pictureLikeList = response.body()?.data
                    if (pictureLikeList != null) {
                        _pictureLikeListLiveData.postValue(pictureLikeList)

                        val pictureListString = Gson().toJson(pictureLikeList)
                        withContext(Dispatchers.IO) {
                            database.accountOtherDao().updateLikeList(phone, pictureListString)
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getPictureLikeList", "网络请求点赞列表响应失败---${errorString}")
                    showToast("网络请求点赞列表响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getPictureLikeList", "Network获取点赞列表请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun setPictureCollectionOrNo(phone: String, id: Long) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.setPictureCollectionOrNo(phone, id)
                }
                if (response.isSuccessful) {
                    val data = response.body()!!.data
                    getPictureCollectionList(phone)
                    if (data == "收藏成功") {
                        Log.d("setPictureCollectionOrNo", "收藏成功")
                    } else if (data == "取消成功") {
                        Log.d("setPictureCollectionOrNo", "取消成功")
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("setPictureCollectionOrNo", "网络请求收藏响应失败---${errorString}")
                    showToast("网络请求收藏响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("setPictureCollectionOrNo", "Network收藏请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun getPictureCollectionList(phone: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getPictureCollectionList(phone)
                }
                if (response.isSuccessful) {
                    val pictureCollectionList = response.body()?.data
                    if (pictureCollectionList != null) {
                        _pictureCollectionListLiveData.postValue(pictureCollectionList)

                        val pictureListString = Gson().toJson(pictureCollectionList)
                        withContext(Dispatchers.IO) {
                            database.accountOtherDao().updateCollectionList(phone, pictureListString)
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getPictureCollectionList", "网络请求收藏列表响应失败---${errorString}")
                    showToast("网络请求收藏列表响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getPictureCollectionList", "Network获取收藏列表请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun setAuthorFocusOrNo(followPhone: String, phone: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.followAuthor(followPhone, phone)
                }
                if (response.isSuccessful) {
                    val data = response.body()!!.data
                    getFocusList(phone)
                    if (data == "关注成功") {
                        Log.d("setAuthorFocusOrNo", "关注成功")
                    } else if (data == "取消关注 ") {
                        Log.d("setAuthorFocusOrNo", "取消成功")
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("setAuthorFocusOrNo", "网络请求关注响应失败---${errorString}")
                    showToast("网络请求关注响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("setAuthorFocusOrNo", "Network关注请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun getFocusList(phone: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getFocusList(phone)
                }
                if (response.isSuccessful) {
                    val focusList = response.body()?.data
                    if (focusList != null) {
                        val focusPhoneList: List<String> = focusList.map { it.phone }
                        _focusPhoneListLiveData.postValue(focusPhoneList)
                        val focusListString = Gson().toJson(focusList)
                        withContext(Dispatchers.IO) {
                            database.accountOtherDao().updateFocusList(phone, focusListString)
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e("getFocusList", "网络请求关注列表响应失败---${errorString}")
                    showToast("网络请求关注列表响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getFocusList", "Network关注列表请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun getPictureInfoListByFocus(phone: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getPictureListByFocus(phone)
                }
                if (response.isSuccessful) {
                    val pictureListByFocus = response.body()?.data
                    _pictureListByFocusLiveData.postValue(pictureListByFocus)
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e(
                        "getPictureInfoListByFocus",
                        "网络请求关注列表图片响应失败---${errorString}"
                    )
                    showToast("网络请求关注列表图片响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getPictureInfoListByFocus", "Network关注列表图片请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            }
        }
    }

    fun getPictureListByKeyword(keyword: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.getPictureListByKeyword(keyword)
                }
                if (response.isSuccessful) {
                    val pictureListByKeyword = response.body()?.data
                    if (pictureListByKeyword != null) {
                        _pictureListByKeywordLiveData.postValue(pictureListByKeyword)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "该关键词暂无内容", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    Log.e(
                        "getPictureListByKeyword",
                        "网络请求搜索列表图片响应失败---${errorString}"
                    )
                    showToast("网络请求搜索列表图片响应失败---${errorString}")
                }
            } catch (e: Exception) {
                Log.e("getPictureListByKeyword", "Network搜索列表图片请求错误---${e.message}", e)
                showToast("网络请求错误：${e.message}")
            } finally {
                onComplete()
            }
        }
    }
}
