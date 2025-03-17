package com.example.myapplication.account.service

import com.example.myapplication.account.AccountApi
import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.account.bean.RegisterRequest
import com.example.myapplication.http.HttpService
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.storage.db.entity.Account
import com.example.myapplication.storage.preference.PreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserServiceImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val preferencesManager: PreferencesManager
): UserService {
    private val address: String = "http://8.138.41.189:8085/"
    private val accountApiService: AccountApi by lazy { HttpService.sendHttp(address, AccountApi::class.java) }

    override fun isLogin(): Boolean {
        return preferencesManager.getPhone() != null
    }

    override fun getPhone(): String? {
        return preferencesManager.getPhone()
    }

    override suspend fun login(loginRequest: LoginRequest): Boolean {
        return try {
            val result = accountApiService.login(loginRequest)
            val account = Account(loginRequest.phone, null, null, loginRequest.password, null, null, null, null, null, null, null)
            val status = appDatabase.accountDao().insert(account)
            preferencesManager.savePhone(loginRequest.phone)

            result.isSuccessful && status != -1L
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun logout(account: Account): Boolean {
        return try {
            val status = appDatabase.accountDao().delete(account)
            preferencesManager.clearPhone()

            status != 0
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): Boolean {
        return try {
            val result = accountApiService.register(registerRequest.nickname,registerRequest.phone,registerRequest.password,registerRequest.verificationCode)
            val account = Account(registerRequest.phone, null, null, registerRequest.password, null, null, null, null, null, null, null)
            val status = appDatabase.accountDao().insert(account)
            preferencesManager.savePhone(registerRequest.phone)

            result.isSuccessful && status != -1L
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun sendVerificationCode(phone: String, nickname: String): String {
        return try {
            val result = accountApiService.sendVerificationCode(phone,nickname)
            result.body()?.data ?: ""
        } catch (e: Exception) {
            " "
        }
    }

    override suspend fun getUserProfile(phone: String): Account {
        return appDatabase.accountDao().getAccount(phone)
    }

    override suspend fun updateUserProfile(account: Account): Boolean {
        return try {
            val status = appDatabase.accountDao().update(account)
            status != 0
        } catch (e: Exception) {
            false
        }
    }
}