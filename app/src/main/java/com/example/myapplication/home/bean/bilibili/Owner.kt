package com.example.myapplication.home.bean.bilibili

import java.io.Serializable

data class Owner(
    val mid: Long,
    val name: String,
    val face: String
) : Serializable
