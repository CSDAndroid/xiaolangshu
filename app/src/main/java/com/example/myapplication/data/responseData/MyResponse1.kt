package com.example.myapplication.data.responseData

data class MyResponse1<T>(
    val msg: String?,
    val code: Int,
    val data: T,
)
