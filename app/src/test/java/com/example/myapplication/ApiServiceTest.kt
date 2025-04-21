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
    }
}