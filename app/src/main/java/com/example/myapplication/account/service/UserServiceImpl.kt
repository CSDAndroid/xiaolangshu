package com.example.myapplication.account.service

import com.example.myapplication.account.AccountApi
import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.account.bean.RegisterRequest
import com.example.myapplication.http.HttpService
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.storage.db.entity.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserServiceImpl @Inject constructor(
    private val appDatabase: AppDatabase
): UserService {
    private val address: String get() = "http://8.138.41.189:8085/"
    private val httpService: AccountApi get() = HttpService.sendHttp(address, AccountApi::class.java)

    override fun isLogin(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun login(loginRequest: LoginRequest): Boolean {
        return try {
            val result = httpService.login(loginRequest)
            val account = Account(loginRequest.phone, null, null, loginRequest.password, null, null, null, null, null, null, null)
            val status = appDatabase.accountDao().insert(account)

            result.isSuccessful && status != -1L
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun logout(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun register(registerRequest: RegisterRequest): Boolean {
        TODO("Not yet implemented")
    }
}