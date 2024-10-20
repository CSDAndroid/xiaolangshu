package com.example.myapplication.data.searchVideoData

data class Result<T>(
    val resultType: String,
    val data: List<T>
)
