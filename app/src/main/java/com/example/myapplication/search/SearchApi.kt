package com.example.myapplication.search

import com.example.myapplication.common.bean.ApiResponse
import com.example.myapplication.search.bean.bilibili.SearchData
import com.example.myapplication.search.bean.bilibili.VideoBySearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    //关键词获取视频数据
    @GET("/x/web-interface/search/all/v2")
    suspend fun getVideoDataByKey(@Query("keyword") keyword: String):
            Response<ApiResponse<SearchData<VideoBySearch>>>
}