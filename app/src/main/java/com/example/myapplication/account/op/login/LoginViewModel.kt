package com.example.myapplication.account.op.login

import androidx.lifecycle.ViewModel
import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.account.service.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userService: UserService
): ViewModel() {

    suspend fun login(loginRequest: LoginRequest): Boolean{
        return userService.login(loginRequest)
    }

}