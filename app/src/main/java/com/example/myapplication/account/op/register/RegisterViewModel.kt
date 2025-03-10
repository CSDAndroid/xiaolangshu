package com.example.myapplication.account.op.register

import androidx.lifecycle.ViewModel
import com.example.myapplication.account.bean.RegisterRequest
import com.example.myapplication.account.service.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userService: UserService
): ViewModel() {
    suspend fun register(registerRequest: RegisterRequest): Boolean {
        return userService.register(registerRequest)
    }

    suspend fun sendVerificationCode(phone:String, nickname: String): String {
        return userService.sendVerificationCode(phone, nickname)
    }
}