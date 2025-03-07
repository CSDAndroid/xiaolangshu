package com.example.myapplication.account.service

import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.account.bean.RegisterRequest

interface UserService {
    fun isLogin(): Boolean

    suspend fun login(loginRequest: LoginRequest):Boolean

    suspend fun logout():Boolean

    suspend fun register(registerRequest: RegisterRequest):Boolean
}