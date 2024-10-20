package com.example.myapplication

import com.example.myapplication.http.HttpInterface
import com.example.myapplication.http.HttpUtil
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ApiServiceTest {

    @Test
    fun testSendVerificationCode(): Unit = runBlocking {
        val address = "https://api.bilibili.com/"
        val service = HttpUtil.sendHttp(address, HttpInterface::class.java)
        val result = service.getVideoDataByMid("585267")
        if (result.body() != null) {
            val videoList = result.body()
            println(videoList)
        }
    }
}