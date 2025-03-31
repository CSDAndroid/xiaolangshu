package com.example.myapplication.home

import com.example.myapplication.common.bean.ApiResponse
import com.example.myapplication.home.bean.bilibili.Data
import retrofit2.Response
import retrofit2.http.GET

interface MainApi {
    //随机获取视频数据
    @GET("/x/web-interface/wbi/index/top/feed/rcmd")
    suspend fun getVideoData(): Response<ApiResponse<Data>>
}