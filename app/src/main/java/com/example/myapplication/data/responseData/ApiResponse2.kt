package com.example.myapplication.data.responseData

data class ApiResponse2<T>(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T
)
