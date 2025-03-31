package com.example.myapplication.home.bean.bilibili

data class Data(
    val item: List<VideoProfile>,
    val business_card: String?,
    val floor_info: String?,
    val user_feature: String?,
    val preload_expose_pct: Float?,
    val preload_floor_expose_pct: Float?,
    val mid: Long?
)