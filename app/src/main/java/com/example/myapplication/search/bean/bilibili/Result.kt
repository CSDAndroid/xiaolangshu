package com.example.myapplication.search.bean.bilibili

data class Result<T>(
    val resultType: String,
    val data: List<T>
)
