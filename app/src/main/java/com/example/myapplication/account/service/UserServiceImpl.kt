package com.example.myapplication.account.service

import com.example.myapplication.account.AccountApi
import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.account.bean.RegisterRequest
import com.example.myapplication.http.HttpService
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.storage.db.entity.Account
import com.example.myapplication.storage.preference.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

// 有时间来处理这里的协程上下文的问题
@Singleton
class UserServiceImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val preferencesManagerProvider: Provider<PreferencesManager>
) : UserService {
    private val preferencesManager by lazy { preferencesManagerProvider.get() }
    private val address: String = "http://8.138.41.189:8085/"
    private val accountApiService: AccountApi by lazy {
        HttpService.sendHttp(address, AccountApi::class.java)
    }

    override fun isLogin(): Boolean {
        return preferencesManager.getPhone() != null
    }

    override fun getPhone(): String? {
        return preferencesManager.getPhone()
    }

    override suspend fun login(loginRequest: LoginRequest): Boolean {
        val result = accountApiService.login(loginRequest)
        val account = Account(loginRequest.phone, null, null, loginRequest.password, null, null, null, null, null, null, null)
        val status = appDatabase.accountDao().insert(account)
        preferencesManager.savePhone(loginRequest.phone)

        return result.isSuccessful && status != -1L
    }

    override suspend fun logout(): Boolean {
        val account = appDatabase.accountDao().getAccount(getPhone() ?: "")
        return run {
            val status = appDatabase.accountDao().delete(account)
            preferencesManager.clearPhone()
            status != 0
        }
    }

    override suspend fun sendVerificationCode(phone: String, nickname: String): String {
        val result = accountApiService.sendVerificationCode(phone, nickname)
        return result.body()?.data ?: ""
    }

    override suspend fun register(registerRequest: RegisterRequest): Boolean {
        val result = accountApiService.register(registerRequest.nickname, registerRequest.phone, registerRequest.password, registerRequest.verificationCode)
        val account = Account(registerRequest.phone, null, null, registerRequest.password, null, null, null, null, null, null, null)
        val status = appDatabase.accountDao().insert(account)
        preferencesManager.savePhone(registerRequest.phone)

        return result.isSuccessful && status != -1L
    }

    override suspend fun getUserProfile(phone: String): Account {
        return withContext(Dispatchers.IO) {
            appDatabase.accountDao().getAccount(phone)
        }
    }

    override suspend fun updateUserProfile(account: Account): Boolean {
        val status = appDatabase.accountDao().update(account)
        return status != 0
    }
}