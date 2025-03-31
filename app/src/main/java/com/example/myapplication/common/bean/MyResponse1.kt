package com.example.myapplication.common.bean

data class MyResponse1<T>(
    val msg: String?,
    val code: Int,
    val data: T,
)
