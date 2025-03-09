package com.example.myapplication.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object HttpService {
    //这是一个泛型函数，接受一个地址 address 和一个服务类 serviceClass，并返回一个泛型类型 T 的结果。
    fun <T : Any> sendHttp(address: String, serviceClass: Class<T>): T {
        //拦截器
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        //创建客户端实例,用于配置和定制 HTTP 请求的行为，例如超时时间、拦截器等。
        val client = OkHttpClient.Builder()
            .connectTimeout(900L, TimeUnit.MILLISECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()

        //创建retrofit实例,可以用来创建 API 接口的实例，发起网络请求等操作
        val retrofit = Retrofit.Builder()
            .baseUrl(address)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())//json转换器
            .build()

        //使用 Retrofit 实例创建一个特定类型的服务接口实例，该接口由参数 serviceClass 指定，并将其返回。
        return retrofit.create(serviceClass)
    }
}