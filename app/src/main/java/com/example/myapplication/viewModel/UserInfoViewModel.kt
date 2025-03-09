package com.example.myapplication.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.storage.db.entity.Account
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.http.HttpInterface
import com.example.myapplication.http.HttpService
import com.example.myapplication.util.DealDataInfo.dealUserInfo
import com.example.myapplication.util.ImageDealHelper
import com.example.myapplication.util.ImageDealHelper.getMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserInfoViewModel(private val database: AppDatabase) : ViewModel() {

    private val _usersLiveData = MutableLiveData<Account?>()
    val user: MutableLiveData<Account?> get() = _usersLiveData
    private val address = "http://8.138.41.189:8085/"
    private val service = HttpService.sendHttp(address, HttpInterface::class.java)

    //从room获取用户信息
    fun getUserInfoFromRoom(phone: String) {
        viewModelScope.launch {
            try {
                val userInfo = withContext(Dispatchers.IO) {
                    database.accountDao().getAccount(phone)
                }
                _usersLiveData.postValue(userInfo)
            } catch (e: Exception) {
                Log.e("getUserInfo", "room查询错误---${e.message}", e)
            }
        }
    }

    //从NetWork获取用户信息
    fun getUserInfoFromNetwork(phone: String) {
        viewModelScope.launch {
            try {
                //查询用户数据请求
                val response = withContext(Dispatchers.IO) {
                    service.getUserInfo(phone)
                }
                //成功响应处理
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData?.data != null) {
                        val userTemInfo = responseData.data
                        val userInfo = dealUserInfo(userTemInfo)
                        _usersLiveData.postValue(userInfo)
                        database.accountDao().update(userInfo)
                    }
                } else {
                    //不成功响应处理
                    Log.d(
                        "getUserInfoFromNetwork",
                        "查询响应失败---${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                //错误处理
                e.printStackTrace()
                Log.e("getUserInfoFromNetwork", "Network查询错误---${e.message}", e)
            }
        }
    }

    //上传图片ToNetwork
    fun uploadImageToNetwork(
        context: Context,
        imageUri: Uri,
        selectedTag: String,
        phone: String,
        onComplete: (() -> Unit?)?
    ) {
        viewModelScope.launch {
            try {
                //设置临时文件
                val imageTemFile = ImageDealHelper.getRealPathFromUri(context, imageUri)
                val imageFile = File(imageTemFile.toString())

                //构造MultipartBody.Part
                val mimeType = getMimeType(imageFile)
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
                        uploadImageToRoom(phone, responseData.data, selectedTag, onComplete)
                    }
                } else {
                    //不成功响应处理
                    Log.e(
                        "uploadImageToNetwork",
                        "上传图片响应失败: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                //错误处理
                Log.e("uploadImageToNetwork", "Network添加图片错误---${e.message}", e)
            }
        }
    }

    //上传图片ToRoom
    private fun uploadImageToRoom(
        phone: String,
        imageUrl: String,
        selectedTag: String,
        onComplete: (() -> Unit?)?
    ) {
        viewModelScope.launch {
            try {
                when (selectedTag) {
                    "avatar" -> database.accountDao().updateAvatar(phone, imageUrl)
                    "backgroundImage" -> database.accountDao().updateBackground(phone, imageUrl)
                }
            } catch (e: Exception) {
                Log.e("uploadImageToRoom", "room添加图片错误---${e.message}", e)
            } finally {
                if (onComplete != null) {
                    onComplete()
                }
            }
        }
    }

    //更新用户信息ToRoom
    fun updateUserInfoToRoom(
        introduction: String?, birthday: String?,
        sex: String?, nickname: String,
        career: String?, phone: String,
        region: String?, school: String?,
        onComplete: (() -> Unit?)?
    ) {

        viewModelScope.launch {
            try {
                val avatar = database.accountDao().getAvatar(phone)
                val backgroundImage = database.accountDao().getBackground(phone)

                database.accountDao().updateAccountByPhone(
                    introduction, birthday, sex, nickname, career, region, school, phone
                )

                updateUserInfoToNetwork(
                    avatar, backgroundImage, introduction, birthday, sex, nickname,
                    career, phone, region, school, onComplete
                )

            } catch (e: Exception) {
                Log.e("updateUserInfoTorRoom", "room更新用户错误---${e.message}", e)
            }
        }
    }

    //更新用户信息ToNetwork
    private fun updateUserInfoToNetwork(
        avatar: String?, backgroundImage: String?,
        introduction: String?, birthday: String?,
        sex: String?, nickname: String,
        career: String?, phone: String,
        region: String?, school: String?,
        onComplete: (() -> Unit?)?
    ) {

        viewModelScope.launch {
            try {
                //更新用户信息请求
                val response = withContext(Dispatchers.IO) {
                    service.updateUserInfo(
                        avatar, backgroundImage, introduction, birthday,
                        sex, nickname, career, phone, region, school
                    )
                }

                //成功响应处理
                if (response.isSuccessful) {
                    response.body()?.let { responseData ->
                        responseData.msg?.let {
                            if (it == "操作成功") {
                                Log.d("updateUserInfoToNetwork", "更新用户信息成功")
                            }
                        }
                    }
                } else {
                    //不成功响应处理
                    Log.e(
                        "updateUserInfoToNetwork",
                        "更新用户信息响应失败: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                //错误处理
                Log.e("updateUserInfoToNetwork", "Network更新用户信息错误---${e.message}", e)
            } finally {
                if (onComplete != null) {
                    onComplete()
                }
            }
        }
    }
}