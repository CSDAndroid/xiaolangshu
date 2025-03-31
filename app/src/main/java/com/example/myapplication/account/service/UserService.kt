package com.example.myapplication.account.service

import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.account.bean.RegisterRequest
import com.example.myapplication.storage.db.entity.Account

interface UserService {

    fun isLogin(): Boolean

    fun getPhone(): String?

    suspend fun login(loginRequest: LoginRequest): Boolean

    suspend fun logout(): Boolean

    suspend fun register(registerRequest: RegisterRequest): Boolean

    suspend fun sendVerificationCode(phone: String, nickname: String): String

    suspend fun getUserProfile(phone: String): Account

    suspend fun updateUserProfile(account: Account): Boolean
}