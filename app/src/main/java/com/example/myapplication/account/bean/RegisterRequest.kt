package com.example.myapplication.account.bean

data class RegisterRequest(
    val nickname: String,
    val phone: String,
    val password: String,
    val verificationCode: String
)
