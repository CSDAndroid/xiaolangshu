package com.example.myapplication

import com.example.myapplication.http.HttpInterface
import com.example.myapplication.http.HttpService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiServiceTest {

    @Test
    fun testSendVerificationCode(): Unit = runBlocking {
//        val address = "https://api.bilibili.com/"
//        val service = HttpService.sendHttp(address, HttpInterface::class.java)
//        val result = service.getVideoDataByMid("585267")
//        if (result.body() != null) {
//            val videoList = result.body()
//            println(videoList)
//        }

        val url = "https://api.bilibili.com/"

        val client = OkHttpClient.Builder()
            .connectTimeout(3000L, TimeUnit.MILLISECONDS)
            .writeTimeout(900L, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val tetxhht = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val fecthhttp = tetxhht.create(HttpInterface::class.java)

        println(fecthhttp.getVideoData())

    }
}