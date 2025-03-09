package com.example.myapplication.account

import com.example.myapplication.account.bean.CommonResult
import com.example.myapplication.account.bean.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface AccountApi {
    //登录
    @POST("/user/customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<CommonResult>

    //注册
    @POST("/user/customer/register")
    suspend fun register(
        @Query("nickname") nickname: String,
        @Query("phone") phone: String,
        @Query("password") pwd: String,
        @Query("code") code: String,
    ): Response<CommonResult>

    //发送验证码
    @FormUrlEncoded
    @POST("/user/customer/code")
    suspend fun sendVerificationCode(
        @Field("phone") phone: String,
        @Field("nickname") nickname: String
    ): Response<CommonResult>
}