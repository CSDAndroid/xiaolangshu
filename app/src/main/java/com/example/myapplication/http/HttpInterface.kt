package com.example.myapplication.http

import com.example.myapplication.data.pictureData.Picture
import com.example.myapplication.data.pictureData.Picture1
import com.example.myapplication.data.randomVideoData.Data
import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.data.responseData.ApiResponse2
import com.example.myapplication.data.responseData.MyResponse1
import com.example.myapplication.data.searchVideoData.SearchData
import com.example.myapplication.data.searchVideoData.VideoBySearch
import com.example.myapplication.data.userData.UserResponseData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface HttpInterface {

    //注册
    @POST("/user/customer/register")
    suspend fun register(
        @Query("nickname") nickname: String,
        @Query("phone") phone: String,
        @Query("password") pwd: String,
        @Query("code") code: String,
    ): Response<MyResponse1<String>>

    //登录
    @POST("/user/customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<MyResponse1<String>>

    //发送验证码
    @FormUrlEncoded
    @POST("/user/customer/code")
    suspend fun sendVerificationCode(
        @Field("phone") phone: String,
        @Field("nickname") nickname: String
    ): Response<MyResponse1<String>>

    //获取用户信息
    @POST("/user/customer/getUser")
    suspend fun getUserInfo(
        @Query("phone") phone: String
    ): Response<MyResponse1<UserResponseData>>

    //上传图片
    @Multipart
    @POST("/common/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<MyResponse1<String>>

    //更新用户信息
    @POST("/user/customer/changeSave")
    suspend fun updateUserInfo(
        @Query("avatar") avatar: String?,
        @Query("backgroundImage") backgroundImage: String?,
        @Query("bio") introduction: String?,
        @Query("birthday") birthday: String?,
        @Query("gender") sex: String?,
        @Query("nickname") nickname: String,
        @Query("occupation") career: String?,
        @Query("phone") phone: String,
        @Query("region") region: String?,
        @Query("school") school: String?,
    ): Response<MyResponse1<String>>

    //随机获取视频数据
    @GET("/x/web-interface/wbi/index/top/feed/rcmd")
    suspend fun getVideoData(): Response<ApiResponse2<Data>>

    //关键词获取视频数据
    @GET("/x/web-interface/search/all/v2")
    suspend fun getVideoDataByKey(@Query("keyword") keyword: String):
            Response<ApiResponse2<SearchData<VideoBySearch>>>

    @GET("/x/space/wbi/arc/search")
    suspend fun getVideoDataByMid(@Query("mid") mid: String): Response<ResponseBody>

    @POST("/system/picture/uploadPicture")
    suspend fun sharePicture(
        @Body picture: Picture
    ): Response<MyResponse1<String>>

    @POST("/system/picture/getPictureByPhone")
    suspend fun getPictureWorkList(
        @Query("phone") phone: String
    ): Response<MyResponse1<List<Picture1>>>

    @GET("/system/picture/getAllPicture")
    suspend fun getPictureList(): Response<MyResponse1<List<Picture1>>>

    @POST("/system/picture/pictureLikes")
    suspend fun setPictureLikeOrNo(
        @Query("phone") phone: String,
        @Query("id") id: Long
    ): Response<MyResponse1<String>>

    @POST("/system/picture/pictureLikesList")
    suspend fun getPictureLikeList(
        @Query("phone") phone: String
    ): Response<MyResponse1<List<Picture1>>>

    @POST("/system/picture/pictureFavorites")
    suspend fun setPictureCollectionOrNo(
        @Query("phone") phone: String,
        @Query("id") id: Long
    ): Response<MyResponse1<String>>

    @POST("/system/picture/pictureFavoritesList")
    suspend fun getPictureCollectionList(
        @Query("phone") phone: String
    ): Response<MyResponse1<List<Picture1>>>

    @POST("/system/picture/follow")
    suspend fun followAuthor(
        @Query("followerPhone") followerPhone: String,
        @Query("phone") phone: String
    ): Response<MyResponse1<String>>

    @POST("/system/picture/getFollowerList")
    suspend fun getFocusList(
        @Query("phone") phone: String
    ): Response<MyResponse1<List<UserResponseData>>>

    @POST("/system/picture/getAllFollowPictureList")
    suspend fun getPictureListByFocus(
        @Query("phone") phone: String
    ): Response<MyResponse1<List<Picture1>>>

    @GET("/system/picture/{keywords}")
    suspend fun getPictureListByKeyword(
        @Query("keyword") keyword: String
    ): Response<MyResponse1<List<Picture1>>>

}